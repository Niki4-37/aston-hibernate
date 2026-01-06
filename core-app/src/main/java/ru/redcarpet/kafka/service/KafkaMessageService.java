package ru.redcarpet.kafka.service;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import ru.redcarpet.KafkaUserDto;
import ru.redcarpet.OperationType;
import ru.redcarpet.database.dto.UserDto;
import ru.redcarpet.kafka.exception.KafkaSendException;
import ru.redcarpet.kafka.support.KafkaConnectionManager;

@Service
public class KafkaMessageService {

    private final KafkaTemplate<String, KafkaUserDto> kafkaTemplate;
    private final KafkaConnectionManager connectionManager;
    @Value("${spring.kafka.topic}")
    private String topic;

    private final static Logger log = LoggerFactory.getLogger(KafkaMessageService.class);
    
    public KafkaMessageService(
        KafkaTemplate<String, KafkaUserDto> kafkaTemplate,
        KafkaConnectionManager connectionManager
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.connectionManager = connectionManager;
    }

    public void sendToKafka(OperationType type, UserDto userDto) {
        sendToKafkaWithCircuitBreaker(type, userDto);
    }

    @CircuitBreaker(name = "kafkaSend", fallbackMethod = "kafkaFallback")
    public void sendToKafkaWithCircuitBreaker(OperationType type, UserDto userDto) {
                
        try {
            checkKafkaAvailability();
            var kafkaUser = new KafkaUserDto(
                type.toString(), 
                userDto.email(), 
                userDto.id(), 
                Instant.now());
            kafkaTemplate.send(
                    topic,
                    String.valueOf(userDto.id()),
                    kafkaUser)
                        .get(10, TimeUnit.SECONDS);
            log.info("Kafka has sent message");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaSendException("Kafka send interrupted", e);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            log.error("Kafka: failed to send {} event, cause {}", type, cause);
            throw new KafkaSendException("Kafka send failed", cause);
        } catch (TimeoutException e) {
            Throwable cause = e.getCause();
            log.error("Can't connect to Kafka, cause {}", cause);
            throw new KafkaSendException("Can't connect to Kafka", cause);
        } catch (Exception e) {
            log.error("Unexpected error while sending to Kafka: {}", e.getMessage());
            throw new KafkaSendException("Unexpected error", e);
        } 
    }

    public void kafkaFallback(OperationType type, UserDto user, Exception ex) {
        log.warn("CircuitBreaker triggered for Kafka send. Operation: {}, User ID: {}. Reason: {}", 
            type, user.id(), ex.getMessage());
    }

    private void checkKafkaAvailability() throws RuntimeException {
        if (connectionManager.checkKafkaAvailability())  return;
        log.error("Kafka is currently unavailable");
        throw new KafkaSendException(
            "Kafka is currently unavailable", 
            new RuntimeException("Unavailable Kafka server"));
    }
}
