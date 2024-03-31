package cn.piflow.bundle.beam.jdbc

import cn.piflow.bundle.beam.test.BaseTest
import org.junit.Test

class JDBCWriteTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/jdbc/JDBCWrite.json"
    BaseTest.testFlow(file)
  }

}
