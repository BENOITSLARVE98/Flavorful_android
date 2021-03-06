package com.example.flavorful;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flavorful.validation.DataValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    public static final String value = "loginActivity";

    EditText emailText;
    EditText passwordText;

    Intent discoverIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).hide();

        //Intent to go to sign up page
        Intent signUpActivityIntent = new Intent(this, SignUpActivity.class);
        signUpActivityIntent.putExtra("key", value);

        //Intent to go to Discover page
        discoverIntent = new Intent(this, MainActivity.class);
        discoverIntent.putExtra("key", value);


        findViewById(R.id.sign_up_nav).setOnClickListener(v -> {
            // Load sign up activity
            startActivity(signUpActivityIntent);
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Views and Inputs
        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);

        //Log in button pressed
        findViewById(R.id.login_btn).setOnClickListener(v -> signIn(emailText.getText().toString(), passwordText.getText().toString()));


    }

    private void signIn(String email, String password) {
        if (validateAllFields()) {

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "signInWithEmail:success");

                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user != null){
                                    //Load Discover page
                                    startActivity(discoverIntent);
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "signInWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Invalid email or password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Log.i("TAG","missing something");
        }
    }

    private boolean validateAllFields() {

        boolean result = true;
        DataValidation validation = new DataValidation();

        //Email
        DataValidation validator = new DataValidation();
        String email = emailText.getText().toString().trim();
        if (!validator.isValidEmail(email)) {
            result = false;
            validation.validationError("Invalid email", emailText);
        }

        //Password
        String password = passwordText.getText().toString().trim();
        if (!validator.isValidPassword(password)) {
            result = false;
            validation.validationError("Invalid Passsowrd", passwordText);
        }

        return result;
    }
}