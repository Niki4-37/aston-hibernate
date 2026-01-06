package ru.redcarpet.kafka.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaStatusEventHandler {

    private final static Logger log = LoggerFactory.getLogger(KafkaStatusEventHandler.class);

    @EventListener
    public void handleKafkaStatus(KafkaStatusEvent event) {
        log.info("Received Kafka status event: available={}", event.isAvailable());
    }
}
