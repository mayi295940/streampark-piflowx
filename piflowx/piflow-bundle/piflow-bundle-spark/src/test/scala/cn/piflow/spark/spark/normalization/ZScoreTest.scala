package cn.piflow.spark.spark.normalization

import cn.piflow.spark.spark.TestBase
import org.junit.Test

class ZScoreTest {

  @Test
  def ZScoreFlow(): Unit = {
    // parse flow json
    val filePath = "src/main/resources/flow/normalization/ZScore.json"
    TestBase.testFlow(filePath)
  }
}
