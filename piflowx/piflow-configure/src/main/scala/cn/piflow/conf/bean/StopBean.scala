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

package cn.piflow.conf.bean

import cn.piflow.conf.ConfigurableStop
import cn.piflow.conf.util.{ClassUtil, MapUtil}

class StopBean[DataType] {

  private var flowName: String = _
  var uuid: String = _
  var name: String = _
  var bundle: String = _
  var properties: Map[String, String] = _
  var customizedProperties: Map[String, String] = _

  def init(flowName: String, map: Map[String, Any]): Unit = {
    this.flowName = flowName
    this.uuid = MapUtil.get(map, "uuid").asInstanceOf[String]
    this.name = MapUtil.get(map, "name").asInstanceOf[String]
    this.bundle = MapUtil.get(map, "bundle").asInstanceOf[String]
    this.properties = MapUtil.get(map, "properties").asInstanceOf[Map[String, String]]
    if (map.contains("customizedProperties")) {
      this.customizedProperties =
        MapUtil.get(map, "customizedProperties").asInstanceOf[Map[String, String]]
    } else {
      this.customizedProperties = Map[String, String]()
    }

  }

  def constructStop(): ConfigurableStop[DataType] = {

    try {
      println("Construct stop: " + this.bundle + "!!!!!!!!!!!!!!!!!!!!!")
      val stop = ClassUtil.findConfigurableStop[DataType](this.bundle)
      println("Construct stop: " + stop + "!!!!!!!!!!!!!!!!!!!!!")

      println("properties is " + this.properties + "!!!!!!!!!!!!!!!")
      stop.setProperties(this.properties)
      stop.setCustomizedProperties(this.customizedProperties)
      stop
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        throw ex
    }
  }

}

object StopBean {

  def apply[DataType](flowName: String, map: Map[String, Any]): StopBean[DataType] = {
    val stopBean = new StopBean[DataType]()
    stopBean.init(flowName, map)
    stopBean
  }

}
