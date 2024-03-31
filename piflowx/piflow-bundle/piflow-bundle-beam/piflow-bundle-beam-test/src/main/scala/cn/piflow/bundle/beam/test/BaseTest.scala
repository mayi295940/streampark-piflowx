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
