package ru.redcarpet.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.DisconnectException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import ru.redcarpet.KafkaUserDto;

@Configuration
@EnableKafka
public class KafkaConfig {

    private final KafkaConfigProperties props;

    public KafkaConfig(KafkaConfigProperties props) {
        this.props = props;
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
                  "ru.redcarpet");

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
}
