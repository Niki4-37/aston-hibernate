package ru.redcarpet.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.redcarpet.dto.KafkaUser;
import ru.redcarpet.util.AppConst;

@Component
public class UserServiceListener {

    private static final Logger log = LoggerFactory.getLogger(UserServiceListener.class);

    @KafkaListener(
            topics = AppConst.TOPIC,
            groupId = "user-service-consumer")
    public void handleUserServiceEvent(ConsumerRecord<String, KafkaUser> record) {
        log.info("Recieved user event {}", record.value().getOperation());
    }
}
