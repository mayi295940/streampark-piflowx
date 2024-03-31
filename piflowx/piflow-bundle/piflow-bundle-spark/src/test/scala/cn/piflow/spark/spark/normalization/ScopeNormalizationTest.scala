package cn.piflow.spark.spark.normalization

import cn.piflow.spark.spark.TestBase
import org.junit.Test

class ScopeNormalizationTest {

  @Test
  def ScopeNormalizationFlow(): Unit = {

    // parse flow json
    val filePath = "src/main/resources/flow/normalization/ScopeNormalization.json"
    TestBase.testFlow(filePath)
  }
}
