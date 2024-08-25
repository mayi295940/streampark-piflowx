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

package cn.piflow.bundle.spark.visualization

import cn.piflow.{Constants, JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableVisualizationStop, Port, StopGroup, VisualizationType}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class TableShow extends ConfigurableVisualizationStop[DataFrame] {

  override var visualizationType: String = VisualizationType.Table
  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Show data with table"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var showField: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    showField = MapUtil.get(map, key = "showField").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val showField = new PropertyDescriptor()
      .name("showField")
      .displayName("ShowField")
      .description("The fields  of data to show.")
      .defaultValue("*")
      .example("id,name,age")
      .required(true)

    descriptor = showField :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/table.png")
  }

  override def getGroup(): List[String] = {
    List {
      StopGroup.Visualization
    }
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val dataFrame = in.read()
    dataFrame.createOrReplaceTempView("TableShow")
    val sqlText = "select " + showField + " from TableShow"
    println("TableShow Sql: " + sqlText)
    val tableShowDF = spark.sql(sqlText)
    out.write(tableShowDF.repartition(1))
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
