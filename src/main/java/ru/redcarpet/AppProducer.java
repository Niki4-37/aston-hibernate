package ru.redcarpet;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppProducer {
    private final KafkaTemplate<String, String> template;

    public AppProducer(KafkaTemplate<String, String> template) {
        this.template = template;
    }

    public void send(String topic, String msg) {
        template.send(topic, msg);
    }
}
