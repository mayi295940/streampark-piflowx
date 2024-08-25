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

package cn.piflow.bundle.spark.jdbc

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}

import java.util.Properties

class PostgresqlWrite extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "bbbbbbyz1110@163.com"
  val description: String = "Write data into postgresql database with jdbc"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var url: String = _
  var user: String = _
  var password: String = _
  var dbtable: String = _
  var saveMode: String = _

  def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val jdbcDF = in.read()
    val properties = new Properties()
    properties.put("user", user)
    properties.put("password", password)
    properties.put("driver", "org.postgresql.Driver")

    jdbcDF.write
      .mode(SaveMode.valueOf(saveMode))
      .jdbc(url, dbtable, properties)
    out.write(jdbcDF)
  }

  def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map, "url").asInstanceOf[String]
    user = MapUtil.get(map, "user").asInstanceOf[String]
    password = MapUtil.get(map, "password").asInstanceOf[String]
    dbtable = MapUtil.get(map, "dbtable").asInstanceOf[String]
    saveMode = MapUtil.get(map, "saveMode").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val saveModeOption = Set("Append", "Overwrite", "Ignore")

    val url = new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("The Url of postgresql database")
      .defaultValue("jdbc:postgresql://...:5432/...")
      .required(true)
      .example("jdbc:postgresql://127.0.0.1:5432/dbname")
    descriptor = url :: descriptor

    val user = new PropertyDescriptor()
      .name("user")
      .displayName("User")
      .description("The user name of postgresql")
      .defaultValue("")
      .required(true)
      .example("postgres")
    descriptor = user :: descriptor

    val password = new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("The password of postgresql")
      .defaultValue("")
      .required(true)
      .example("123456")
      .sensitive(true)
    descriptor = password :: descriptor

    val dbtable = new PropertyDescriptor()
      .name("dbtable")
      .displayName("DBTable")
      .description("The table you want to write")
      .defaultValue("")
      .required(true)
      .example("test")
    descriptor = dbtable :: descriptor

    val saveMode = new PropertyDescriptor()
      .name("saveMode")
      .displayName("SaveMode")
      .description("The save mode for table")
      .allowableValues(saveModeOption)
      .defaultValue("Append")
      .required(true)
      .example("Append")
    descriptor = saveMode :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/PostgresqlWrite.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
