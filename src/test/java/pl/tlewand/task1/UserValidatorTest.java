package pl.tlewand.task1;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {

    @Test
    void throwsIllegalArgumentException_whenEmailNotContainsAtSign() {
        // given
        String email = "nazwadomena.pl";

        // when
        Executable action = () -> UserValidator.validateEmail(email);

        // then
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    void throwsIllegalArgumentException_whenEmailIsEmpty() {
        // given
        String email = "";

        // when
        Executable action = () -> UserValidator.validateEmail(email);

        // then
        assertThrows(IllegalArgumentException.class, action);
    }

    @Test
    void throwsIllegalArgumentException_whenEmailIsNull() {
        // given
        String email = null;

        // when
        Executable action = () -> UserValidator.validateEmail(email);

        // then
        assertThrows(NullPointerException.class, action);
    }

    @Test
    void doesNotThrowsException_whenEmailIsCorrect() {
        // given
        String email = "nazwa@domena.pl";

        // when
        Executable action = () -> UserValidator.validateEmail(email);

        // then
        assertDoesNotThrow(action);
    }
}