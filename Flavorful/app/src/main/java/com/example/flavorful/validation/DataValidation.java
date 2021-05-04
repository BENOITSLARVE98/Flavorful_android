package com.example.flavorful.validation;

import android.graphics.Color;
import android.widget.EditText;

import com.example.flavorful.MainActivity;
import com.example.flavorful.R;

import java.util.regex.Pattern;

public class DataValidation {

    public boolean isValidEmail(String email) {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    public boolean isValidPassword(String password) {

        return Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")
                .matcher(password).matches();
    }

    public void validationError(String errorMessage, EditText editText) {
        editText.setText("");
        editText.setHint(errorMessage);
        editText.setHintTextColor(Color.parseColor("#DB504A"));
    }

}
