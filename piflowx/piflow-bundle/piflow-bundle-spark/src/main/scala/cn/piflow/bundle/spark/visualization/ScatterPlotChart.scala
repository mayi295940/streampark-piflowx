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

class ScatterPlotChart extends ConfigurableVisualizationStop[DataFrame] {

  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Show data with scatter plot chart." +
    "Dimension is represented by customizedProperties, " +
    "the key of customizedProperty is the dimentsion column, and the value is the index of the dimension. " +
    "The first index represent abscissa, the second index represent ordinate. "
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var legend: String = _

  override var visualizationType: String = VisualizationType.ScatterPlot
  override val isCustomized: Boolean = true

  // override val customizedAllowValue: List[String] = List("COUNT","SUM","AVG","MAX","MIN")
  override def setProperties(map: Map[String, Any]): Unit = {
    legend = MapUtil.get(map, key = "legend").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val abscissa = new PropertyDescriptor()
      .name("legend")
      .displayName("Legend")
      .description("The legend  of bubble chart")
      .defaultValue("")
      .example("year")
      .required(true)

    descriptor = abscissa :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/scatter-plot.png")
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
    val dataFrame = in.read()
    dataFrame.createOrReplaceTempView("ScatterPlot")

    if (this.customizedProperties != null || this.customizedProperties.nonEmpty) {

      println("dimension is " + this.customizedProperties.keySet.mkString(",") + "!!!!!!!!!!!!!!!")

      val dimensionArray = List(this.customizedProperties.toSeq.sortBy(_._2): _*).map(x => x._1)
      println("ordered dimension is " + dimensionArray.mkString(",") + "!!!!!!!!!!!!!!!")

      val sqlText = "select " + legend + "," + dimensionArray.mkString(
        ",") + " from ScatterPlot order by " + legend + "," + dimensionArray(0);
      println("ScatterPlot Sql: " + sqlText)
      val scatterPlottDF = spark.sql(sqlText)
      out.write(scatterPlottDF.repartition(1))
    } else {
      out.write(dataFrame)
    }
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
