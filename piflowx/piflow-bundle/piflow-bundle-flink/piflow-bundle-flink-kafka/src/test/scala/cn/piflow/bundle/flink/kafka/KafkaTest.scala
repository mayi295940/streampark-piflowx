package cn.piflow.bundle.flink.kafka

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class KafkaTest {

  @Test
  def testFlow(): Unit = {

    // parse flow json
    val file = "src/test/resources/kafka/kafka.json"
    BaseTest.testFlow(file)
  }

  @Test
  def testFlow2(): Unit = {

    // parse flow json
    val file = "src/test/resources/kafka/kafka2.json"
    BaseTest.testFlow(file)
  }

}
