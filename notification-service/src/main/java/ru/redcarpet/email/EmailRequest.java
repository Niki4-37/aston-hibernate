package ru.redcarpet.email;

public record EmailRequest(String to, String subject, String body) { }
