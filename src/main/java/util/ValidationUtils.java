package util;

public class ValidationUtils {
    public static String validatePassword(String password) {
        if(password == null) {
            return "Empty password";
        }

        int score = 0;
        if(password.matches(".*[A-Z].*")) {
            score++;
        }
        if(password.matches(".*\\d.*")) {
            score++;
        }
        if(password.matches(".*[^A-Za-z0-9].*")) {
            score++;
        }
        if(password.length() >= 8) {
            score++;
        }

        if(score >= 2) {
            return null; // Valid
        } else {
            return "Password must include at least 2 of the following: uppercase, digit, " +
                    "special character, be at least 8 characters long.";
        }
    }
}
