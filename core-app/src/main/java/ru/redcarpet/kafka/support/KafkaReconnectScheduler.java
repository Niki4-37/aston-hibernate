package ru.redcarpet.kafka.support;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KafkaReconnectScheduler {

    private final KafkaHealthIndicator healthIndicator;

    public KafkaReconnectScheduler(KafkaHealthIndicator healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    @Scheduled(fixedDelay = 10000)
    public void reconnectIfNeeded() {
       healthIndicator.health();
    }
}
