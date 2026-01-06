package ru.redcarpet.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import ru.redcarpet.KafkaUserDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final KafkaConfigProperties props;
    private final static Logger log = LoggerFactory.getLogger(KafkaConfig.class);

    public KafkaConfig(KafkaConfigProperties props) { this.props = props; }

    @Bean
    public ProducerFactory<String, KafkaUserDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, 
            props.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            props.getProducer().getKeySerializer());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, 
            org.springframework.kafka.support.serializer.JacksonJsonSerializer.class);

        config.putAll(props.getProducer().getProperties());

        return new DefaultKafkaProducerFactory<>(config);
    }

    /*
        Spring creates topic which uses KafkaProducer
    */
    @Bean
    public NewTopic mainTopic() {
        return TopicBuilder.name(props.getTopic())
                           .partitions(2)
                           .replicas(1)
                           .build();
    }

    @Bean
    public ProducerListener<String, KafkaUserDto> producerListener() {
        return new ProducerListener<>() {
            @Override
            public void onError(ProducerRecord<String, KafkaUserDto> record,
                            RecordMetadata metadata, Exception exception) {
                log.error("Fail send message to Kafka: {}", exception.getMessage());
            }
        };
    }

    @Bean
    public KafkaTemplate<String, KafkaUserDto> kafkaTemplate(
            ProducerFactory<String, KafkaUserDto> pf,
            ProducerListener<String, KafkaUserDto> listener) {
        KafkaTemplate<String, KafkaUserDto> template = new KafkaTemplate<>(pf);
        template.setProducerListener(listener);
        return template;
    }
}