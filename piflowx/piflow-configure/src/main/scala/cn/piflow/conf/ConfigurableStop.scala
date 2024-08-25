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

package cn.piflow.conf

import cn.piflow.Stop
import cn.piflow.conf.bean.PropertyDescriptor

abstract class ConfigurableStop[DataType] extends Stop[DataType] {

  val authorEmail: String
  val description: String
  // PortEnum.AnyPort: any port; PortEnum.DefaultPort: default port
  // null: no port; userdefinePort: port1, port2...
  val inportList: List[String] // = List(PortEnum.DefaultPort.toString)
  val outportList: List[String] // = List(PortEnum.DefaultPort.toString)

  // Have customized properties or not
  val isCustomized = false
  val customizedAllowKey = List[String]()
  val customizedAllowValue = List[String]()

  var customizedProperties: Map[String, String] = _

  def setProperties(map: Map[String, Any]): Unit

  def getPropertyDescriptor(): List[PropertyDescriptor]

  def getIcon(): Array[Byte]

  def getGroup(): List[String]

  def setCustomizedProperties(customizedProperties: Map[String, String]): Unit = {
    this.customizedProperties = customizedProperties
  }

  def getCustomized(): Boolean = {
    this.isCustomized
  }

  def getEngineType: String

}
