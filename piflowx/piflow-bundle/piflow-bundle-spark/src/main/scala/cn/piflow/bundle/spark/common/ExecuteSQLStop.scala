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

package cn.piflow.bundle.spark.common

import cn.piflow._
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

class ExecuteSQLStop extends ConfigurableStop[DataFrame] {

  val authorEmail: String = "ygang@cnic.cn"
  val description: String = "Create temporary view table to execute sql"
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)

  var sql: String = _
  var ViewName: String = _

  override def perform(
      in: JobInputStream[DataFrame],
      out: JobOutputStream[DataFrame],
      pec: JobContext[DataFrame]): Unit = {

    val spark = pec.get[SparkSession]()
    val inDF = in.read()
    inDF.createOrReplaceTempView(ViewName)

    val frame: DataFrame = spark.sql(sql)
    out.write(frame)
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    sql = MapUtil.get(map, "sql").asInstanceOf[String]
    ViewName = MapUtil.get(map, "viewName").asInstanceOf[String]

  }

  override def initialize(ctx: ProcessContext[DataFrame]): Unit = {}

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val sql = new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("Sql string")
      .defaultValue("")
      .required(true)
      .language(Language.Sql)
      .example("select * from temp")
    descriptor = sql :: descriptor

    val ViewName = new PropertyDescriptor()
      .name("viewName")
      .displayName("ViewName")
      .description("Name of the temporary view table")
      .defaultValue("temp")
      .required(true)
      .example("temp")

    descriptor = ViewName :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/ExecuteSqlStop.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CommonGroup)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
