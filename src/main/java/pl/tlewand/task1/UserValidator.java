package pl.tlewand.task1;

public class UserValidator {

    static void validateEmail(String email) {
        if (!email.contains("@")) {
            throw new IllegalArgumentException("The email address does not contain the @ symbol");
        } else if (email.isBlank()) {
            throw new IllegalArgumentException("The email is empty string");
        } else if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")){
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}

