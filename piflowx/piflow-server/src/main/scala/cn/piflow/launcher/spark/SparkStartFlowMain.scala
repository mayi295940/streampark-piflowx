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
import cn.piflow.util.{ConfigureUtil, FlowFileUtil, JsonUtil, PropertyUtil}
import org.apache.spark.sql.{DataFrame, SparkSession}

import java.io.File

object SparkStartFlowMain {

  def main(args: Array[String]): Unit = {

    // val flowJsonencryptAES = args(0)
    // val flowJson = SecurityUtil.decryptAES(flowJsonencryptAES)
    val flowFileName = args(0)

    var flowFilePath = FlowFileUtil.getFlowFileInUserDir(flowFileName)
    val file = new File(flowFilePath)
    if (!file.exists()) {
      flowFilePath = FlowFileUtil.getFlowFilePath(flowFileName)
    }

    val flowJson = FlowFileUtil.readFlowFile(flowFilePath).trim()
    println(flowJson)
    val map = JsonUtil.jsonToMap(flowJson)
    println(map)

    // create flow
    val flowBean = FlowBean[DataFrame](map)
    val flow = flowBean.constructFlow(false)

    // execute flow
    val sparkSessionBuilder = SparkSession.builder().appName(flowBean.name)
    if (PropertyUtil.getPropertyValue("hive.metastore.uris") != null) {
      sparkSessionBuilder
        .config("hive.metastore.uris", PropertyUtil.getPropertyValue("hive.metastore.uris"))
        .enableHiveSupport()
    }

    val spark = sparkSessionBuilder.getOrCreate()
    println(
      "hive.metastore.uris=" + spark.sparkContext.getConf.get("hive.metastore.uris") + "!!!!!!!")

    val process = Runner
      .create[DataFrame]()
      .bind(classOf[SparkSession].getName, spark)
      .bind("checkpoint.path", ConfigureUtil.getCheckpointPath())
      .bind("debug.path", ConfigureUtil.getDebugPath())
      .bind("environmentVariable", flowBean.environmentVariable)
      .start(flow)

    val applicationId = spark.sparkContext.applicationId
    process.awaitTermination()
    spark.close()

    /*new Thread( new WaitProcessTerminateRunnable(spark, process)).start()
    (applicationId,process)*/
  }

}
