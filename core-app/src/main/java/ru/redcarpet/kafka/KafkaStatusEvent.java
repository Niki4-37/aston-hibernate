package ru.redcarpet.kafka;

import org.springframework.context.ApplicationEvent;

public class KafkaStatusEvent extends ApplicationEvent {

    private final boolean isAvailable;

    public KafkaStatusEvent(Object source, boolean isAvailable) {
        super(source);
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() { return isAvailable; }
}
