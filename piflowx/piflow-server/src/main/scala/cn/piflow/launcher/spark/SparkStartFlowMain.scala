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
