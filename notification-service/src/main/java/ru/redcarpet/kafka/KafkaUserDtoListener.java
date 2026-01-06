package ru.redcarpet.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import ru.redcarpet.KafkaUserDto;
import ru.redcarpet.OperationType;
import ru.redcarpet.email.EmailService;

@Component
public class KafkaUserDtoListener {
    
    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(KafkaUserDtoListener.class);

    public KafkaUserDtoListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserServiceEvent(ConsumerRecord<String, KafkaUserDto> record) {
        if (record.value() == null) {
            log.warn("Recieved empty data for key = {}", record.key());
            return;
        }
        String operationName = record.value().operation();
        log.info("Recieved user event {}", operationName);
        var operationType = OperationType.valueOf(operationName);
        StringBuilder message = new StringBuilder();
        switch (operationType) {
            case CREATE : 
                message.append("Hello! Your account on our website has been successfully created");
                break;
            case DELETE :
                message.append("Hello! Your account has been deleted");
                break;
            default : 
                message.append("Something strange has happened, and we're trying to figure it out");
                break;
        }
        String subject = operationName + " account";
        emailService.sendEmail(record.value().email(), subject, message.toString());
    }
}
