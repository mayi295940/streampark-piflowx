package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.test.BaseTest
import org.apache.flink.api.common.RuntimeExecutionMode
import org.junit.Test

class UnionAllTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/common/unionall.json"
    BaseTest.testFlow(file, RuntimeExecutionMode.STREAMING)
  }

}
