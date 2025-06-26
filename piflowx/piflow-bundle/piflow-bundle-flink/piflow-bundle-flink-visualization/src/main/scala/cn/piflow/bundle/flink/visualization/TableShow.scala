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

package cn.piflow.bundle.flink.visualization

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.{ConfigurableVisualizationStop, Port, StopGroup, VisualizationType}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.{FileUtil, IdGenerator}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

import java.nio.file.{Files, Paths}

class TableShow extends ConfigurableVisualizationStop[Null, Table, Null] {

  override var visualizationType: String = VisualizationType.Table
  override val authorEmail: String = ""
  override val description: String = "Show data with table"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var showField: String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    showField = MapUtil.get(map, key = "showField", "").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()
    val showField = new PropertyDescriptor()
      .name("showField")
      .displayName("ShowField")
      .description("The fields  of data to show.")
      .defaultValue("*")
      .example("id,name,age")
      .required(false)

    descriptor = showField :: descriptor
    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/table.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.Visualization)
  }

  override def initialize(ctx: ProcessContext[Null, Table, Null]): Unit = {}

  override def perform(
      in: JobInputStream[Null, Table, Null],
      out: JobOutputStream[Null, Table, Null],
      pec: JobContext[Null, Table, Null]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()
    val inputTable: Table = in.read()

    val visualizationPath: String = System.getProperty("java.io.tmpdir") +
      "/visualization/" + pec.getProcessContext.getProcess.pid() + Constants.SINGLE_SLASH + pec.getStopJob.getStopName
    val portDataPath = visualizationPath + "/data"
    val portSchemaPath = visualizationPath + "/schema"
    Files.createDirectories(Paths.get(FileUtil.convertUriToLocalPath(visualizationPath)))

    val inputTempViewName = this.getClass.getSimpleName
      .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    tableEnv.createTemporaryView(inputTempViewName, inputTable)

    val schema = inputTable.getResolvedSchema
    var columns = ""
    var selectColumns = ""

    if (StringUtils.isEmpty(showField) || "*".equals(showField)) {
      columns = RowTypeUtil.getTableSchema(inputTable)
      selectColumns = String.join(",", schema.getColumnNames)
    } else {
      showField.split(",").foreach(fieldName => {
        val columnOption = schema.getColumn(fieldName)
        if (columnOption.isPresent) {
          val column = columnOption.get()
          columns += s"  $fieldName ${column.getDataType},"
        }
      })
      columns = s"( ${columns.stripMargin.dropRight(1)} )"
      selectColumns = showField
    }

    // 创建临时视图
    val tmpViewName = this.getClass.getSimpleName
      .stripSuffix("$") + Constants.UNDERLINE_SIGN + IdGenerator.uuidWithoutSplit

    val ddl =
      s""" CREATE TABLE $tmpViewName
         | $columns
         | WITH (
         |'connector' = 'filesystem',
         |'path' = '$portDataPath',
         |'format' = 'json'
         |)
         |""".stripMargin
        .replaceAll("\r\n", " ")
        .replaceAll(Constants.LINE_SPLIT_N, " ")

    println(ddl)

    tableEnv.executeSql(ddl)
    tableEnv.executeSql(s"INSERT INTO $tmpViewName SELECT $selectColumns FROM $inputTempViewName")
    tableEnv.toDataStream(inputTable.limit(1)).print()

    // HdfsUtil.saveLine(portSchemaPath, String.join(",", fieldNames))
    FileUtil.writeFile(selectColumns, portSchemaPath)

  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
