package ru.redcarpet.kafka.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaConfigProperties {

    private String bootstrapServers;
    private Consumer consumer = new Consumer();

    static class Consumer {
        private String groupId;
        private String autoOffsetReset;
        private String keyDeserializer;
        private String valueDeserializer;

        private Map<String, String> properties;

        public String getGroupId() { return groupId; }
        public void setGroupId(String groupId) { this.groupId = groupId; }
        
        public String getAutoOffsetReset() { return autoOffsetReset; }
        public void setAutoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; }
        
        public String getKeyDeserializer() { return keyDeserializer; }
        public void setKeyDeserializer(String keyDeserializer) { this.keyDeserializer = keyDeserializer; }
        
        public String getValueDeserializer() { return valueDeserializer; }
        public void setValueDeserializer(String valueDeserializer) { this.valueDeserializer = valueDeserializer; }

        public Map<String, String> getProperties() { return properties; }
        public void setProperties(Map<String, String> properties) { this.properties = properties; }
    }

    public String getBootstrapServers() { return bootstrapServers; }
    public void setBootstrapServers(String bootstapServers) { this.bootstrapServers = bootstapServers; }

    public Consumer getConsumer() { return consumer; }
    public void setConsumer(Consumer consumer) { this.consumer = consumer; }
}
