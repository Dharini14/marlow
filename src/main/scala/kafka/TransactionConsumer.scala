package kafka

import com.marlow.bankingapp.models.Transaction
import io.circe.generic.auto.exportDecoder
import io.circe.parser._
import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

import java.util.{Collections, Properties}
import scala.jdk.CollectionConverters.{IterableHasAsJava, IterableHasAsScala}

object TransactionConsumer extends App {
  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-group")

  val consumer = new KafkaConsumer[String, String](props)
  consumer.subscribe(Collections.singletonList("transactions"))

  while (true) {
    val records = consumer.poll(1000).asScala
    for (record <- records) {
      decode[Transaction](record.value()) match {
        case Right(transaction) => println(s"Processing: $transaction")
        case Left(error) => println(s"Failed to decode transaction: $error")

      }
    }
  }

}