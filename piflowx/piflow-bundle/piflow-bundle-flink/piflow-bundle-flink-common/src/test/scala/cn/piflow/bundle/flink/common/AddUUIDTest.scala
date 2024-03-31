package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class AddUUIDTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/common/uuid.json"
    BaseTest.testFlow(file)
  }

}
