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

package cn.piflow.bundle.spark

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.FileUtil
import cn.piflow.util.{JsonUtil, PropertyUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object TestBase {

  def testFlow(filePath: String): Unit = {
    // parse flow json
    val flowJsonStr = FileUtil.fileReader(filePath)
    val map = JsonUtil.jsonToMap(flowJsonStr)
    println(map)

    // create flow
    val flowBean = FlowBean.apply[StreamingContext, DataFrame, DStream[_]](map)
    val flow = flowBean.constructFlow()

    // Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "50001").start()

    val sparkSessionBuilder = SparkSession.builder().appName(flowBean.name)
    if (PropertyUtil.getPropertyValue("hive.metastore.uris") != null) {
      sparkSessionBuilder
        .config("hive.metastore.uris", PropertyUtil.getPropertyValue("hive.metastore.uris"))
        .enableHiveSupport()
    }

    val spark = sparkSessionBuilder
      .master("local[*]")
      .appName("MaxMinNormalizationTest")
      .config("spark.driver.memory", "1g")
      .config("spark.executor.memory", "2g")
      .config("spark.cores.max", "2")
      .getOrCreate()

    val process = Runner
      .create[StreamingContext, DataFrame, DStream[_]]()
      .bind(classOf[SparkSession].getName, spark)
      .bind("checkpoint.path", "")
      .bind("debug.path", "")
      .bind("applicationId", spark.sparkContext.applicationId)
      .start(flow)

    val pid = process.pid()
    println(pid + "!!!!!!!!!!!!!!!!!!!!!")
    process.awaitTermination()
    spark.close()
  }
}
