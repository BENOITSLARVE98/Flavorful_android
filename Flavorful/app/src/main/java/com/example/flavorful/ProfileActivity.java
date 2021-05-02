package com.example.flavorful;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.flavorful.validation.DataValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    AuthManager authManager = new AuthManager();

    public static final int REQUEST_DATA= 101;
    Uri returnUri;
    ImageView imageView;
    TextView nameLabelText;
    EditText nameText;
    EditText emailText;

    Intent loginIntent;
    public static final String value = "Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Intent to go to login page
        loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("key", value);

        Objects.requireNonNull(Objects.requireNonNull(getSupportActionBar())).hide();

        retrieveUserInfo();

        //Views
        imageView = findViewById(R.id.pro_profileImage_view);
        imageView.setOnClickListener(imageClickListener);
        nameLabelText = findViewById(R.id.pro_profileName_text);

        nameText = findViewById(R.id.name_editText);
        emailText = findViewById(R.id.email_editText);


        //Save button pressed
        findViewById(R.id.saveChanges_btn).setOnClickListener(v -> saveChanges());

        //Edit password button pressed
        findViewById(R.id.editPassword_btn).setOnClickListener(v -> editPassword());

        //Sign out button pressed
        findViewById(R.id.signOut_btn).setOnClickListener(v -> signOut());

        //Delete user button pressed
        findViewById(R.id.deleteUser_btn).setOnClickListener(v -> deleteUser());

    }

    private void signOut() {
        authManager.signOut();
        //Go back to login page
        startActivity(loginIntent);
    }

    private void deleteUser() {
        currentUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "User account deleted.");

                            //Go back to login page
                            startActivity(loginIntent);
                        }
                    }
                });

        //Delete user from FireStore
        db.collection("users").document(currentUser.getUid()).delete();

        //Delete user profile image
        StorageReference storageProfileRef = storageRef.child("profileImages").child(currentUser.getUid());
        storageProfileRef.delete();
    }

    private void editPassword() {
        DataValidation validator = new DataValidation();

        EditText newPasswordText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage("Enter new Password")
                .setView(newPasswordText)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String passwordInput = newPasswordText.getText().toString();
                        Log.d("onclick","editext value is: "+ passwordInput);

                        //Update password once save button is clicked
                        currentUser.updatePassword(passwordInput);

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        //change color of the button
        dialog.setOnShowListener( new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.custom_red));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.custom_red));
            }
        });

        //input text changed listener
        newPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //Validate input
                if(validator.isValidPassword(s.toString())) {
                    //If password typed is valid enable save button
                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        dialog.show();

        // Initially disable the positive button
        ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void saveChanges() {

        if (validateAllFields()) {
            //Update
            //Do image validation here
            if (returnUri == null) {
                //If returned Uri here is empty it means they did not update profile image
                Map<String, Object> users = new HashMap<>();
                users.put("name", nameText.getText().toString()); //Name
                users.put("email", emailText.getText().toString()); //Email
                db.collection("users").document(currentUser.getUid()).update(users);
            } else {
                //image was updated by user
                authManager.saveImage(returnUri); //update image in both FireStore and storage

                Map<String, Object> users = new HashMap<>();
                users.put("name", nameText.getText().toString()); //Name
                users.put("email", emailText.getText().toString()); //Email
                db.collection("users").document(currentUser.getUid()).update(users);

            }
        }

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
            imageView.setImageURI(returnUri);
        }
    }

    public void retrieveUserInfo() {

        if(currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("TAG", "Listen failed.", e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                Log.d("TAG", "Current data: " + snapshot.getData());

                                //Set views
                                //Set image
                                String imageUrl = snapshot.getData().get("profileImageUrl").toString();
                                final long ONE_MEGABYTE = 1024 * 1024;
                                StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);
                                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        // Data for "images/island.jpg" is returns, use this as needed
                                        Bitmap bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                        imageView.setImageBitmap(bm);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });

                                nameLabelText.setText(snapshot.getData().get("name").toString());
                                nameText.setText(snapshot.getData().get("name").toString());
                                emailText.setText(snapshot.getData().get("email").toString());


                            } else {
                                Log.d("TAG", "Current data: null");
                            }
                        }
                    });


        }
    }

    private boolean validateAllFields() {

        boolean result = true;

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

        return result;
    }
}