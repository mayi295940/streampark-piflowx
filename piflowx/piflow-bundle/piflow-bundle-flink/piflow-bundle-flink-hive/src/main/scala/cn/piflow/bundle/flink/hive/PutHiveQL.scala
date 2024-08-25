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

package cn.piflow.bundle.flink.hive

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.HdfsUtil
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.catalog.hive.HiveCatalog

class PutHiveQL extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  override val description: String = "Execute hiveQL script"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var database: String = _
  var hiveQL_Path: String = _

  val name = "myhive"
  val defaultDatabase = "mydatabase"
  val hiveConfDir = "/piflow-configure/hive-conf"

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val hive = new HiveCatalog(name, defaultDatabase, hiveConfDir)

    tableEnv.registerCatalog("myhive", hive)

    tableEnv.useCatalog("myhive")

    import org.apache.flink.table.api.SqlDialect
    tableEnv.getConfig.setSqlDialect(SqlDialect.HIVE)

    tableEnv.useDatabase(database)

    val sqlString: String = HdfsUtil.getLines(hiveQL_Path)
    sqlString
      .split(Constants.SEMICOLON)
      .foreach(s => {
        println("Sql is " + s)
        tableEnv.executeSql(s)
      })

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    hiveQL_Path = MapUtil.get(map, "hiveQL_Path").asInstanceOf[String]
    database = MapUtil.get(map, "database").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {

    var descriptor: List[PropertyDescriptor] = List()

    val hiveQL_Path = new PropertyDescriptor()
      .name("hiveQL_Path")
      .displayName("HiveQL_Path")
      .description("The hdfs path of the hiveQL file")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("hdfs://192.168.3.138:8020/test/PutHiveQL.hiveql")
    descriptor = hiveQL_Path :: descriptor

    val database = new PropertyDescriptor()
      .name("database")
      .displayName("DataBase")
      .description("The database name which the hiveQL will execute on")
      .defaultValue("")
      .required(true)
      .order(2)
      .example("test")
    descriptor = database :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/hive/PutHiveQL.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.HiveGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK
}
