package ru.redcarpet;

import java.io.Serializable;
import java.time.Instant;

public record KafkaUserDto(
    String operation,
    String email,
    Long userId,
    Instant timestamp
) implements Serializable  { }
