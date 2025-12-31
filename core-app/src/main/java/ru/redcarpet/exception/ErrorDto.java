package ru.redcarpet.exception;

import java.time.LocalDateTime;

public record ErrorDto (
    String message,
    String errorMessage,
    LocalDateTime errorTime
) { }
