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

package cn.piflow.spark.spark.ceph

import org.apache.spark.sql.{DataFrame, SparkSession}

object CephReadTest {

  var cephAccessKey: String = _
  var cephSecretKey: String = _
  var cephEndpoint: String = _
  var types: String = _
  var path: String = _
  var header: Boolean = _
  var delimiter: String = _

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]").appName("CephReadTest").getOrCreate()

    spark.conf.set("fs.s3a.access.key", cephAccessKey)
    spark.conf.set("fs.s3a.secret.key", cephSecretKey)
    spark.conf.set("fs.s3a.endpoint", cephEndpoint)
    spark.conf.set("fs.s3a.connection.ssl.enabled", "false")

    var df: DataFrame = null

    if (types == "parquet") {
      df = spark.read
        .parquet(path)
    }

    if (types == "csv") {

      df = spark.read
        .option("header", header)
        .option("inferSchema", "true")
        .option("delimiter", delimiter)
        .csv(path)
    }

    if (types == "json") {
      df = spark.read
        .json(path)
    }
    df.show()

  }

}
