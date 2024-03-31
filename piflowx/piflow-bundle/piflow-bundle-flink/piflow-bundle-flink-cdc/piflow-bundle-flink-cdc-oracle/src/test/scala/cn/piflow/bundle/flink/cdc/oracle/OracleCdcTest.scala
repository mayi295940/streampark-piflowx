package cn.piflow.bundle.flink.cdc.oracle

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class OracleCdcTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/cdc/oracle/OracleCdc.json"
    BaseTest.testFlow(file)
  }

}
