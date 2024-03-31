package cn.piflow.bundle.flink.kafka

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class UpsertKafkaReadTest {

  @Test
  def testFlow(): Unit = {

    // parse flow json
    val file = "src/test/resources/kafka/upsert_kafka_read.json"
    BaseTest.testFlow(file)
  }

}
