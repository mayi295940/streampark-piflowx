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

import java.io.{FileInputStream, InputStream}
import java.util.Properties

object PropertyUtil {
  private val prop: Properties = new Properties()
  var fis: InputStream = null
  var path: String = ""
  var classPath: String = ""
  var scalaPath: String = ""
  var sparkJarPath: String = ""

  val NOT_EXIST_FLAG = 0
  val EXIST_FLAG = 1

  try {
    // val path = Thread.currentThread().getContextClassLoader.getResource("config.properties").getPath
    val userDir = System.getProperty("user.dir")
    path = userDir + "/config.properties"
    prop.load(new FileInputStream(path))
    classPath = userDir + "/classpath/"
    scalaPath = userDir + "/scala/"
    sparkJarPath = userDir + "/sparkJar/"
  } catch {
    case ex: Exception => ex.printStackTrace()
  }

  def getConfigureFile(): String = {
    path
  }

  def getClassPath(): String = {
    classPath
  }

  def getScalaPath(): String = {
    scalaPath
  }

  def getSpartJarPath(): String = {
    sparkJarPath
  }

  def getVisualDataDirectoryPath(): String = {
    val item = "visualDataDirectoryHdfsPath"
    val hdfsFS = PropertyUtil.getPropertyValue("fs.defaultFS")
    val visualDataDirectoryHdfsPath = hdfsFS + "/user/piflow/visualDataDirectoryPath/"

    val isPluginHdfsPathExist = H2Util.getFlag(item)
    if (isPluginHdfsPathExist == NOT_EXIST_FLAG) {
      if (!HdfsUtil.exists(hdfsFS, visualDataDirectoryHdfsPath)) {
        HdfsUtil.mkdir(hdfsFS, visualDataDirectoryHdfsPath)
      }
      H2Util.addFlag(item, EXIST_FLAG)
    }
    visualDataDirectoryHdfsPath
  }

  def getPropertyValue(propertyKey: String): String = {
    val obj = prop.get(propertyKey)
    if (obj != null) {
      return obj.toString
    }
    null
  }

  def getIntPropertyValue(propertyKey: String): Int = {
    val obj = prop.getProperty(propertyKey)
    if (obj != null) {
      return obj.toInt
    }
    throw new NullPointerException
  }

}
