package cn.piflow.bundle.flink.jdbc

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class JDBCWriteTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/jdbc/JDBCWrite.json"
    BaseTest.testFlow(file)
  }

}
