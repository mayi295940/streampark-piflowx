package cn.piflow.spark.spark.normalization

import cn.piflow.spark.spark.TestBase
import org.junit.Test

class MaxMinNormalizationTest {

  @Test
  def MaxMinNormalizationFlow(): Unit = {
    val filePath = "src/main/resources/flow/normalization/MaxMinNormalization.json"
    TestBase.testFlow(filePath)
  }
}
