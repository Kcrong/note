import scala.io.Source
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

object Token {
  private val remark = "//(.*)".r
  private val pass = "(return|continue|break).*".r
  private val cond = "(if|while|for)[\\s]*\\(([^\\)]+)\\)(.*)".r
  private val condElse = "else(.*)".r
  private val preproc = "[^#]*#(include|define|pragma|ifdef|undef|endif).*".r //#{include} <stdio.h>
  private val defn = "[^\\s]+[\\s]*([^\\(]+)[\\s]*\\(([^\\)]*)\\).*".r //int {predef}({char *addr});
  private val defv = "([^\\s]+)[\\s]*([^;]+).*".r //{int} {data};
  private val defa = "([^\\s]+)[\\s]*([^=]+)=([^;]+).*".r //{int} {data} = {value};
  private val assign = "([^=]+)=([^;]+).*".r // {data} = {value};
  private val reassign = "([^\\=\\+\\-\\%\\^\\&\\*\\/\\!\\>\\<]+)([\\+\\-\\%\\^\\&\\*\\/\\|\\>\\<]+)=([^;]+).*".r
  private val func = "([^\\(]+)\\(([^\\)]*)\\).*".r // {function}({param})
  private val blank = "([ \\s\\{\\}]+)".r

  def tokenizer(tList: ListBuffer[String]) =
    (str: String) => str.trim match {
      case pass(_) => ""
      case remark(_) => ""
      case preproc(_) => ""
      case cond(typed, con, res) =>
        tList += con
        tList += res
        ""
      case condElse(res) =>
        tList += res.mkString
        ""
      case func(name, params) => name + ":" + params
      case reassign(name, op, value) =>
        " #" + name + "#" + name.trim + " " + op.trim + " (" + value.trim + ")"
      case assign(name, value) if (!name.contains("(") && !name.trim.split("\\[")(0).contains(" ")) =>
        " #" + name + "#" + value
      case defa(typed, name, assign) => typed + "#" + name + "#" + assign
      case defv(typed, name) =>
        typed + "#" + name + "# "
      case defn(_, _) => ""
      case blank(_) => ""
      case _ => ""
    }

  def extract_token(lines: Iterator[String]) = {
    var token_list = Array[String]()

    def run(list: List[String]) {
      var tList = ListBuffer[String]()
      val Tokenizer = tokenizer(tList)

      val tmp = for (i <- list) yield Tokenizer(i.trim)
      token_list ++= tmp
      if (tList.size != 0) run(tList.toList)
    }

    run(lines.toList)
    token_list
  }
}

object Process {
  def extract_function(proc: Array[String]) = {
    val vuln = ".*(read|cpy|cat|scan|sprint|snprint|gets).*".r
    val call_list = proc.zipWithIndex.map {
      case (str, index) => (index, str.split(":").map(_.trim))
    }

    call_list.filter {
      case (index, Array(name, param)) =>
        try { val vuln(data) = name; true }
        catch { case e: Throwable => false }
      case _ => false
    }.toList
  }

  def extract_value(proc: Array[String]) = {
    val value_list = proc.zipWithIndex.map {
      case (str, index) => (index, str.split("#").map(_.trim))
    }.filter {
      case (index, Array(a, b, c)) if (b(0).isLetter || b.startsWith("_") || b.startsWith("*")) => true
      case _ => false
    }

    var table = Map[String, List[(Int, Array[String])]]()
    for ((index, arr) <- value_list) {
      val list = arr(1).split("\\[")(0).split(",")
      val name = list(list.length - 1).replace("*", "").trim

      try table(name) ::= (index, arr)
      catch { case _: Throwable => table += name -> List((index, arr)) }
    }

    table.map { case (a, b) => (a, b.reverse)}
  }

  def bof_analysis(
      function_call: List[(Int, Array[String])],
      value_list: Map[String, List[(Int, Array[String])]]) {
    for ((index, Array(name, param)) <- function_call) {
      println(index + " : " + name + "(" + param + ")")
      if (param.contains(","))
        for (i <- param.split(","))
          try {
            for ((ndex, Array(typed, name, value)) <- value_list(i.trim)) {
              val assign = if (value != "") " = " + value else value
              println("\t" + ndex + " : " + typed + " " + name + assign)
            }
          } catch { case _: Throwable => }
    }
  }
}

object extractor extends App {
  print("[*] file name : ")
  val fname = readLine

  val f = try Source.fromFile(fname) catch { case _: Throwable => null }
  if (f == null) {
    println("[*] file does not exist.")
    System.exit(-1)
  }

  val proc = Token.extract_token(f.getLines)
  val function_call = Process.extract_function(proc)
  val value_list = Process.extract_value(proc)

  val res = Process.bof_analysis(function_call, value_list)
}
