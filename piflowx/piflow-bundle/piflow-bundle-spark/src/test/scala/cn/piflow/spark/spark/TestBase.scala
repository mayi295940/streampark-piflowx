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
