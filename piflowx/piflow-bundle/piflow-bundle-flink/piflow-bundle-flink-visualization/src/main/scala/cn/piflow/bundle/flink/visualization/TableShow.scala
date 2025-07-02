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
import cn.piflow.conf.{ConfigurableVisualizationStop, Port, StopGroup, VisualizationType}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.{FileUtil, IdGenerator}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.configuration.MemorySize
import org.apache.flink.connector.file.sink.FileSink
import org.apache.flink.core.fs.Path
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.{BucketAssigner, OutputFileConfig}
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

import java.nio.file.{Files, Paths}
import java.time.{Duration, LocalDateTime}

class TableShow extends ConfigurableVisualizationStop[Null, Table, Null] {

  override var visualizationType: String = VisualizationType.Table
  override val authorEmail: String = ""
  override val description: String = "Show data with table"
  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  private var showField: String = _
  private var showNumber: Int = _

  override def setProperties(map: Map[String, Any]): Unit = {
    showField = MapUtil.get(map, key = "showField", "").asInstanceOf[String]
    MapUtil.get(map, "showNumber", "-1") match {
      case str: String =>
        try {
          showNumber = str.toInt
        } catch {
          case _: NumberFormatException =>
            showNumber = -1
            println("Failed to convert showNumber to Int. Using default value -1.")
        }
      case _ =>
        showNumber = -1
        println("showNumber is not a String. Using default value -1.")
    }
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

    val showNumber = new PropertyDescriptor()
      .name("showNumber")
      .displayName("showNumber")
      .description("The count to show.")
      .required(false)
      .defaultValue("")
      .example("10")
    descriptor = showNumber :: descriptor

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

    val inputTempViewName = s"${getClass.getSimpleName.stripSuffix("$")}_${IdGenerator.uuidWithoutSplit}"

    if (showNumber > 0) {
      tableEnv.createTemporaryView(inputTempViewName, inputTable.limit(showNumber))
    } else {
      tableEnv.createTemporaryView(inputTempViewName, inputTable)
    }

    val schema = inputTable.getResolvedSchema
    var selectColumns = ""

    if (StringUtils.isEmpty(showField) || "*".equals(showField)) {
      selectColumns = String.join(",", schema.getColumnNames)
    } else {
      selectColumns = showField
    }

    val resultTable = tableEnv.sqlQuery(s"select $selectColumns from $inputTempViewName")

    // 配置文件Sink
    val outputConfig = OutputFileConfig.builder()
      .withPartPrefix("table_show")
      .withPartSuffix(".json")
      .build()

    val sink: FileSink[String] = FileSink
      .forRowFormat(
        new Path(s"$visualizationPath"),
        new SimpleStringEncoder[String]("UTF-8"))
      .withOutputFileConfig(outputConfig)
      // 固定桶名
      .withBucketAssigner(new FixedBucketAssigner())
      .withRollingPolicy(
        DefaultRollingPolicy.builder()
          .withRolloverInterval(Duration.ofMinutes(10))
          .withInactivityInterval(Duration.ofMinutes(5))
          .withMaxPartSize(MemorySize.ofMebiBytes(128))
          .build())
      .build()

    // 转换为DataStream并转换为JSON格式
    val resultDs = tableEnv.toDataStream(resultTable)
    // 使用可序列化的MapFunction替代Lambda表达式
    val jsonDs = resultDs.map(new RowToJsonMapper(schema.getColumnNames.toArray(Array[String]())))
    jsonDs.sinkTo(sink).name("TableShowSink").uid("table-show-sink").setParallelism(1)

    // HdfsUtil.saveLine(portSchemaPath, String.join(",", fieldNames))
    FileUtil.writeFile(selectColumns, portSchemaPath)

    out.write(inputTable)
  }

  // 可序列化的MapFunction实现
  private class RowToJsonMapper(fieldNames: Array[String]) extends MapFunction[Row, String] {
    override def map(row: Row): String = {
      val sb = new StringBuilder("{")
      for (i <- 0 until row.getArity) {
        if (i > 0) sb.append(",")
        sb.append(s""""${fieldNames(i)}":""")

        val value = row.getField(i)
        value match {
          case null => sb.append("null")
          case s: String => sb.append(s""""${s.replace("\"", "\\\"")}"""")
          case d: java.util.Date => sb.append(s""""${d.toString}"""")
          case d: LocalDateTime => sb.append(s""""${d.toString}"""")
          case _ => sb.append(value)
        }
      }
      sb.append("}")
      sb.toString
    }
  }

  private class FixedBucketAssigner extends BucketAssigner[String, String] {
    override def getBucketId(element: String, context: BucketAssigner.Context): String = {
      // 所有数据都写入到 "data" 子目录
      "data"
    }

    override def getSerializer: SimpleVersionedSerializer[String] = {
      SimpleVersionedStringSerializer.INSTANCE
    }
  }

  override def getEngineType: String = Constants.ENGIN_FLINK

}
