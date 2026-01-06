package ru.redcarpet.kafka.support;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class KafkaHealthIndicator implements HealthIndicator{

    private final KafkaConnectionManager connectionManager;

    public KafkaHealthIndicator(KafkaConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public @Nullable Health health() {
        connectionManager.updateListeners();

        return connectionManager.checkKafkaAvailability() 
            ? Health.up().build() 
            : Health.down().withDetail("reason", "Kafka is unavailable").build();
    }
}
