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

package cn.piflow.spark.spark

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.{FileUtil, OptionUtil}
import cn.piflow.util.PropertyUtil
import com.alibaba.fastjson2.JSON
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.h2.tools.Server

object TestBase {

  def testFlow(filePath: String): Unit = {
    // parse flow json
    val flowJsonStr = FileUtil.fileReader(filePath)
    val map = OptionUtil.getAny(JSON.parseObject(flowJsonStr)).asInstanceOf[Map[String, Any]]
    println(map)

    // create flow
    val flowBean = FlowBean.apply[DataFrame](map)
    val flow = flowBean.constructFlow()

    Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "50001").start()

    // execute flow
    val spark = SparkSession
      .builder()
      .master("local[*]")
      .appName("MaxMinNormalizationTest")
      .config("spark.driver.memory", "1g")
      .config("spark.executor.memory", "2g")
      .config("spark.cores.max", "2")
      .config("hive.metastore.uris", PropertyUtil.getPropertyValue("hive.metastore.uris"))
      .enableHiveSupport()
      .getOrCreate()

    val process = Runner
      .create[DataFrame]()
      .bind(classOf[SparkSession].getName, spark)
      .bind("checkpoint.path", "")
      .bind("debug.path", "")
      .start(flow)

    process.awaitTermination()
    val pid = process.pid()
    println(pid + "!!!!!!!!!!!!!!!!!!!!!")
    spark.close()
  }
}
