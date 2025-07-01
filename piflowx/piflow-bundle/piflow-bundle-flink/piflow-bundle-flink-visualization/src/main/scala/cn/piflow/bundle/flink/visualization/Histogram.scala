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
import cn.piflow.conf._
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.IdGenerator
import org.apache.flink.api.common.functions.MapFunction
import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.configuration.MemorySize
import org.apache.flink.connector.file.sink.FileSink
import org.apache.flink.core.fs.Path
import org.apache.flink.core.io.SimpleVersionedSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.{BucketAssigner, OutputFileConfig}
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.SimpleVersionedStringSerializer
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.table.api._
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

import java.nio.file.{Files, Paths}
import java.time.{Duration, LocalDateTime}

import scala.collection.JavaConversions.asScalaBuffer

class Histogram extends ConfigurableVisualizationStop[Null, Table, Null] {

  override val authorEmail: String = ""
  override val description: String = "使用柱状图展示数据。横坐标表示时间，纵坐标表示聚合值。"

  override val inportList: List[String] = List(Port.DefaultPort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var abscissa: String = _
  var windowSize: Long = _
  var timeField: String = _
  var timeType: String = _

  override var visualizationType: String = VisualizationType.Histogram
  override val isCustomized: Boolean = true
  override val customizedAllowValue: List[String] = List("COUNT", "SUM", "AVG", "MAX", "MIN")

  override def setProperties(map: Map[String, Any]): Unit = {
    abscissa = MapUtil.get(map, key = "abscissa").asInstanceOf[String]
    windowSize = MapUtil.get(map, key = "windowSize", defaultValue = "60").asInstanceOf[String].toLong
    timeField = MapUtil.get(map, key = "timeField", defaultValue = "").asInstanceOf[String]
    timeType = MapUtil.get(map, key = "timeType", defaultValue = "PROCESSING_TIME").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    val abscissa = new PropertyDescriptor()
      .name("abscissa")
      .displayName("横坐标字段")
      .description("用于分组的字段")
      .defaultValue("")
      .required(true)

    val timeType = new PropertyDescriptor()
      .name("timeType")
      .displayName("时间类型")
      .description("PROCESSING_TIME（处理时间）或 EVENT_TIME（事件时间）")
      .defaultValue("PROCESSING_TIME")
      .allowableValues(Set("PROCESSING_TIME", "EVENT_TIME"))
      .required(true)

    val timeField = new PropertyDescriptor()
      .name("timeField")
      .displayName("时间字段")
      .description("用于时间窗口的事件时间字段")
      .defaultValue("")
      .required(false)

    val windowSize = new PropertyDescriptor()
      .name("windowSize")
      .displayName("窗口大小(秒)")
      .description("时间窗口的持续时间（秒）")
      .defaultValue("60")
      .required(true)

    val customizedProperties = new PropertyDescriptor()
      .name("customizedProperties")
      .displayName("customizedProperties")
      .description("custom properties")
      .defaultValue("")
      .language(Language.CustomProperties)
      .required(true)
      .example("\"age\": \"COUNT\",\"id\": \"SUM\"")

    List(abscissa, timeType, timeField, windowSize, customizedProperties)
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/visualization/histogram.png")
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

    // 2. 配置空闲检测
    val configuration = tableEnv.getConfig.getConfiguration
    configuration.setString("table.exec.source.idle-timeout", "60s")

    if (this.customizedProperties != null && this.customizedProperties.nonEmpty) {
      val inputTempViewName = s"${getClass.getSimpleName}_${IdGenerator.uuidWithoutSplit}"
      tableEnv.createTemporaryView(inputTempViewName, inputTable)

      // 构建聚合表达式
      val aggregations = this.customizedProperties.map {
        case (field, op) => s"$op(`$field`) AS `${field}_$op`"
      }.mkString(",")

      // 根据时间类型构建SQL
      val sqlText = timeType match {

        case "PROCESSING_TIME" =>
          s"""
             |SELECT
             |  `$abscissa`,
             |  TUMBLE_START(PROCTIME(), INTERVAL '$windowSize' SECOND) AS window_start,
             |  TUMBLE_END(PROCTIME(), INTERVAL '$windowSize' SECOND) AS window_end,
             |  $aggregations
             |FROM $inputTempViewName
             |GROUP BY `$abscissa`,
             |  TUMBLE(PROCTIME(), INTERVAL '$windowSize' SECOND)
             |""".stripMargin

        case "EVENT_TIME" =>
          if (timeField == null || timeField.isEmpty) {
            throw new IllegalArgumentException("事件时间模式必须提供timeField参数")
          }

          // 添加水印空闲检测
          tableEnv.executeSql(
            s"""
               |CREATE TEMPORARY VIEW ${inputTempViewName}_with_idle AS
               |SELECT *,
               |  WATERMARK FOR `$timeField` AS `$timeField` - INTERVAL '5' SECOND
               |FROM $inputTempViewName
               |""".stripMargin)

          s"""
             |SELECT
             |  `$abscissa`,
             |  TUMBLE_START(`$timeField`, INTERVAL '$windowSize' SECOND) AS window_start,
             |  TUMBLE_END(`$timeField`, INTERVAL '$windowSize' SECOND) AS window_end,
             |  $aggregations
             |FROM ${inputTempViewName}_with_idle
             |GROUP BY `$abscissa`,
             |  TUMBLE(`$timeField`, INTERVAL '$windowSize' SECOND)
             |""".stripMargin
      }

      println(s"SQL: $sqlText")

      val resultTable = tableEnv.sqlQuery(sqlText)

      // 准备可视化存储路径
      val visualizationPath = s"${System.getProperty("java.io.tmpdir")}/visualization/" +
        s"${pec.getProcessContext.getProcess.pid()}/${pec.getStopJob.getStopName}"
      Files.createDirectories(Paths.get(visualizationPath))

      // 写入列名元数据
      val schema = resultTable.getResolvedSchema
      val columnNames = schema.getColumnNames.mkString(",")
      Files.write(Paths.get(s"$visualizationPath/schema"), columnNames.getBytes)

      // 配置文件Sink
      val outputConfig = OutputFileConfig.builder()
        .withPartPrefix("line_chart")
        .withPartSuffix(".json")
        .build()

      val sink: FileSink[String] = FileSink
        .forRowFormat(
          new Path(s"$visualizationPath"),
          new SimpleStringEncoder[String]("UTF-8"))
        .withOutputFileConfig(outputConfig)
        // .withBucketAssigner(new DateTimeBucketAssigner[String]("yyyy-MM-dd-HH"))
        .withBucketAssigner(new FixedBucketAssigner())
        // 固定桶名
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
      jsonDs.print()
      jsonDs.sinkTo(sink).name("LineChartSink").uid("line-chart-sink").setParallelism(1)
    }

    // todo 如何保证数据在窗口完整处理
    // 等待3个窗口周期
    Thread.sleep(windowSize * 3000)

    // 传递原始数据到下游
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
