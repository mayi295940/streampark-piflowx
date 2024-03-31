package cn.piflow.spark.spark.normalization

import cn.piflow.spark.spark.TestBase
import org.junit.Test

class DiscretizationTest {

  @Test
  def DiscretizationFlow(): Unit = {

    // parse flow json
    val filePath = "src/main/resources/flow/normalization/Discretization.json"
    TestBase.testFlow(filePath)
  }
}
