package com.example.flavorful;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    public static final int REQUEST_DATA= 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Views and Inputs
        ImageView profileImageView = findViewById(R.id.profile_image);
        profileImageView.setOnClickListener(imageClickListener);

        EditText emailText = findViewById(R.id.email_text);
        EditText passwordText = findViewById(R.id.password_text);

        //Sign Up button pressed
        findViewById(R.id.signup_btn).setOnClickListener(v -> createAccount(emailText.getText().toString(), passwordText.getText().toString()));

    }

    View.OnClickListener imageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Start Intent to request image data from device
            Intent requestData = new Intent();
            requestData.setAction(Intent.ACTION_PICK);
            requestData.setType("image/jpeg");
            startActivityForResult(requestData, REQUEST_DATA);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //The requested data is sent back in this method's intent
        //Retrieve image data
        if(resultCode != RESULT_OK) {
            //Exit without doing anything
        } else {
            //Get the file's content URi from incoming intent
            assert data != null;
            Uri returnUri = data.getData();
        }
    }

    private void createAccount(String email, String password) {

    }
}