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

package cn.piflow.bundle.flink.jdbc

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.enums.DataBaseType
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table

import java.sql.{Connection, DriverManager, Statement}

class JDBCExecuteSql extends ConfigurableStop[Table] {

  val authorEmail: String = ""
  val description: String = "使用JDBC驱动执行关系型数据库SQL"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  private var url: String = _
  private var driver: String = _
  private var username: String = _
  private var password: String = _
  private var sql: String = _

  def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    if (StringUtils.isNotEmpty(sql)) {
      sql = sql.replaceAll("\n|\t", "")
      var connection: Connection = null
      var statement: Statement = null
      try {
        Class.forName(driver)
        connection = DriverManager.getConnection(url, username, password)
        statement = connection.createStatement()
        sql
          .split(Constants.SEMICOLON)
          .map(t => {
            statement.execute(t)
          })
      } catch {
        case e: Exception => e.printStackTrace()
      } finally {
        if (statement != null) {
          statement.close()
        }
        if (connection != null) {
          connection.close()
        }
      }
    }

  }

  def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map, "url").asInstanceOf[String]
    driver = MapUtil.get(map, "driver", "").asInstanceOf[String]
    username = MapUtil.get(map, "username", "").asInstanceOf[String]
    password = MapUtil.get(map, "password", "").asInstanceOf[String]
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("JDBC数据库url")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("jdbc:mysql://127.0.0.1:3306/dbname")
    descriptor = url :: descriptor

    val driver = new PropertyDescriptor()
      .name("driver")
      .displayName("Driver")
      .description("用于连接到此URL的JDBC驱动类名")
      .defaultValue(DataBaseType.MySQL8.getDriverClassName)
      .required(true)
      .order(2)
      .example(DataBaseType.MySQL8.getDriverClassName)
    descriptor = driver :: descriptor

    val username = new PropertyDescriptor()
      .name("username")
      .displayName("username")
      .description("JDBC用户名。")
      .defaultValue("")
      .required(true)
      .order(3)
      .example("root")
    descriptor = username :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("JDBC密码")
      .defaultValue("")
      .required(false)
      .example("12345")
      .order(4)
      .sensitive(true)
    descriptor = password :: descriptor

    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("execute sql语句，多行sql以';'分隔")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .order(5)
      .example("CREATE TABLE IF NOT EXISTS `test` (`id` int DEFAULT NULL,`name` varchar(20) DEFAULT NULL,`age` int DEFAULT NULL);")
    descriptor = sql :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ExecuteSqlStop.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
