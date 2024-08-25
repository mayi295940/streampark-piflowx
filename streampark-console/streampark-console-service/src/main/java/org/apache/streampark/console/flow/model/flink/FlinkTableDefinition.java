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

package org.apache.streampark.console.flow.model.flink;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * CREATE TABLE [IF NOT EXISTS] [catalog_name.][db_name.]table_name ( { <physical_column_definition>
 * | <metadata_column_definition> | <computed_column_definition> }[ , ...n] [ <watermark_definition>
 * ] [ <table_constraint> ][ , ...n] ) [COMMENT table_comment] [PARTITIONED BY
 * (partition_column_name1, partition_column_name2, ...)] WITH (key1=val1, key2=val2, ...) [ LIKE
 * source_table [( <like_options> )] | AS select_query ]
 *
 * <p><physical_column_definition>: column_name column_type [ <column_constraint> ] [COMMENT
 * column_comment]
 *
 * <p><column_constraint>: [CONSTRAINT constraint_name] PRIMARY KEY NOT ENFORCED
 *
 * <p><table_constraint>: [CONSTRAINT constraint_name] PRIMARY KEY (column_name, ...) NOT ENFORCED
 *
 * <p><metadata_column_definition>: column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL
 * ]
 *
 * <p><computed_column_definition>: column_name AS computed_column_expression [COMMENT
 * column_comment]
 *
 * <p><watermark_definition>: WATERMARK FOR rowtime_column_name AS watermark_strategy_expression
 *
 * <p><source_table>: [catalog_name.][db_name.]table_name
 *
 * <p><like_options>: { { INCLUDING | EXCLUDING } { ALL | CONSTRAINTS | PARTITIONS } | { INCLUDING |
 * EXCLUDING | OVERWRITING } { GENERATED | OPTIONS | WATERMARKS } }[, ...]
 */
public class FlinkTableDefinition {

    private FlinkTableBaseInfo tableBaseInfo;

    private FlinkTableAsSelectStatement asSelectStatement;

    private FlinkTableLikeStatement likeStatement;

    /**
     * <physical_column_definition>: column_name column_type [ <column_constraint> ] [COMMENT
     * column_comment]
     */
    private List<FlinkTablePhysicalColumn> physicalColumnDefinition;

    /**
     * <metadata_column_definition>: column_name column_type METADATA [ FROM metadata_key ] [ VIRTUAL]
     */
    private List<FlinkTableMetadataColumn> metadataColumnDefinition;

    /**
     * <computed_column_definition>: column_name AS computed_column_expression [COMMENT
     * column_comment]
     */
    private List<FlinkTableComputedColumn> computedColumnDefinition;

    /** <watermark_definition>: WATERMARK FOR rowtime_column_name AS watermark_strategy_expression */
    private FlinkTableWatermark watermarkDefinition;

    public FlinkTableBaseInfo getTableBaseInfo() {
        return tableBaseInfo;
    }

    public void setTableBaseInfo(FlinkTableBaseInfo tableBaseInfo) {
        this.tableBaseInfo = tableBaseInfo;
    }

    public FlinkTableAsSelectStatement getAsSelectStatement() {
        return asSelectStatement;
    }

    public void setAsSelectStatement(FlinkTableAsSelectStatement asSelectStatement) {
        this.asSelectStatement = asSelectStatement;
    }

    public FlinkTableLikeStatement getLikeStatement() {
        return likeStatement;
    }

    public void setLikeStatement(FlinkTableLikeStatement likeStatement) {
        this.likeStatement = likeStatement;
    }

    public List<FlinkTablePhysicalColumn> getPhysicalColumnDefinition() {
        return physicalColumnDefinition;
    }

    public void setPhysicalColumnDefinition(List<FlinkTablePhysicalColumn> physicalColumnDefinition) {
        this.physicalColumnDefinition = physicalColumnDefinition;
    }

    public List<FlinkTableMetadataColumn> getMetadataColumnDefinition() {
        return metadataColumnDefinition;
    }

    public void setMetadataColumnDefinition(List<FlinkTableMetadataColumn> metadataColumnDefinition) {
        this.metadataColumnDefinition = metadataColumnDefinition;
    }

    public List<FlinkTableComputedColumn> getComputedColumnDefinition() {
        return computedColumnDefinition;
    }

    public void setComputedColumnDefinition(List<FlinkTableComputedColumn> computedColumnDefinition) {
        this.computedColumnDefinition = computedColumnDefinition;
    }

    public FlinkTableWatermark getWatermarkDefinition() {
        return watermarkDefinition;
    }

    public void setWatermarkDefinition(FlinkTableWatermark watermarkDefinition) {
        this.watermarkDefinition = watermarkDefinition;
    }

    public String getRegisterTableName() {
        if (getTableBaseInfo() != null
            && StringUtils.isNotBlank(getTableBaseInfo().getRegisterTableName())) {
            return getTableBaseInfo().getRegisterTableName();
        } else {
            return "";
        }
    }

    public String getFullRegisterTableName() {

        String realTableName = "";

        if (StringUtils.isEmpty(getRegisterTableName())) {
            return realTableName;
        }

        if (StringUtils.isNotEmpty(getTableBaseInfo().getCatalogName())) {
            realTableName += getTableBaseInfo().getCatalogName() + ".";
        }
        if (StringUtils.isNotEmpty(getTableBaseInfo().getDbname())) {
            realTableName += getTableBaseInfo().getDbname() + ".";
        }

        if (StringUtils.isNotEmpty(getTableBaseInfo().getSchema())) {
            realTableName +=
                "`"
                    + getTableBaseInfo().getSchema()
                    + "."
                    + getTableBaseInfo().getRegisterTableName()
                    + "`";
        } else {
            realTableName += getTableBaseInfo().getRegisterTableName();
        }

        return realTableName;
    }
}