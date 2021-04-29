package com.example.flavorful;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class LoginActivity extends AppCompatActivity {

    public static final String value = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Intent to go to sign up page
        Intent signUpActivityIntent = new Intent(this, SignUpActivity.class);
        signUpActivityIntent.putExtra("key", value);

        findViewById(R.id.sign_up_nav).setOnClickListener(v -> {
            // Load login activity
            startActivity(signUpActivityIntent);
        });


    }
}