package ru.redcarpet.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.DisconnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import ru.redcarpet.kafka.dto.KafkaUserDto;
import ru.redcarpet.util.AppConst;

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


    @Bean
    public ConsumerFactory<String, KafkaUserDto> consumerFactory() {
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
        config.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, 
                  "ru.redcarpet.kafka.dto");

        config.putAll(props.getConsumer().getProperties());
           
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaUserDto>
        kafkaListenerContainerFactory() {
            ConcurrentKafkaListenerContainerFactory<String, KafkaUserDto> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
            factory.setConsumerFactory(consumerFactory());
            factory.setCommonErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public NewTopic demoTopic() {
        return TopicBuilder.name(AppConst.TOPIC)
                           .partitions(2)
                           .replicas(1)
                           .build();
    }

    @Bean
    public CommonErrorHandler errorHandler() {
        return new CommonErrorHandler() {
            @Override
            public void handleOtherException(
                Exception thrownException,
                Consumer<?, ?> consumer,
                MessageListenerContainer container,
                boolean batchListener
            ) {
                if (thrownException instanceof DisconnectException) {
                    container.stop();
                }
            }       
        };
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