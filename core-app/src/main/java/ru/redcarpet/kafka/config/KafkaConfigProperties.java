package ru.redcarpet.kafka.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConfigProperties {

    private String topic;
    private String bootstrapServers;
    private Producer producer = new Producer();

    static class Producer {
        private Boolean autoCreateTopics;
        private String keySerializer;
        private String valueSerializer;

        private Map<String, String> properties;
        
        public Boolean getAutoCreateTopics() { return autoCreateTopics; }
        public void setAutoCreateTopics(Boolean autoCreateTopics) { this.autoCreateTopics = autoCreateTopics; }
        
        public String getKeySerializer() { return keySerializer; }
        public void setKeySerializer(String keySerializer) { this.keySerializer = keySerializer; }
        
        public String getValueSerializer() { return valueSerializer; }
        public void setValueSerializer(String valueSerializer) { this.valueSerializer = valueSerializer; }

        public Map<String, String> getProperties() { return properties; }
        public void setProperties(Map<String, String> properties) { this.properties = properties; }
    }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstapServers) { this.bootstrapServers = bootstapServers; }

    public Producer getProducer() { return producer; }
    public void setProducer(Producer producer) { this.producer = producer; }
}
