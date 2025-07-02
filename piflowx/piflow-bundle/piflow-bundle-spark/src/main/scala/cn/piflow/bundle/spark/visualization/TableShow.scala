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

import cn.piflow._
import cn.piflow.bundle.spark.util.DataHandler
import cn.piflow.conf.{ConfigurableVisualizationStop, Port, StopGroup, VisualizationType}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.spark.sql.{DataFrame, SparkSession}

class TableShow extends ConfigurableVisualizationStop[Null, DataFrame, Null] {

  override var visualizationType: String = VisualizationType.Table
  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Show data with table"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var showField: String = _
  private var showNumber: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    showField = MapUtil.get(map, key = "showField").asInstanceOf[String]
    MapUtil.get(map, "showNumber", "-1") match {
      case str: String =>
        try {
          showNumber = str.toInt
        } catch {
          case _: NumberFormatException =>
            showNumber = -1
            println("Failed to convert showNumber to Int. Using default value -1.")
        }
      case _ =>
        showNumber = -1
        println("showNumber is not a String. Using default value -1.")
    }
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

    val showNumber = new PropertyDescriptor()
      .name("showNumber")
      .displayName("showNumber")
      .description("The count to show.")
      .required(false)
      .defaultValue("")
      .example("10")
    descriptor = showNumber :: descriptor

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

  override def initialize(ctx: ProcessContext[Null, DataFrame, Null]): Unit = {}

  override def perform(
      in: JobInputStream[Null, DataFrame, Null],
      out: JobOutputStream[Null, DataFrame, Null],
      pec: JobContext[Null, DataFrame, Null]): Unit = {

    val spark = pec.get[SparkSession]()
    val dataFrame = in.read()

    val inputTempViewName = s"${getClass.getSimpleName.stripSuffix("$")}_${IdGenerator.uuidWithoutSplit}"

    dataFrame.createOrReplaceTempView(inputTempViewName)

    var sqlText = s"select $showField from $inputTempViewName"
    if (showNumber > 0) {
      sqlText = sqlText + " limit " + showNumber
    }

    println("TableShow Sql: " + sqlText)
    val tableShowDF = spark.sql(sqlText)
    val result = tableShowDF.repartition(1)

    val visualizationPath = s"${System.getProperty("java.io.tmpdir")}/visualization/" +
      s"${pec.getProcessContext.getProcess.pid()}/${pec.getStopJob.getStopName}"
    DataHandler.saveVisualizationData(visualizationPath, result)

    out.write(result)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
