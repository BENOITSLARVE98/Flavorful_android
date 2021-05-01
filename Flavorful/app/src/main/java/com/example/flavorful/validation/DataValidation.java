package com.example.flavorful.validation;

import java.util.regex.Pattern;

public class DataValidation {

    public boolean isValidEmail(String email) {
        String emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\\\.[A-Za-z]{2,4}+";

        //return Pattern.compile("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}").matcher(email).matches();

//        if(email.matches(emailPattern)) {
//            return true;
//        } else {
//            return false;
//        }

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public boolean isValidPassword(String password) {
        //String passwordPattern = "^(?=.*[a-z])(?=.*[$@$#!%*?&])[A-Za-z\\\\d$@$#!%*?&]{8,}";

//        if(password.matches(passwordPattern)) {
//            return true;
//        } else {
//            return false;
//        }

        return Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
                .matcher(password).matches();
    }


}
