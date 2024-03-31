package cn.piflow.bundle.flink.kafka

import cn.piflow.bundle.flink.test.BaseTest
import org.junit.Test

class UpsertKafkaWriteTest {

  @Test
  def testFlow(): Unit = {

    // parse flow json
    val file = "src/test/resources/kafka/upsert_kafka_write.json"
    BaseTest.testFlow(file)
  }

}
