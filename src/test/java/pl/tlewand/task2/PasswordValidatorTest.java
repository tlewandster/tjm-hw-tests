package pl.tlewand.task2;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordValidatorTest {

    @ParameterizedTest
    @CsvSource({
            "Password1, true",
            "pass, false",
            "password, false",
            "password1, false",
            "PASSWORD1, true",
            "Pass1234, true",
            "Aa1, false"
    })
    void shouldBeValidatePassword(String password, boolean expected) {
        assertEquals(expected, PasswordValidator.isValid(password));
    }
}
