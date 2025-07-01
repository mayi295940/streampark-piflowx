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
import cn.piflow.bundle.spark.util.DataHandler
import cn.piflow.conf.{ConfigurableVisualizationStop, Language, Port, StopGroup, VisualizationType}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class PieChart extends ConfigurableVisualizationStop[Null, DataFrame, Null] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Show data with pie chart. "
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  // var x:String =_
  var dimension: String = _
  var indicator: String = _
  var indicatorOption: String = _

  override var visualizationType: String = VisualizationType.PieChart

  override def setProperties(map: Map[String, Any]): Unit = {
    // x=MapUtil.get(map,key="x").asInstanceOf[String]
    dimension = MapUtil.get(map, key = "dimension").asInstanceOf[String]
    indicator = MapUtil.get(map, key = "indicator").asInstanceOf[String]
    indicatorOption = MapUtil.get(map, key = "indicatorOption").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val dimension = new PropertyDescriptor()
      .name("dimension")
      .displayName("Dimension")
      .description("The dimension of pie chart")
      .defaultValue("")
      .example("type")
      .required(true)

    val indicator = new PropertyDescriptor()
      .name("indicator")
      .displayName("Indicator")
      .description("The indicator of pie chart")
      .defaultValue("")
      .example("project")
      .required(true)

    val indicatorOption = new PropertyDescriptor()
      .name("indicatorOption")
      .displayName("IndicatorOption")
      .description("The indicator option of pie chart")
      .allowableValues(Set("COUNT", "SUM", "AVG", "MAX", "MIN"))
      .defaultValue("COUNT")
      .example("COUNT")
      .required(true)

    descriptor = dimension :: descriptor
    descriptor = indicator :: descriptor
    descriptor = indicatorOption :: descriptor

    val customizedProperties = new PropertyDescriptor()
      .name("customizedProperties")
      .displayName("customizedProperties")
      .description("custom properties")
      .defaultValue("")
      .language(Language.CustomProperties)
      .required(false)
      .example("\"age\": \"COUNT\",\"id\": \"SUM\"")
    descriptor = customizedProperties :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/pie-chart.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Visualization)
  }

  override def initialize(ctx: ProcessContext[Null, DataFrame, Null]): Unit = {}

  override def perform(
      in: JobInputStream[Null, DataFrame, Null],
      out: JobOutputStream[Null, DataFrame, Null],
      pec: JobContext[Null, DataFrame, Null]): Unit = {

    val spark = pec.get[SparkSession]()
    val dataFrame = in.read()
    dataFrame.createOrReplaceTempView("PieChart")
    val sqlText =
      "select " + dimension + "," + indicatorOption + "(" + indicator + ") from PieChart group by " + dimension;
    println("PieChart Sql: " + sqlText)
    val pieChartDF = spark.sql(sqlText)

    val result = pieChartDF.repartition(1)

    val visualizationPath = s"${System.getProperty("java.io.tmpdir")}/visualization/" +
      s"${pec.getProcessContext.getProcess.pid()}/${pec.getStopJob.getStopName}"
    DataHandler.saveVisualizationData(visualizationPath, result)

    out.write(result)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
