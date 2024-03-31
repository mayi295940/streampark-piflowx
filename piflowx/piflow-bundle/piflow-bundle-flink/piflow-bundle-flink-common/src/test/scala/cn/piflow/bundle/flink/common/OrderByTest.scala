package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.test.BaseTest
import org.apache.flink.api.common.RuntimeExecutionMode
import org.junit.Test

class OrderByTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/common/OrderBy.json"
    BaseTest.testFlow(file, RuntimeExecutionMode.BATCH)
  }

}
