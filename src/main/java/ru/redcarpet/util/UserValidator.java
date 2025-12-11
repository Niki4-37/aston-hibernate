package ru.redcarpet.util;

import ru.redcarpet.exception.AppException;

public final class UserValidator {

    private UserValidator() {}

    public static void validate(String userDescription) {
        if (userDescription == null) {
            throw new AppException("Empty data");
        };
        String[] values = userDescription.split("\\s+");
        if(values.length < 3) {
            throw new AppException("More information needed, you should fill all fields");
        };
        if (!checkName(values[0])) {
            throw new AppException("Wrong name format");
        }
        if (!checkEmail(values[1])) {
            throw new AppException("Wrong e-mail format");
        }
        if (!checkDate(values[2])) {
            throw new AppException("Wrong date format");
        }
    }

    private static boolean checkName(String name) {
        return name.matches("^[a-zA-Z]+$");
    }

    private static boolean checkEmail(String email) {
        return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    }

    private static boolean checkDate(String dateAsString) {
        return dateAsString.matches("^\\d{2}-\\d{2}-\\d{4}$");
    }

}
