package pl.tlewand.task2;

public class PasswordValidator {

    static boolean isValid(String password){
        return password.matches("^(?=.*\\d)(?=.*[A-Z]).{8,}$");
    }
}
