package ru.redcarpet;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AppConsumer {
    @KafkaListener(topics = "my-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String message) {
        System.out.println("Получено: " + message);
    }
}
