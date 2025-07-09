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

package cn.piflow.bundle.spark.dqc

import cn.piflow._
import cn.piflow.conf.{ConfigurableStop, Language, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.ImageUtil
import cn.piflow.util.IdGenerator
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.util.Optional

class ColumnCompare extends ConfigurableStop[Null, DataFrame, Null] {

  override val authorEmail: String = ""
  override val description: String = "compare data in the right table from the left table"

  override val inportList: List[String] = List(Port.LeftPort, Port.RightPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var originTableName: String = _
  private var originTablePrimary: String = _
  private var originTableFields: String = _
  private var originTableFilter: String = _

  private var toTableName: String = _
  private var toTablePrimary: String = _
  private var toTableFields: String = _
  private var toTableFilter: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    originTableName = s"${getClass.getSimpleName.stripSuffix("$")}_${IdGenerator.uuidWithoutSplit}_originTableName"
    originTablePrimary = map.getOrElse("originTablePrimary", "").asInstanceOf[String]
    originTableFields = map.getOrElse("originTableFields", "").asInstanceOf[String]
    originTableFilter = map.getOrElse("originTableFilter", "").asInstanceOf[String]

    toTableName = s"${getClass.getSimpleName.stripSuffix("$")}_${IdGenerator.uuidWithoutSplit}_toTableName"
    toTablePrimary = map.getOrElse("toTablePrimary", "").asInstanceOf[String]
    toTableFields = map.getOrElse("toTableFields", "").asInstanceOf[String]
    toTableFilter = map.getOrElse("toTableFilter", "").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val originTablePrimary = new PropertyDescriptor()
      .name("originTablePrimary")
      .displayName("源表主键")
      .language(Language.Text)
      .description("源表主键")
      .required(true)

    descriptor = originTablePrimary :: descriptor

    val originTableFields = new PropertyDescriptor()
      .name("originTableFields")
      .displayName("源表字段")
      .language(Language.Text)
      .description("源表字段列表，以逗号分隔")
      .required(true)

    descriptor = originTableFields :: descriptor

    val originTableFilter = new PropertyDescriptor()
      .name("originTableFilter")
      .displayName("源表过滤条件")
      .language(Language.Text)
      .description("源表过滤条件")
      .required(false)

    descriptor = originTableFilter :: descriptor

    val toTablePrimary = new PropertyDescriptor()
      .name("toTablePrimary")
      .displayName("目标表主键")
      .language(Language.Text)
      .description("目标表主键")
      .required(true)

    descriptor = toTablePrimary :: descriptor

    val toTableFields = new PropertyDescriptor()
      .name("toTableFields")
      .displayName("目标表字段")
      .language(Language.Text)
      .description("目标表字段列表，以逗号分隔")
      .required(true)

    descriptor = toTableFields :: descriptor

    val toTableFilter = new PropertyDescriptor()
      .name("originTableFilter")
      .displayName("目标表过滤条件")
      .language(Language.Text)
      .description("目标表过滤条件")
      .required(false)

    descriptor = toTableFilter :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/common/Distinct.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Dqc)
  }

  override def initialize(ctx: ProcessContext[Null, DataFrame, Null]): Unit = {}

  private def fields_list_check_filter(column: String): String =
    s"""
       |if(base.$column is null, '-', base.$column) <>
       |if(verify.$column is null, '-', verify.$column)
       |""".stripMargin.trim

  private def CHECK_SQL(
      originTablePrimary: String,
      originTableFields: String,
      originTableName: String,
      originTableFilter: String,
      toTableName: String,
      toTableFilter: String,
      fields_list_check: String,
      fields_list_check_filter: String): String =
    s"""
       |select base.$originTablePrimary as base_primary,
       |       $fields_list_check
       |  from (
       |        select 'num' as compareKey,
       |               $originTablePrimary,
       |               $originTableFields
       |          from $originTableName
       |          $originTableFilter
       |       ) base
       |  left join (
       |        select 'num' as compareKey,
       |               $originTablePrimary,
       |               $originTableFields
       |          from $toTableName
       |          $toTableFilter
       |       ) verify
       |on base.compareKey = verify.compareKey
       |   and if(base.$originTablePrimary is null, '-', base.$originTablePrimary) = if(verify.$originTablePrimary is null, '-', verify.$originTablePrimary)
       | where
       |       $fields_list_check_filter
       |union
       |select base.$originTablePrimary as base_primary,
       |       $fields_list_check
       |  from (
       |        select 'num' as compareKey,
       |               $originTablePrimary,
       |               $originTableFields
       |          from $originTableName
       |          $originTableFilter
       |       ) base
       |  right join (
       |        select 'num' as compareKey,
       |               $originTablePrimary,
       |               $originTableFields
       |          from $toTableName
       |          $toTableFilter
       |       ) verify
       |on base.compareKey = verify.compareKey
       |   and if(base.$originTablePrimary is null, '-', base.$originTablePrimary) = if(verify.$originTablePrimary is null, '-', verify.$originTablePrimary)
       | where
       |       $fields_list_check_filter
       |""".stripMargin

  private def fields_list_check(column: String): String =
    s"""base.$column as base_$column,
       |       verify.$column as verify_$column,
       |       case when if(base.$column is null, '-',base.$column) = if(verify.$column is null, '-', verify.$column) then '一致'
       |            else '不一致'
       |             end as ${column}_is_pass""".stripMargin

  override def perform(
      in: JobInputStream[Null, DataFrame, Null],
      out: JobOutputStream[Null, DataFrame, Null],
      pec: JobContext[Null, DataFrame, Null]): Unit = {

    val spark = pec.get[SparkSession]()

    val leftDF = in.read(Port.LeftPort)

    leftDF.createOrReplaceTempView(originTableName)

    val rightDF = in.read(Port.RightPort)
    rightDF.createOrReplaceTempView(toTableName)

    val filedArr: Array[String] = originTableFields.split(",")
    // 初始化空列表
    var fields_list: List[String] = List.empty
    var fields_list_filter: List[String] = List.empty
    for (i <- filedArr.indices) {
      val field = filedArr(i)
      val check = fields_list_check(field)
      val filter = fields_list_check_filter(field)
      fields_list = fields_list :+ check
      fields_list_filter = fields_list_filter :+ filter
    }

    val check_sql = CHECK_SQL(
      originTablePrimary,
      originTableFields,
      originTableName,
      Optional.ofNullable(originTableFilter).orElse(""),
      toTableName,
      Optional.ofNullable(toTableFilter).orElse(""),
      fields_list.mkString(","),
      fields_list_filter.mkString(" or "))

    println(check_sql)

    val resultDf: DataFrame = spark.sql(check_sql)

    out.write(resultDf)
  }

  override def getEngineType: String = Constants.ENGIN_SPARK

}
