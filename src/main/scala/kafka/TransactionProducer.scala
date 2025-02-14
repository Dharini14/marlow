package kafka

import com.marlow.bankingapp.models.Transaction
import io.circe.generic.auto.exportEncoder
import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer

import java.util.Properties

object TransactionProducer {
  private val props = new Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

  private val producer = new KafkaProducer[String, String](props)

  /** Send transaction details of Kafka */
  def sendTransaction(transaction: Transaction): Unit = {
    val message = transaction.asJson.noSpaces
    val record = new ProducerRecord[String, String]("transactions", message)
    producer.send(record)
  }

}