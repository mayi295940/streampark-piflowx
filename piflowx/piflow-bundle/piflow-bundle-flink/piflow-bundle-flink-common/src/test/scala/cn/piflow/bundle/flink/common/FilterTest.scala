package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class FilterTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/common/filter.json"
    BaseTest.testFlow(file)
  }

}
