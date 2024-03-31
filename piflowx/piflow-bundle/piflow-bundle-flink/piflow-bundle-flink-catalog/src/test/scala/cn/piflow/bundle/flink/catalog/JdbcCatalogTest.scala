package cn.piflow.bundle.flink.catalog

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class JdbcCatalogTest {

  @Test
  def testFlow(): Unit = {
    // parse flow json
    val file = "src/test/resources/catalog/JdbcCatalog.json"
    BaseTest.testFlow(file)
  }

}
