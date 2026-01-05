package ru.redcarpet.kafka.dto;

import java.io.Serializable;
import java.time.Instant;

public class KafkaUserDto implements Serializable {
    private String operation;
    private String email;
    private Long userId;
    private Instant timestamp;
    
    public KafkaUserDto(String operation, String email, Long userId, Instant timestamp) {
        this.operation = operation;
        this.email = email;
        this.userId = userId;
        this.timestamp = timestamp;
}
    public KafkaUserDto() {}
    
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

