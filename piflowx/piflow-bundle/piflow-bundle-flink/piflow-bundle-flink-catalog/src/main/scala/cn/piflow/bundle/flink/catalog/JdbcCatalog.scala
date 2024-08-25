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
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class JdbcCatalog extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "通过JDBC协议将Flink连接到关系数据库,目前支持Postgres Catalog和MySQL Catalog。"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var catalogName: String = _
  private var databaseType: String = _
  private var ip: String = _
  private var port: Int = _
  private var defaultDatabase: String = _
  private var username: String = _
  private var password: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val ddl =
      s"""CREATE CATALOG $catalogName WITH (
         |'type' = 'jdbc',
         |'default-database' = '$defaultDatabase',
         |'username' = '$username',
         |'password' = '$password',
         |'base-url' = 'jdbc:$databaseType://$ip:$port'
         |)
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    tableEnv.executeSql(ddl)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    catalogName = MapUtil.get(map, "catalogName").asInstanceOf[String]
    databaseType = MapUtil.get(map, "databaseType").asInstanceOf[String]
    ip = MapUtil.get(map, "ip").asInstanceOf[String]
    port = MapUtil.get(map, "port").asInstanceOf[String].toInt
    defaultDatabase = MapUtil.get(map, "defaultDatabase").asInstanceOf[String]
    username = MapUtil.get(map, "username").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val catalogName = new PropertyDescriptor()
      .name("catalogName")
      .displayName("CatalogName")
      .description("catalog名称。")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("my_catalog")
    descriptor = catalogName :: descriptor

    val databaseType = new PropertyDescriptor()
      .name("databaseType")
      .displayName("DatabaseType")
      .description("Postgres Catalog或MySQL Catalog。")
      .defaultValue("")
      .allowableValues(Set("postgresql", "mysql"))
      .required(true)
      .order(2)
      .example("mysql")
    descriptor = databaseType :: descriptor

    val ip = new PropertyDescriptor()
      .name("ip")
      .displayName("ip")
      .description("数据库ip。")
      .defaultValue("")
      .required(true)
      .order(3)
      .example("127.0.0.1")
    descriptor = ip :: descriptor

    val port = new PropertyDescriptor()
      .name("port")
      .displayName("port")
      .description("数据库端口。")
      .defaultValue("")
      .required(true)
      .order(4)
      .example("3306")
    descriptor = port :: descriptor

    val defaultDatabase = new PropertyDescriptor()
      .name("defaultDatabase")
      .displayName("DefaultDatabase")
      .description("默认要连接的数据库。")
      .defaultValue("")
      .required(true)
      .order(5)
      .example("my_database")
    descriptor = defaultDatabase :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("username")
      .description("账户的用户名。")
      .defaultValue("")
      .required(true)
      .order(6)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("password")
      .description("账户的密码。")
      .defaultValue("")
      .sensitive(true)
      .required(true)
      .order(7)
      .example("123456")
    descriptor = password :: descriptor

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
