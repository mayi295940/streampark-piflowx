/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
