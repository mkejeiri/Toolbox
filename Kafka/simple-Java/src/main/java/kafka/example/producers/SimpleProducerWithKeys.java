package kafka.example.producers;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class SimpleProducerWithKeys {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        final Logger logger = LoggerFactory.getLogger(SimpleProducerWithKeys.class);

        final String bootstrapServers = "127.0.0.1:9092";
        //four keys
        final Integer numberOfKeys = 4;

        // create Producer properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // create the producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //ten messages to be sent!
        for (int i = 1; i <= 10; i++) {
            //here we have two partition and 4 different keys
            String topic = "third_topic";
            String value = "hello message " + Integer.toString(i);
            String key = "key_" + Integer.toString(i % numberOfKeys == 0 ? numberOfKeys : i % numberOfKeys);

            // create a producer record
            ProducerRecord<String, String> record =
                    new ProducerRecord<String, String>(topic, key, value);

            logger.info("Key: " + key); // log the key
            // key_1 is going to  partition 1
            // key_2 partition 0
            // key_3 partition 1
            // key_4 partition 0

            // send data - asynchronous
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    // executes every time a record is successfully sent or an exception is thrown
                    if (e == null) {
                        // the record was successfully sent
                        logger.info("Received new metadata. \n" +
                                "Topic:" + recordMetadata.topic() + "\n" +
                                "Partition: " + recordMetadata.partition() + "\n" +
                                "Offset: " + recordMetadata.offset() + "\n" +
                                "Timestamp: " + recordMetadata.timestamp());
                    } else {
                        logger.error("Error while producing", e);
                    }
                }
            })
              // block the .send() to make it synchronous - bad practice - don't do this in production!
              //we need  to 'throws ExecutionException, InterruptedException'
              .get();
        }
        // flush data
        producer.flush();
        // flush and close producer
        producer.close();
    }
}
