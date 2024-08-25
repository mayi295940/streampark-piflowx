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

class Distinct extends ConfigurableStop[DataFrame] {
  override val authorEmail: String = "yangqidong@cnic.cn"
  override val description: String =
    "Duplicate based on the specified column name or all column names"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var columnNames: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    columnNames = MapUtil.get(map, "columnNames").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val fields = new PropertyDescriptor()
      .name("columnNames")
      .displayName("ColumnNames")
      .description(
        "Fill in the column names you want to duplicate,multiple columns names separated by commas,if not,all the columns will be deduplicated")
      .defaultValue("")
      .required(false)
      .example("id")
    descriptor = fields :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val inDf: DataFrame = in.read()
    var outDf: DataFrame = null

    if (columnNames.nonEmpty) {
      val fileArr: Array[String] = columnNames.split(",")
      outDf = inDf.dropDuplicates(fileArr)
    } else {
      outDf = inDf.distinct()
    }
    out.write(outDf)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
