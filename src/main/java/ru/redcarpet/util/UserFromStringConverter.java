package ru.redcarpet.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ru.redcarpet.dto.UserDto;
import ru.redcarpet.exception.AppException;

public final class UserFromStringConverter {

    private UserFromStringConverter() {}

    public static UserDto convert(String userDescription) {
        if (!UserValidator.validate(userDescription)) {
            throw new AppException("Validation failed");
        }
        String[] values = userDescription.split("\\s+");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate birthDate = LocalDate.parse(values[2], formatter);
        return new UserDto(null, values[0], values[1], birthDate, LocalDate.now());
    }
}
