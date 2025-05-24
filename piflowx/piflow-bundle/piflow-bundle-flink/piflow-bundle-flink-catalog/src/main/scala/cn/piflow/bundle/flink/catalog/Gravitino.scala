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

package cn.piflow.bundle.flink.catalog

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.flink.configuration.Configuration
import org.apache.flink.table.api.{EnvironmentSettings, Table, TableEnvironment}

class Gravitino extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Apache Gravitino Flink 连接器。"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var metalake: String = _
  private var gravitinoUri: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val configuration = new Configuration()
    configuration.setString("table.catalog-store.kind", "gravitino")
    configuration.setString("table.catalog-store.gravitino.gravitino.metalake", metalake)
    configuration.setString("table.catalog-store.gravitino.gravitino.uri", gravitinoUri)
    val builder = EnvironmentSettings.newInstance().withConfiguration(configuration)
    val tableEnv = TableEnvironment.create(builder.inStreamingMode().build())
    pec.getProcessContext.put(classOf[TableEnvironment].getName, tableEnv)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    metalake = MapUtil.get(map, "metalake").asInstanceOf[String]
    gravitinoUri = MapUtil.get(map, "gravitinoUri").asInstanceOf[String]
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

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/catalog/JdbcCatalog.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CatalogGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
