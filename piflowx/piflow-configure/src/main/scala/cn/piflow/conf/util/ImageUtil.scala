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

package cn.piflow.conf.util

import com.sksamuel
import com.sksamuel.scrimage
import com.sksamuel.scrimage.Image

import java.io.{BufferedInputStream, File}

object ImageUtil {

  System.setProperty("java.awt.headless", "true")

  def getImage(imagePath: String, bundle: String = ""): Array[Byte] = {
    if (bundle == "") {
      try {
        val classLoader = this.getClass.getClassLoader
        val imageInputStream = classLoader.getResourceAsStream(imagePath)
        val input = new BufferedInputStream(imageInputStream)
        Image.fromStream(input).bytes(sksamuel.scrimage.writer)
      } catch {
        case ex: Exception => {
          println(ex);
          Array[Byte]()
        }
      }
    } else {
      val pluginManager = PluginManager.getInstance
      pluginManager.getConfigurableStopIcon(imagePath, bundle)
    }
  }

  def saveImage(imageBytes: Array[Byte], savePath: String) = {
    Image(imageBytes).output(new File(savePath))(scrimage.writer)
  }

}
