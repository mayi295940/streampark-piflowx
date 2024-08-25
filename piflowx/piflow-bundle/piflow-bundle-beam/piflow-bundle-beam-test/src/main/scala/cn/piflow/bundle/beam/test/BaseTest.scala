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

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.FileUtil
import cn.piflow.util.JsonUtil
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.values.{PCollection, Row}

object BaseTest {

  def testFlow(jsonPath: String): Unit = {

    // parse flow json
    val flowJsonStr = FileUtil.fileReader(jsonPath)
    val map = JsonUtil.jsonToMap(flowJsonStr)
    println(map)

    // create flow
    val flowBean = FlowBean.apply[PCollection[Row]](map)
    val flow = flowBean.constructFlow()
    println(flow)

    // Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "50001").start()

    val options = PipelineOptionsFactory.create
    val pipeline = Pipeline.create(options)

    println("pipeline is " + pipeline + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    val process = Runner
      .create[PCollection[Row]]()
      .bind(classOf[Pipeline].getName, pipeline)
      // .bind("checkpoint.path", ConfigureUtil.getCheckpointPath())
      // .bind("debug.path", ConfigureUtil.getDebugPath())
      .bind("environmentVariable", flowBean.environmentVariable)
      .start(flow);

    println("pid is " + process.pid + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")

    pipeline.run()

  }

}
