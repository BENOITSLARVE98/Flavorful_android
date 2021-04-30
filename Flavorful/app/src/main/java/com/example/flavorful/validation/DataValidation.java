package com.example.flavorful.validation;

public class DataValidation {

    public boolean isValidEmail(String email) {
        String emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,4}";

        if(email.matches(emailPattern)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[a-z])(?=.*[$@$#!%*?&])[A-Za-z\\\\d$@$#!%*?&]{8,}";

        if(password.matches(passwordPattern)) {
            return true;
        } else {
            return false;
        }
    }


}
