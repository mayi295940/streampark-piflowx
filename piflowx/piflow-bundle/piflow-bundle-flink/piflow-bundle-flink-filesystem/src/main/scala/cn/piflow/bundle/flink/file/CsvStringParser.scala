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

package cn.piflow.bundle.flink.file

import cn.piflow._
import cn.piflow.bundle.flink.util.RowTypeUtil
import cn.piflow.conf.{ConfigurableStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.util.DateUtils
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.types.Row

class CsvStringParser extends ConfigurableStop[Table] {

  override val authorEmail: String = ""
  val inportList: List[String] = List(Port.DefaultPort)
  val outportList: List[String] = List(Port.DefaultPort)
  override val description: String = "Parse csv string"

  var content: String = _
  var delimiter: String = _
  var schema: String = _

  override def perform(
      in: JobInputStream[Table],
      out: JobOutputStream[Table],
      pec: JobContext[Table]): Unit = {

    val tableEnv = pec.get[StreamTableEnvironment]()

    val (rowType, _) = RowTypeUtil.getDataType(schema)

    val children = rowType.getChildren

    val colNum: Int = children.size()

    val arrStr: Array[String] = content.split(Constants.LINE_SPLIT_N).map(x => x.trim)

    val listROW: List[Row] = arrStr
      .map(line => {

        val seqSTR: Seq[String] = line.split(delimiter).map(x => x.trim).toSeq

        // todo time format

        val row = new Row(colNum)
        for (i <- 0 until colNum) {

          val colType = children.get(i).getConversionClass.getSimpleName.toLowerCase()
          colType match {
            case "string" => row.setField(i, seqSTR(i))
            case "integer" => row.setField(i, seqSTR(i).toInt)
            case "long" => row.setField(i, seqSTR(i).toLong)
            case "double" => row.setField(i, seqSTR(i).toDouble)
            case "float" => row.setField(i, seqSTR(i).toFloat)
            case "boolean" => row.setField(i, seqSTR(i).toBoolean)
            case "date" => row.setField(i, DateUtils.strToDate(seqSTR(i)))
            case "timestamp" => row.setField(i, DateUtils.strToSqlTimestamp(seqSTR(i)))
            case _ => row.setField(i, seqSTR(i))
          }
        }

        row
      })
      .toList

    out.write(tableEnv.fromValues(rowType, listROW: _*))
  }

  override def setProperties(map: Map[String, Any]): Unit = {
    content = MapUtil.get(map, "content").asInstanceOf[String]
    delimiter = MapUtil.get(map, "delimiter").asInstanceOf[String]
    schema = MapUtil.get(map, "schema").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor: List[PropertyDescriptor] = List()

    val content = new PropertyDescriptor()
      .name("content")
      .displayName("Content")
      .defaultValue("")
      .required(true)
      .order(1)
      .example("1,zs\n2,ls\n3,ww")
    descriptor = content :: descriptor

    val delimiter = new PropertyDescriptor()
      .name("delimiter")
      .displayName("Delimiter")
      .description("The delimiter of CSV string")
      .defaultValue(",")
      .required(true)
      .order(2)
      .example(",")
    descriptor = delimiter :: descriptor

    val schema = new PropertyDescriptor()
      .name("schema")
      .displayName("Schema")
      .description("The schema of CSV string")
      .defaultValue("")
      .required(false)
      .order(3)
      .example("")
    descriptor = schema :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/csv/CsvStringParser.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.CsvGroup)
  }

  override def initialize(ctx: ProcessContext[Table]): Unit = {}

  override def getEngineType: String = Constants.ENGIN_FLINK

}
