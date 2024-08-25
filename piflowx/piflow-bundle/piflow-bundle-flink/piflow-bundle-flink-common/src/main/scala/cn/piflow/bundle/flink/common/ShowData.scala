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
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class ShowData extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "Show Data"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var showNumber: Int = _
  private var changeLog: Boolean = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val inputTable: Table = in.read()
    val resultTable = inputTable.limit(showNumber)

    if (!changeLog) {
      tableEnv.toDataStream(resultTable).print()
    } else {
      tableEnv.toChangelogStream(resultTable).print()
    }

    out.write(inputTable)
  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  // set customized properties of your Stop
  def setProperties(map: Map[String, Any]): Unit = {
    showNumber = MapUtil.get(map, "showNumber", "10").asInstanceOf[String].toInt
    changeLog = MapUtil.get(map, "changeLog", "false").asInstanceOf[String].toBoolean
  }

  // get descriptor of customized properties
  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val showNumber = new PropertyDescriptor()
      .name("showNumber")
      .displayName("showNumber")
      .description("The count to show.")
      .defaultValue("10")
      .required(true)
      .example("10")
    descriptor = showNumber :: descriptor

    val changeLog = new PropertyDescriptor()
      .name("changeLog")
      .displayName("changeLog")
      .description("change log data")
      .defaultValue("false")
      .allowableValues(Set("true", "false"))
      .required(false)
      .example("false")
    descriptor = changeLog :: descriptor

    descriptor
  }

  // get icon of Stop
  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ShowData.png")
  }

  // get group of Stop
  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
