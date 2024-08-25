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

import java.io.{File, FileInputStream}
import java.net.InetAddress
import java.util.Properties

object ServerIpUtil {
  private val prop: Properties = new Properties()
  var path: String = ""

  try {
    val userDir = System.getProperty("user.dir")
    path = userDir + "/server.ip"
    val file = new File(path)
    if (!file.exists()) {
      file.createNewFile()
    }
    val ip = InetAddress.getLocalHost.getHostAddress
    // write ip to server.ip file
    FileUtil.writeFile("server.ip=" + ip, ServerIpUtil.getServerIpFile())
    prop.load(new FileInputStream(path))
  } catch {
    case ex: Exception => ex.printStackTrace()
  }

  def getServerIpFile(): String = {
    path
  }

  def getServerIp(): String = {
    val obj = prop.get("server.ip")
    if (obj != null) {
      return obj.toString
    }
    null
  }

  def main(args: Array[String]): Unit = {

    val ip = InetAddress.getLocalHost.getHostAddress
    // write ip to server.ip file
    FileUtil.writeFile("server.ip=" + ip, ServerIpUtil.getServerIpFile())
    println(ServerIpUtil.getServerIp())
  }
}
