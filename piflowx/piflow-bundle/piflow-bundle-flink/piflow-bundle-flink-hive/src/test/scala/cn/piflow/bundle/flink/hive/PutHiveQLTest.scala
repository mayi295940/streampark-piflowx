package cn.piflow.bundle.flink.hive

import cn.piflow.Runner
import cn.piflow.conf.bean.FlowBean
import cn.piflow.conf.util.FileUtil
import cn.piflow.util.JsonUtil
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.Table
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.catalog.hive.HiveCatalog
import org.h2.tools.Server
import org.junit.Test

class PutHiveQLTest {

  @Test
  def testFlow(): Unit = {

    // parse flow json
    val file = "src/main/resources/flow/hive/PutHiveQL.json"
    val flowJsonStr = FileUtil.fileReader(file)
    val map = JsonUtil.jsonToMap(flowJsonStr)
    println(map)

    // create flow
    val flowBean = FlowBean.apply[Table](map)
    val flow = flowBean.constructFlow()

    val h2Server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "50001").start()

    val name = "myhive"
    val defaultDatabase = "mydatabase"
    val hiveConfDir = "/piflow-configure/hive-conf"

    // execute flow
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    val hive = new HiveCatalog(name, defaultDatabase, hiveConfDir)
    tableEnv.registerCatalog("myhive", hive)

    // set the HiveCatalog as the current catalog of the session
    tableEnv.useCatalog("myhive")

    val process = Runner
      .create[Table]()
      .bind(classOf[StreamExecutionEnvironment].getName, env)
      .bind("checkpoint.path", "")
      .bind("debug.path", "")
      .start(flow)

    process.awaitTermination()
    val pid = process.pid()
    println(pid + "!!!!!!!!!!!!!!!!!!!!!")
  }

}
