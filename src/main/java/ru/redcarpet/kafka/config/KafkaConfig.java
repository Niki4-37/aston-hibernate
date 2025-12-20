package ru.redcarpet.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import ru.redcarpet.kafka.dto.KafkaUser;
import ru.redcarpet.util.AppConst;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Autowired
    private KafkaConfigProperties props;

    @Bean
    public ProducerFactory<String, KafkaUser> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            props.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            props.getProducer().getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            org.springframework.kafka.support.serializer.JacksonJsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, KafkaUser> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, KafkaUser> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                   props.getBootstrapServers());
        config.put(ConsumerConfig.GROUP_ID_CONFIG,
                   props.getConsumer().getGroupId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                   props.getConsumer().getAutoOffsetReset());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                   props.getConsumer().getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                   props.getConsumer().getValueDeserializer());
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "ru.redcarpet.kafka.dto");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaUser>
        kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, KafkaUser> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(AppConst.TOPIC)
                           .partitions(2)
                           .replicas(1)
                           .build();
    }

}
