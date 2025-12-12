package ru.redcarpet.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ru.redcarpet.exception.AppException;

public class UserValidatorTest {
    @ParameterizedTest(name = "{index} => input={0}, expectedMessage={1}")
    @MethodSource("validationProvider")
    void testValidate(String input, String expectedMessage) {
        if (expectedMessage == null) {
            assertDoesNotThrow(() -> UserValidator.validate(input));
        } else {
            AppException exception = assertThrows(AppException.class, 
                () -> UserValidator.validate(input));
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    static Stream<Arguments> validationProvider() {
        return Stream.of(
            Arguments.of(null, "Empty data"),
            Arguments.of("Test", "More information needed, you should fill all fields"),
            Arguments.of("Ivan123 ivan@example.com 01-01-2000", "Wrong name format"),
            Arguments.of("Ivan ivanex 01-01-2000", "Wrong e-mail format"),
            Arguments.of("Ivan ivan@example.com notDate", "Wrong date format"),
            Arguments.of("Ivan ivan@example.com 01-01-2000", null)
        );
    }
}
