package example.consumers;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
public class SimpleConsumer {
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(SimpleConsumer.class.getName());

        final String bootstrapServers = "127.0.0.1:9092";
        final String groupId = "mygroup-java-client";
        final String topic = "third_topic";

        // create consumer configs
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // create consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        // subscribe consumer to our topic(s)
        consumer.subscribe(Arrays.asList(topic));
        //OR all topics
        //consumer.subscribe(Arrays.asList("first_topic","second_topic","third_topic","fourth_topic"));
        //or Alternatively subscribe to one topic
        //consumer.subscribe(Collections.singleton(topic));

        // poll for new data
        while(true){
            ConsumerRecords<String, String> records =
                    //poll with a timeout in milliseconds! with a duration object!
                    consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records){
                logger.info("Key: " + record.key() + ", Value: " + record.value());
                logger.info("Partition: " + record.partition() + ", Offset:" + record.offset());
            }
        }
    }
}
