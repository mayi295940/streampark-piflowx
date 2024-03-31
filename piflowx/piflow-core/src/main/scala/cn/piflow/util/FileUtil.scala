package cn.piflow.util

import java.io.{File, PrintWriter}

import scala.io.Source

object FileUtil {

  def getJarFile(file: File): Array[File] = {
    val files = file
      .listFiles()
      .filter(!_.isDirectory)
      .filter(t => t.toString.endsWith(".jar")) // 此处读取.txt and .md文件
    files ++ file.listFiles().filter(_.isDirectory).flatMap(getJarFile)
  }

  def writeFile(text: String, path: String) = {

    val file = new File(path)
    if (!file.exists()) {
      file.createNewFile()
    }
    val writer = new PrintWriter(new File(path))
    writer.write(text)
    writer.close()
  }

  def readFile(path: String): String = {
    Source.fromFile(path).mkString("")
  }

  def main(args: Array[String]): Unit = {
    val classPath = PropertyUtil.getClassPath()

    val path = new File(classPath)
    getJarFile(path).foreach(println)

  }
}
