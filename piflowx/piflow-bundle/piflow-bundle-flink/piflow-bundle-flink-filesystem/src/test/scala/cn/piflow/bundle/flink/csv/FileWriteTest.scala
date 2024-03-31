package cn.piflow.bundle.flink.csv

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class FileWriteTest {

  @Test
  def testFlow(): Unit = {
    val file = "src/test/resources/file/FileWrite.json"
    BaseTest.testFlow(file)
  }

}
