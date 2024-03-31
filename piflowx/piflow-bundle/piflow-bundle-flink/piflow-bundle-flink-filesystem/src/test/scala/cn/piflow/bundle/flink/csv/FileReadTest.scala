package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class FileReadTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/file/FileRead.json"
    BaseTest.testFlow(file)
  }

}
