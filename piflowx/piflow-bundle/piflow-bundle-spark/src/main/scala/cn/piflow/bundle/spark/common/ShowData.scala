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

package cn.piflow.bundle.spark.common

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.DataFrame

class ShowData extends ConfigurableStop[DataFrame] {

  // the email of author
  val authorEmail: String = "xjzhu@cnic.cn"
  // the description of Stop
  val description: String = "Show Data"
  // the inport list of Stop
  val inportList: List[String] = List(Port.DefaultPort)
  // the outport list of Stop
  val outportList: List[String] = List(Port.DefaultPort)

  // the customized properties of your Stop
  private var showNumber: String = _

  // core logic function of Stop
  // read data by "in.read(inPortName)", the default port is ""
  // write data by "out.write(data, outportName)", the default port is ""
  def perform(in: JobInputStream[DataFrame], out: JobOutputStream[DataFrame], pec: JobContext[DataFrame]): Unit = {

    val df = in.read()
    df.show(showNumber.toInt)
    out.write(df)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  // set customized properties of your Stop
  def setProperties(map: Map[String, Any]): Unit = {
    showNumber = MapUtil.get(map, "showNumber").asInstanceOf[String]
  }

  // get descriptor of customized properties
  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val showNumber = new PropertyDescriptor()
      .name("showNumber")
      .displayName("showNumber")
      .description("The count to show.")
      .defaultValue("")
      .required(false)
      .example("10")
    descriptor = showNumber :: descriptor
    descriptor
  }

  // get icon of Stop
  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ShowData.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
