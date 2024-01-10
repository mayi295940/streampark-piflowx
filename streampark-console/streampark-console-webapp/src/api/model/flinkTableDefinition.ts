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

export type TFlinkTableBaseInfo = {
  catalogName?: string;
  dbname?: string;
  schema?: string;
  ifNotExists?: Boolean;
  tableName?: string;
  tableComment?: string;
};

export type TFlinkTableAsSelectStatement = {
  selectStatement?: string;
};

export type TFlinkTableLikeStatement = {
  likeStatement?: string;
};

export type TFlinkTablePhysicalColumn = {
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

export type TFlinkTableMetadataColumn = {
  columnName: string;
  columnType: string;
  from: string;
  virtual?: Boolean;
};

export type TFlinkTableComputedColumn = {
  columnName: string;
  computedColumnExpression: string;
  comment: string;
};

export type TFlinkTableWatermark = {
  rowTimeColumnName: string;
  time: Date;
  timeUnit: string;
};

export type TFlinkTableDefinition = {
  tableBaseInfo: TFlinkTableBaseInfo;
  asSelectStatement: TFlinkTableAsSelectStatement;
  likeStatement: TFlinkTableLikeStatement;
  physicalColumnDefinition: Array<TFlinkTablePhysicalColumn>;
  metadataColumnDefinition: Array<TFlinkTableMetadataColumn>;
  computedColumnDefinition: Array<TFlinkTableComputedColumn>;
  watermarkDefinition: TFlinkTableWatermark;
};
