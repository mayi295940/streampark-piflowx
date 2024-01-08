/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export type FlinkTableBaseInfo = {
  catalogName?: string;
  dbname?: string;
  schema?: string;
  ifNotExists?: Boolean;
  tableName?: string;
  tableComment?: string;
};

export type FlinkTableAsSelectStatement = {
  selectStatement?: string;
};

export interface FlinkTableLikeStatement {
  likeStatement?: string;
}

export type FlinkTablePhysicalColumn = {
  columnName: string;
  columnType: string;
  length?: number;
  precision?: number;
  scale?: number;
  nullable?: Boolean;
  primaryKey?: Boolean;
  partitionKey?: Boolean;
  comment?: string;
};

export type FlinkTableMetadataColumn = {
  columnName: string;
  columnType: string;
  from: string;
  virtual?: Boolean;
};

export type FlinkTableComputedColumn = {
  columnName: string;
  computedColumnExpression: string;
  comment: string;
};

export type FlinkTableWatermark = {
  rowTimeColumnName: string;
  time: Date;
  timeUnit: string;
};

export interface FlinkTableDefinition {
  tableBaseInfo: FlinkTableBaseInfo;
  asSelectStatement: FlinkTableAsSelectStatement;
  likeStatement: FlinkTableLikeStatement;
  physicalColumnDefinition: Array<FlinkTablePhysicalColumn>;
  metadataColumnDefinition: Array<FlinkTableMetadataColumn>;
  computedColumnDefinition: Array<FlinkTableComputedColumn>;
  watermarkDefinition: FlinkTableWatermark;
}
