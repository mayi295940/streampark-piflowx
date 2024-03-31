package cn.piflow.launcher.beam

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.util.{FlowFileUtil, JsonUtil}
import org.apache.beam.sdk.Pipeline
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.values.{PCollection, Row}

import java.io.File

object StartBeamFlowMain {

  def main(args: Array[String]): Unit = {

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
    val flowBean = FlowBean.apply[PCollection[Row]](map)
    val flow = flowBean.constructFlow(false)

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
