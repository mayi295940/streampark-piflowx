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

package cn.piflow.bundle.flink.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.{Table, TableEnvironment, TableResult}
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

class SQLExecute extends ConfigurableStop[Null, Table, Null] {

  val authorEmail: String = ""
  val description: String = "execute sql"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var useTableEnv: Boolean = false
  private var sql: String = _

  override def perform(
      in: JobInputStream[Null, Table, Null],
      out: JobOutputStream[Null, Table, Null],
      pec: JobContext[Null, Table, Null]): Unit = {

    var tableEnv: TableEnvironment = null
    if (useTableEnv) {
      tableEnv = pec.get[TableEnvironment]()
    } else {
      tableEnv = pec.get[StreamTableEnvironment]()
    }

    if (StringUtils.isNotEmpty(sql)) {
      sql
        .split(Constants.SEMICOLON)
        .map(t => {
          val result: TableResult = tableEnv.executeSql(t)
          result.print()
        })
    }

  }

  override def setProperties(map: Map[String, Any]): Unit = {
    useTableEnv = MapUtil.get(map, "useTableEnv", "false").asInstanceOf[String].toBoolean
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
  }

  override def initialize(ctx: ProcessContext[Null, Table, Null]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val useTableEnv = new PropertyDescriptor()
      .name("useTableEnv")
      .displayName("useTableEnv")
      .description(
        "是否使用TableEnvironment，如果为true，则使用TableEnvironment，否则使用StreamTableEnvironment。在Gravitino组件中创建的是TableEnvironment，后续组件如果需要和Gravitino集成，需设置为true。")
      .defaultValue("false")
      .required(false)
      .allowableValues(Set("true", "false"))
      .order(1)
      .example("false")
    descriptor = useTableEnv :: descriptor

    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("sql语句，多行sql以';'分隔")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .example("select * from temp;show tables;")
    descriptor = sql :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ExecuteSqlStop.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
