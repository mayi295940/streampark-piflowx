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

package cn.piflow.bundle.beam.test

import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.extensions.sql.SqlTransform
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.schemas.Schema
import org.apache.beam.sdk.transforms.{Create, DoFn, ParDo}
import org.apache.beam.sdk.transforms.DoFn.{Element, ProcessElement}
import org.apache.beam.sdk.values.Row

object BeamDemo {

  // 避免使用匿名类，定义一个独立的 DoFn 类
  private class PrintDoFn extends DoFn[Row, Void] {
    @ProcessElement
    def processElement(@Element row: Row, out: DoFn.OutputReceiver[Void]): Unit = {
      println(row) // 打印每一行数据
    }
  }

  def main(args: Array[String]): Unit = {

    val options = PipelineOptionsFactory.create
    val pipeline = Pipeline.create(options)

    // 定义 Schema
    val schema = Schema.builder
      .addStringField("name")
      .addInt32Field("age")
      .addDoubleField("score")
      .build

    val row1 = Row.withSchema(schema)
      .withFieldValue("name", "Alice")
      .withFieldValue("age", 25)
      .withFieldValue("score", 95.5)
      .build()

    // 创建 PCollection<Row>
    val inputRows = pipeline.apply("CreateData", Create.of(row1)).setRowSchema(schema)

    // 定义 SQL 查询
    val sqlQuery = "SELECT name, age, score FROM PCOLLECTION WHERE score > 90"

    // 执行查询
    val resultRows = inputRows.apply("ExecuteSQL", SqlTransform.query(sqlQuery))

    // 在 Pipeline 中使用
    resultRows.apply("PrintResults", ParDo.of(new PrintDoFn()))

    pipeline.run.waitUntilFinish

  }

}
