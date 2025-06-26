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

package cn.piflow.bundle.spark.gravitino

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class Gravitino extends ConfigurableStop[Null, DataFrame, Null] {

  override val authorEmail: String = ""
  override val description: String = "Apache Gravitino Spark连接器。(注：该连接器会停止初始化的sparkSession,重新创建)"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var metalake: String = _
  private var gravitinoUri: String = _
  private var enableLineage: Boolean = _

  override def perform(
      in: JobInputStream[Null, DataFrame, Null],
      out: JobOutputStream[Null, DataFrame, Null],
      pec: JobContext[Null, DataFrame, Null]): Unit = {

    val spark = pec.get[SparkSession]()
    val appName = spark.sparkContext.appName
    val conf = spark.sparkContext.getConf

    spark.stop()

    conf.set("spark.plugins", "org.apache.gravitino.spark.connector.plugin.GravitinoSparkPlugin")
    conf.set("spark.sql.gravitino.uri", gravitinoUri)
    conf.set("spark.sql.gravitino.metalake", metalake)
    // conf.set("spark.sql.gravitino.enableIcebergSupport", "true")

    if (enableLineage) {
      conf.set("spark.extraListeners", "io.openlineage.spark.agent.OpenLineageSparkListener")
      conf.set("spark.openlineage.transport.type", "http")
      conf.set("spark.openlineage.transport.url", gravitinoUri)
      conf.set("spark.openlineage.transport.endpoint", "/api/lineage")
      conf.set("spark.openlineage.namespace", metalake)
      conf.set("spark.openlineage.appName", appName)
      conf.set("spark.openlineage.columnLineage.datasetLineageEnabled", "true")
    }

    val sparkSessionBuilder = SparkSession.builder().appName(appName)
    val sparkSession = sparkSessionBuilder.appName(appName).config(conf).getOrCreate()
    pec.getProcessContext.put(classOf[SparkSession].getName, sparkSession)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    metalake = MapUtil.get(map, "metalake").asInstanceOf[String]
    gravitinoUri = MapUtil.get(map, "gravitinoUri").asInstanceOf[String]
    enableLineage = MapUtil.get(map, "enableLineage", "false").asInstanceOf[String].toBoolean
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val catalogName = new PropertyDescriptor()
      .name("metalake")
      .displayName("metalake")
      .description("The metalake name that flink connector used to request to Gravitino.")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("test")
    descriptor = catalogName :: descriptor

    val gravitinoUri = new PropertyDescriptor()
      .name("gravitinoUri")
      .displayName("gravitinoUri")
      .description("The uri of Gravitino server address.")
      .defaultValue("")
      .required(true)
      .order(2)
      .example("http://localhost:8090")
    descriptor = gravitinoUri :: descriptor

    val enableLineage = new PropertyDescriptor()
      .name("enableLineage")
      .displayName("enableLineage")
      .description("enable lineage.")
      .defaultValue("false")
      .allowableValues(Set("true", "false"))
      .required(false)
      .order(2)
      .example("false")
    descriptor = enableLineage :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/MysqlRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CatalogGroup)
  }

  override def initialize(ctx: ProcessContext[Null, DataFrame, Null]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_SPARK

}
