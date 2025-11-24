package ru.redcarpet.util;

public final class UserValidator {

    private UserValidator() {}

    public static boolean validate(String userDescription) {
        if (userDescription == null) return false;
        String[] values = userDescription.split("\\s+");
        if(values.length < 3) return false;
        return checkName(values[0]) && checkEmail(values[1]) && checkDate(values[2]);
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
