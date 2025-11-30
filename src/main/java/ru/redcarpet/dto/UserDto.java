package ru.redcarpet.dto;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof UserDto other)) return false;

        return Objects.equals(name,     other.name) &&
               Objects.equals(email,    other.email) &&
               Objects.equals(birthDate,other.birthDate) &&
               Objects.equals(createdAt,other.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, birthDate, createdAt);
    }
}
