import scala.io.Source
import java.io._

object Dbg {
  def DebugTime[T: Manifest](dat: Array[T], block: Array[T] => Array[T]) = {
    val newDat = new Array[T](dat.size)
    Array.copy(dat, 0, newDat, 0, dat.size)

    val t0 = System.nanoTime()
    val result = block(newDat)
    val t1 = System.nanoTime()

    (result, t1 - t0)
  }

  def DebugTime[T](dat: T, block: T => T) = {
    val t0 = System.nanoTime()
    val result = block(dat)
    val t1 = System.nanoTime()

    (result, t1 - t0)
  }
}

object Sort {
  def bubbleSort[T](f: (T, T) => Boolean)(dat: Array[T]) = {
    val len = dat.size - 1
    for (i <- 0 until len; j <- 0 until len - i) {
      if (f(dat(j + 1), dat(j))) {
         val temp = dat(j)
         dat(j) = dat(j + 1)
         dat(j + 1) = temp
      }
    }
    dat
  }

  def mergeSort[T](f: (T, T) => Boolean)(dat: List[T]): List[T] = {
    def merge(left: List[T], right: List[T]): List[T] =
      if (left.isEmpty) right
      else if (right.isEmpty) left
      else if (f(left.head, right.head)) left.head :: merge(left.tail, right)
      else right.head :: merge(left, right.tail)
    val n = dat.length/2
    if (n == 0) dat
    else merge(mergeSort(f)(dat take n), mergeSort(f)(dat drop n))
  }

  def quickSort[T](f: (T, T) => Boolean)(dat: List[T]): List[T] = dat match {
    case Nil => Nil
    case pivot :: tail => {
      val (fTrue: List[T], fFalse: List[T]) = tail.partition(f(_, pivot))
      quickSort(f)(fTrue) ::: pivot :: quickSort(f)(fFalse)
    }
  }
}

object yk_main extends App {
  print("data file : ")
  val name = readLine()

  val f = Source.fromFile(name)
  val dat = (for (i <- f.getLines) yield i.toInt).toList

  val bsort = Sort.bubbleSort((a: Int, b: Int) => a < b)(_)
  val (bresult, btime) = Dbg.DebugTime(dat.toArray, bsort)

  val msort = Sort.mergeSort((a: Int, b: Int) => a < b)(_)
  val (mresult, mtime) = Dbg.DebugTime(dat, msort)

  val qsort = Sort.quickSort((a: Int, b: Int) => a < b)(_)
  val (qresult, qtime) = Dbg.DebugTime(dat, qsort)

  var str = ""
  str += "[*] Sorting result\n\n"
  str += "bubble sort\tmerge sort\tquick sort\n"
  str += btime + "ns\t" + mtime + "ns\t" + qtime + "ns\n\n"
  str += "original data\t: " + dat.mkString(" ") + "\n"
  str += "bubble sort\t: " + bresult.mkString(" ") + "\n"
  str += "merge sort\t: " + mresult.mkString(" ") + "\n"
  str += "quick sort\t: " + qresult.mkString(" ") + "\n"

  println(str)

  val pw = new PrintWriter(new File("SortingResult.txt"))
  pw.write(str)
  pw.close
}
