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

package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class Route extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Route data by custom properties,key is port,value is filter"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.RoutePort)

  override val isCustomized: Boolean = true

  override def setProperties(map: Map[String, Any]): Unit = {}

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable = in.read()

    val tmpTable = this.getClass.getSimpleName
      .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit
    tableEnv.createTemporaryView(tmpTable, inputTable)

    if (this.customizedProperties != null || this.customizedProperties.nonEmpty) {
      val keyIterator = this.customizedProperties.keySet.iterator
      while (keyIterator.hasNext) {
        val port = keyIterator.next()
        val filterCondition = MapUtil.get(this.customizedProperties, port).asInstanceOf[String]
        val resultTable = tableEnv.sqlQuery(s"SELECT * FROM $tmpTable WHERE $filterCondition")
        out.write(port, resultTable)
      }
    }

    out.write(inputTable)

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val customizedProperties = new PropertyDescriptor()
      .name("customizedProperties")
      .displayName("customizedProperties")
      .description("custom properties,key is port,value is filter")
      .defaultValue("")
      .required(true)
      .example(
        "\"port1\": \"author = \\\"Carmen Heine\\\"\",\n\"port2\": \"author = \\\"Gerd Hoff\\\"\"")

    descriptor = customizedProperties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Fork.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
