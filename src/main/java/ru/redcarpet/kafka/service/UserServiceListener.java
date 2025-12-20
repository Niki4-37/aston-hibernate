package ru.redcarpet.kafka.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import ru.redcarpet.email.EmailService;
import ru.redcarpet.kafka.dto.KafkaUser;
import ru.redcarpet.kafka.enums.OperationType;
import ru.redcarpet.util.AppConst;

@Component
public class UserServiceListener {

    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(UserServiceListener.class);

    public UserServiceListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = AppConst.TOPIC,
            groupId = "user-service-consumer")
    public void handleUserServiceEvent(ConsumerRecord<String, KafkaUser> record) {
        String operationName = record.value().getOperation();
        log.info("Recieved user event {}", operationName);
        var operationType = OperationType.valueOf(record.value().getOperation());
        StringBuilder message = new StringBuilder();
        switch (operationType) {
            case CREATE : 
                message.append("Hello! Your account on our website has been successfully created");
                break;
            case DELETE :
                message.append("Hello! Your account has been deleted");
                break;
            default : message.append("Something strange has happened, and we're trying to figure it out");
        }
        String subject = operationName + "account";
        emailService.sendEmail(record.value().getEmail(), subject, message.toString());
    }
}
