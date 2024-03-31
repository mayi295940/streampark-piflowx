package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class CsvStringParserTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/file/CsvStringParser.json"
    BaseTest.testFlow(file)
  }

}
