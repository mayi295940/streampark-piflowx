package cn.piflow.bundle.flink.hive

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment
import org.apache.flink.table.catalog.hive.HiveCatalog
import org.junit.Test

class TestMainFunction {

  @Test
  def testFlow(): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val tableEnv = StreamTableEnvironment.create(env)

    // connect hive by flink
    val name = "myhive"
    val defaultDatabase = "mydatabase"
    val hiveConfDir = "/piflow-configure/hive-conf"

    val database: String = "database";
    val table: String = "table";

    val hive = new HiveCatalog(name, defaultDatabase, hiveConfDir)
    tableEnv.registerCatalog("myhive", hive)

    // set the HiveCatalog as the current catalog of the session
    tableEnv.useCatalog("myhive")
    tableEnv.useDatabase(database)

    // save data to hive
    tableEnv.executeSql("insert into stu select 11,'wangwu'")
  }

}
