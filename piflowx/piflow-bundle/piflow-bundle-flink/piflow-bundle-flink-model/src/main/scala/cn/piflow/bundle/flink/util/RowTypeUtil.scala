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

package cn.piflow.bundle.flink.util

import cn.piflow.Constants
import cn.piflow.bundle.flink.model.FlinkTableDefinition
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.typeinfo.{BasicTypeInfo, TypeInformation, Types}
import org.apache.flink.api.java.typeutils.RowTypeInfo
import org.apache.flink.table.api.{DataTypes, Schema, Table}
import org.apache.flink.table.types.DataType

import java.util

// https://nightlies.apache.org/flink/flink-docs-release-1.17/zh/docs/dev/table/types/
// TODO 类型完善
object RowTypeUtil {

  /** 生成Row类型的TypeInformation */
  def getRowTypeInfo(schema: String): RowTypeInfo = {
    val field = schema.split(Constants.COMMA)

    val columnTypes = new Array[TypeInformation[_]](field.size)

    val fieldNames = new Array[String](field.size)

    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }

      fieldNames(i) = columnName

      columnType.toLowerCase() match {
        case "string" => columnTypes(i) = Types.STRING
        case "int" => columnTypes(i) = Types.INT
        case "double" => columnTypes(i) = Types.DOUBLE
        case "float" => columnTypes(i) = Types.FLOAT
        case "long" => columnTypes(i) = Types.LONG
        case "boolean" => columnTypes(i) = Types.BOOLEAN
        case "date" => columnTypes(i) = BasicTypeInfo.DATE_TYPE_INFO
        case "timestamp" => columnTypes(i) = Types.SQL_TIMESTAMP
        case _ =>
          throw new RuntimeException("Unsupported type: " + columnType)
      }
    }

    val info = new RowTypeInfo(columnTypes, fieldNames)

    info
  }

  def getDataType(schema: String): (DataType, Array[String]) = {

    val fieldList = new util.ArrayList[DataTypes.Field]()

    val field = schema.split(Constants.COMMA)

    val fieldNames = new Array[String](field.size)

    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }

      fieldNames(i) = columnName

      val filed = DataTypes.FIELD(columnName, getDataTypeByType(columnType))
      fieldList.add(filed)
    }

    val info = DataTypes.ROW(fieldList)

    (info, fieldNames)
  }

  /** 生成Row类型的TypeInformation. */
  def getRowSchema(schema: String): Schema = {
    val schemaBuilder = Schema.newBuilder()
    val field = schema.split(Constants.COMMA)
    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }
      schemaBuilder.column(columnName, getDataTypeByType(columnType))
    }
    schemaBuilder.build()
  }

  /** 生成table Schema */
  def getTableSchema(schema: String): String = {

    val primaryKey: String = ""
    var tableSchema = ""

    val field = schema.split(Constants.COMMA)
    for (i <- 0 until field.size) {
      val columnInfo = field(i).trim.split(Constants.COLON)
      val columnName = columnInfo(0).trim
      val columnType = columnInfo(1).trim
      var isNullable = false
      if (columnInfo.size == 3) {
        isNullable = columnInfo(2).trim.toBoolean
      }
      tableSchema += s"  $columnName ${getDataTypeByType(columnType)},"
    }

    if (StringUtils.isNotBlank(primaryKey)) {
      tableSchema = tableSchema.stripMargin + s"PRIMARY KEY ($primaryKey) NOT ENFORCED"
      tableSchema
    } else {
      tableSchema.stripMargin.dropRight(1)
    }
  }

  /** 生成table Schema */
  def getTableSchema(table: Table): String = {
    s"${table.getResolvedSchema.toString.replace("ROW<", "").replace(">", "")}"
  }

  /**
   * 根据columnType获取对应的DataType
   *
   * @param columnType 列类型
   * @return DataType
   */
  def getDataTypeByType(columnType: String): DataType = {
    if (StringUtils.isBlank(columnType)) {
      throw new RuntimeException("type can not be null")
    }
    columnType.toLowerCase() match {
      case "string" => DataTypes.STRING()
      case "int" => DataTypes.INT()
      case "double" => DataTypes.DOUBLE()
      case "float" => DataTypes.FLOAT()
      case "long" => DataTypes.BIGINT()
      case "boolean" => DataTypes.BOOLEAN()
      case "date" => DataTypes.DATE()
      case "timestamp" => DataTypes.TIMESTAMP()
      case _ =>
        throw new RuntimeException("Unsupported type: " + columnType)
    }
  }

  /** 生成table Schema */
  def getTableSchema(definition: FlinkTableDefinition): (String, String, String, String, String, String) = {

    var columns = ""
    var primaryKeyList: List[String] = List()
    var partitionKeyList: List[String] = List()

    val baseInfo = definition.getTableBaseInfo
    val asSelectStatement = definition.getAsSelectStatement
    val likeStatement = definition.getLikeStatement
    val physicalColumns = definition.getPhysicalColumnDefinition
    val metadataColumns = definition.getMetadataColumnDefinition
    val computedColumns = definition.getComputedColumnDefinition
    val watermark = definition.getWatermarkDefinition

    /*
     * <physical_column_definition>:
     * column_name column_type [ <column_constraint> ] [COMMENT column_comment]
     */
    if (CollectionUtils.isNotEmpty(physicalColumns)) {
      physicalColumns.forEach(column => {
        if (StringUtils.isNotBlank(column.getColumnName)) {

          if (column.getLength != null && column.getLength > 0) {
            columns += s"  ${column.getColumnName} ${column.getColumnType}(${column.getLength})"
          } else if (column.getPrecision != null && column.getPrecision > 0 && column.getScale != null && column.getScale > 0) {
            columns += s"  ${column.getColumnName} ${column.getColumnType}(${column.getPrecision}, ${column.getScale})"
          } else {
            columns += s"  ${column.getColumnName} ${column.getColumnType}"
          }

          if (StringUtils.isNotBlank(column.getComment)) {
            columns += s" COMMENT '${column.getComment}'"
          }
          columns += Constants.COMMA

          if (column.getPrimaryKey()) {
            primaryKeyList = column.getColumnName :: primaryKeyList
          }
          if (column.getPartitionKey()) {
            partitionKeyList = column.getColumnName :: partitionKeyList
          }
        }
      })
    }

    /*
     * <metadata_column_definition>:
     *  column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL ]
     */
    if (CollectionUtils.isNotEmpty(metadataColumns)) {
      metadataColumns.forEach(column => {
        if (StringUtils.isNotBlank(column.getColumnName)) {
          columns += s"  ${column.getColumnName} ${column.getColumnType} METADATA"
          if (StringUtils.isNotBlank(column.getFrom)) {
            columns += s" FROM '${column.getFrom}"
          }
          if (column.getVirtual) {
            columns += " VIRTUAL"
          }
          columns += Constants.COMMA
        }
      })
    }

    /*
     * <computed_column_definition>:
     * column_name AS computed_column_expression [COMMENT column_comment]
     */
    if (CollectionUtils.isNotEmpty(computedColumns)) {
      computedColumns.forEach(column => {
        if (StringUtils.isNotBlank(column.getColumnName)) {
          columns += s"  ${column.getColumnName} AS ${column.getComputedColumnExpression}"
          if (StringUtils.isNotBlank(column.getComment)) {
            columns += s" COMMENT '${column.getComment}"
          }
          columns += Constants.COMMA
        }
      })
    }

    /*
     * <watermark_definition>:
     * WATERMARK FOR rowtime_column_name AS watermark_strategy_expression
     * WATERMARK FOR view_time as view_time - INTERVAL '5' SECOND
     */
    if (watermark != null) {
      if (StringUtils.isNotBlank(watermark.getRowTimeColumnName)) {
        columns += s" WATERMARK FOR  ${watermark.getRowTimeColumnName} " +
          s"AS ${watermark.getRowTimeColumnName} - INTERVAL '${watermark.getTime}' ${watermark.getTimeUnit},"
      }
    }

    if (primaryKeyList.nonEmpty) {
      columns += s" PRIMARY KEY (${primaryKeyList.mkString(Constants.COMMA)}) NOT ENFORCED,"
    }

    val tableComment =
      if (baseInfo != null && StringUtils.isNotBlank(baseInfo.getRegisterTableComment))
        s" COMMENT ${baseInfo.getRegisterTableComment}"
      else ""

    val partitionStatement =
      if (partitionKeyList.nonEmpty)
        s" PARTITIONED BY (${partitionKeyList.mkString(Constants.COMMA)}) "
      else ""

    val ifNotExists = if (baseInfo != null && baseInfo.getIfNotExists) "IF NOT EXISTS" else ""

    val selectStatement =
      if (asSelectStatement != null && StringUtils.isNotBlank(asSelectStatement.getSelectStatement))
        s" AS ${asSelectStatement.getSelectStatement}"
      else ""

    val like =
      if (likeStatement != null && StringUtils.isNotBlank(likeStatement.getLikeStatement))
        s" LIKE  ${likeStatement.getLikeStatement}"
      else ""

    if (StringUtils.isNotBlank(columns)) {
      columns = s"( ${columns.stripMargin.dropRight(1)} )"
    }

    (columns, ifNotExists, tableComment, partitionStatement, selectStatement, like)
  }

}
