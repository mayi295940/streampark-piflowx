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

package cn.piflow.launcher.spark

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.FileUtil
import cn.piflow.util.{JsonUtil, PropertyUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream

object SparkStartTestMain {

  def main(args: Array[String]): Unit = {

    val jsonPath = args(0)
    val flowJsonStr = FileUtil.fileReader(jsonPath)
    val map = JsonUtil.jsonToMap(flowJsonStr)
    println(map)

    // create flow
    val flowBean = FlowBean[StreamingContext, DataFrame, DStream[_]](map)
    val flow = flowBean.constructFlow(false)

    // execute flow
    val sparkSessionBuilder = SparkSession.builder().appName(flowBean.name)
    if (PropertyUtil.getPropertyValue("hive.metastore.uris") != null) {
      sparkSessionBuilder
        .config("hive.metastore.uris", PropertyUtil.getPropertyValue("hive.metastore.uris"))
        .enableHiveSupport()
    }

    sparkSessionBuilder.config("spark.driver.memory", "1g")
      .config("spark.executor.memory", "1g")
      .config("spark.cores.max", "2")
      .master("local[*]")

    val spark = sparkSessionBuilder.getOrCreate()
    val applicationId = spark.sparkContext.applicationId

    val process = Runner
      .create[StreamingContext, DataFrame, DStream[_]]()
      .bind(classOf[SparkSession].getName, spark)
      // .bind("checkpoint.path", ConfigureUtil.getCheckpointPath())
      // .bind("debug.path", ConfigureUtil.getDebugPath())
      .bind("environmentVariable", flowBean.environmentVariable)
      .bind("applicationId", applicationId)
      .start(flow)

    process.awaitTermination()
    spark.close()

    /*new Thread( new WaitProcessTerminateRunnable(spark, process)).start()
    (applicationId,process)*/
  }

}
