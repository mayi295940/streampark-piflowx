package cn.piflow.bundle.flink.common

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class FakerTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/common/Faker.json"
    BaseTest.testFlow(file)
  }

}
