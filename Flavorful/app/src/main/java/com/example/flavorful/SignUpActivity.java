package com.example.flavorful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.flavorful.validation.DataValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    public static final int REQUEST_DATA= 101;
    ImageView profileImageView;
    Uri returnUri;
    EditText nameText;
    EditText emailText;
    EditText passwordText;

    Intent discoverIntent;
    public static final String value = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).hide();

        //Intent to go to Discover page
        discoverIntent = new Intent(this, MainActivity.class);
        discoverIntent.putExtra("key", value);


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //Views and Inputs
        profileImageView = findViewById(R.id.profile_image);
        profileImageView.setOnClickListener(imageClickListener);

        nameText = findViewById(R.id.name_text);
        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);

        //Sign Up button pressed
        findViewById(R.id.login_btn).setOnClickListener(v -> createAccount(returnUri,
                nameText.getText().toString(), emailText.getText().toString(),
                passwordText.getText().toString()));

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
            returnUri = data.getData();

            //Display selected image
            profileImageView.setImageURI(returnUri);
        }
    }

    private void createAccount(Uri image, String name, String email, String password) {
        if (validateAllFields()) {
            //If all fields are valid, create account and save user info
            AuthManager authManager = new AuthManager();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("TAG", "createUserWithEmail:success");

                                currentUser = task.getResult().getUser();
                                if(currentUser != null){
                                    //Save user to FireStore user collection
                                    authManager.saveUser(image, name, email);

                                }
                                //Load Discover page
                                startActivity(discoverIntent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("TAG", "createUserWithEmail:failure", task.getException());
                                //Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                //Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }
                        }
                    });
        } else {
            Log.i("TAG","missing something");
        }

    }

    private boolean validateAllFields() {

        boolean result = true;

        //Image
        if (returnUri == null) {
            result = false;
        }
        //Name
        if (nameText.getText().toString().isEmpty() ) {
            result = false;
        }
        //Email
        DataValidation validator = new DataValidation();
        String email = emailText.getText().toString().trim();
        if (!validator.isValidEmail(email)) {
            result = false;
        }

        //Password
        String password = passwordText.getText().toString().trim();
        if (!validator.isValidPassword(password)) {
            result = false;
        }

        return result;
    }
}