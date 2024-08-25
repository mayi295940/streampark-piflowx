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

package cn.piflow.launcher.flink

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.MapUtil
import cn.piflow.util.{FlowFileUtil, JsonUtil}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.RuntimeExecutionMode
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment

import java.io.File

object StartFlinkFlowMain {

  def main(args: Array[String]): Unit = {

    val parameter = ParameterTool.fromArgs(args)

    val flowFileName = parameter.get("pipeline.json", args(0))

    if (StringUtils.isEmpty(flowFileName)) {
      throw new RuntimeException("pipeline.json is empty")
    }

    println(flowFileName)

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
    val flowBean = FlowBean.apply[Table](map)
    val flow = flowBean.constructFlow(false)

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val environment = flow.getEnvironment

    val runtimeMode = MapUtil.get(environment, "runtimeMode", "").asInstanceOf[String]
    if (RuntimeExecutionMode.BATCH.name().equalsIgnoreCase(runtimeMode)) {
      env.setRuntimeMode(RuntimeExecutionMode.BATCH)
    } else {
      env.setRuntimeMode(RuntimeExecutionMode.STREAMING)
    }

    val tableEnv = StreamTableEnvironment.create(env)
    println("StreamExecutionEnvironment is " + env + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    val process = Runner
      .create[Table]()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .bind(classOf[StreamTableEnvironment].getName, tableEnv)
      // .bind("checkpoint.path", ConfigureUtil.getCheckpointPath())
      // .bind("debug.path", ConfigureUtil.getDebugPath())
      .bind("environmentVariable", flowBean.environmentVariable)
      .start(flow);

    println("pid is " + process.pid + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    env.execute(flow.getFlowName)
  }

}
