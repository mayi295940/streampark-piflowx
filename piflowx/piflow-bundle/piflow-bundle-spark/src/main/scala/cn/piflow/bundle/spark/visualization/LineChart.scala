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

class LineChart extends ConfigurableVisualizationStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Show data with scatter plot. " +
    "The ordinate is represented by customizedProperties, " +
    "the key of customizedProperty is the dimentsion, " +
    "and the value of customizedProperty is the operation for the dimension, " +
    "such as COUNT/SUM/AVG/MAX/MIN."

  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var abscissa: String = _

  override var visualizationType: String = VisualizationType.LineChart
  override val isCustomized: Boolean = true
  override val customizedAllowValue: List[String] = List("COUNT", "SUM", "AVG", "MAX", "MIN")

  override def setProperties(map: Map[String, Any]): Unit = {
    abscissa = MapUtil.get(map, key = "abscissa").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val abscissa = new PropertyDescriptor()
      .name("abscissa")
      .displayName("Abscissa")
      .description("The abscissa  of line chart")
      .defaultValue("")
      .example("year")
      .required(true)

    descriptor = abscissa :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/line-chart.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Visualization)
  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val sqlContext = spark.sqlContext
    val dataFrame = in.read()
    dataFrame.createOrReplaceTempView("LineChart")

    if (this.customizedProperties != null || this.customizedProperties.nonEmpty) {

      println("dimension is " + this.customizedProperties.keySet.mkString(",") + "!!!!!!!!!!!!!!!")

      var dimensionActionArray = List[String]()
      val it = this.customizedProperties.keySet.iterator
      while (it.hasNext) {
        val dimention = it.next()
        val action = MapUtil.get(this.customizedProperties, dimention).asInstanceOf[String]
        val dimentionAction = action + "(" + dimention + ") as " + dimention + "_" + action
        dimensionActionArray = dimensionActionArray :+ dimentionAction
      }

      val sqlText = "select " + abscissa + "," + dimensionActionArray.mkString(
        ",") + " from LineChart group by " + abscissa;
      println("LineChart Sql: " + sqlText)
      val lineChartDF = spark.sql(sqlText)
      out.write(lineChartDF.repartition(1))
    } else {
      out.write(dataFrame)
    }
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}