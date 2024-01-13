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

export interface TFlinkTableBaseInfo {
  catalogName?: string;
  dbname?: string;
  schema?: string;
  ifNotExists?: Boolean;
  registerTableName?: string;
  registerTableComment?: string;
}

export interface TFlinkTableAsSelectStatement {
  selectStatement?: string;
}

export interface TFlinkTableLikeStatement {
  likeStatement?: string;
}

export interface TFlinkTablePhysicalColumn {
  columnName: string;
  columnType: string;
  length?: number;
  precision?: number;
  scale?: number;
  nullable?: Boolean;
  primaryKey?: Boolean;
  partitionKey?: Boolean;
  comment?: string;
}

export interface TFlinkTableMetadataColumn {
  columnName: string;
  columnType: string;
  from: string;
  virtual?: Boolean;
}

export interface TFlinkTableComputedColumn {
  columnName: string;
  computedColumnExpression: string;
  comment: string;
}

export interface TFlinkTableWatermark {
  rowTimeColumnName: string;
  time: Date;
  timeUnit: string;
}

export interface TFlinkTableDefinition {
  tableBaseInfo?: TFlinkTableBaseInfo;
  asSelectStatement?: TFlinkTableAsSelectStatement;
  likeStatement?: TFlinkTableLikeStatement;
  physicalColumnDefinition: Array<TFlinkTablePhysicalColumn>;
  metadataColumnDefinition?: Array<TFlinkTableMetadataColumn>;
  computedColumnDefinition?: Array<TFlinkTableComputedColumn>;
  watermarkDefinition?: TFlinkTableWatermark;
}
