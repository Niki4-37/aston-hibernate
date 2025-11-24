package ru.redcarpet.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record UserDto(
    Long id,
    String name,
    String email,
    LocalDate birthDate,
    LocalDate createdAt
) {
    public Long getAge() {
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
    }

    @Override
    public String toString() {
        return "[ User with name: "
            + name
            + " email: "
            + email
            + " birthday: "
            + birthDate
            + " age: "
            + getAge()
            + " created at: "
            + createdAt
            + " ]";
    }
 }
