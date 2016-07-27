/*
아래 참고하세요.
[학습목표]
ㆍ앞으로의 수업에서 뭘 어떻게 배워야 할 지 알아본다.
ㆍ0과 1을 구분할 줄 안다.
ㆍ논리적, 과학적 사고 방식을 이해하고 흉내내본다.
[과제]
B1. 폴더명을 입력받아 폴더안에 저장된 파일이름을 얻고 결과를 정렬하여
        출력하는 코드를 작성하시오.
[개발 과제 제출 시 유의점]
ㆍ사용언어 : 편한대로 할 것이나, 그 언어를 선택한 이유를 쓸 것('익숙해서'도 충분한 이유임)
ㆍ어떤 언어를 쓰던지 주석을 주요 단계별로 간단하게 명기할 것
ㆍ실행 결과도 같이 첨부 -> 입력값, 출력값 명시(단, 입출력값이 너무 많을 경우 요약하여 기술할 것)
[그 외]
ㆍ향 후 수업 진행의 궁금한 점, 바라는 점 준비할 것(첫 날외엔 말할 기회가 없을수 있음)
ㆍ트랙별 반장은 페북 채팅 걸어주세요. 제출 이메일 주소를 알려드릴테니 별도로 공유해주세요.
여유로운 주말보내고, 그 날 뵙겠습니다

*/

import java.io.File

print("[*] Directory name : ")
val dirName = readLine() // input directory name

val f = new File(dirName) // make directory structure
if (!f.exists) { // if not exists -> println and exit process
  println("[*] Invalid directory name.")
  System.exit(1)
}

val list = f.listFiles // make directory's file lists
val (dir, files) = list.partition(_.isDirectory) // split directory and file list

val sortedDir = dir.sorted // sort the list
val sortedFiles = files.sorted

println("[*] Directories :")
println(sortedDir.mkString("\n") + "\n") //print with \n in each elements

println("[*] Files : ")
println(sortedFiles.sorted.mkString("\n"))
