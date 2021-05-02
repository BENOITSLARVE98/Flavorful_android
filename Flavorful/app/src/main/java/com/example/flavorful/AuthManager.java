package com.example.flavorful;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.rpc.context.AttributeContext;

import java.util.HashMap;
import java.util.Map;


public class AuthManager {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    Map<String, Object> users = new HashMap<>();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public void saveUser(Uri image, String name, String email) {

        if(currentUser != null){
            users.put("uid", currentUser.getUid());
            users.put("name", name);
            users.put("email", email);
            users.put("profileImageUrl", "");

            db.collection("users").document(currentUser.getUid())
                    .set(users)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully written!");
                            saveImage(image);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error writing document", e);
                        }
                    });
        }

    }

    public void saveImage(Uri image) {
        StorageReference storageProfileRef = storageRef.child("profileImages").child(currentUser.getUid());
        UploadTask uploadTask = storageProfileRef.putFile(image);

        //Save image to firebase Storage
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return storageProfileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    users.put("profileImageUrl", downloadUri.toString());

                    //Save user to FireStore user collection
                    db.collection("users").document(currentUser.getUid()).update(users);

                } else {
                    // Handle failures
                    // ...
                }
            }
        });

    }

    public void signOut() {
        mAuth.signOut();
    }


//    public Bitmap convertUrlToBitmap(String imageUrl) {
//
//
//        final long ONE_MEGABYTE = 1024 * 1024;
//        StorageReference imageStorageRef = storage.getReferenceFromUrl(imageUrl);
//        imageStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//
//                //Convert bytes to bitmap image
//                bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors
//                Log.i("TAG",exception.getMessage());
//            }
//        });
//
//        return bmp;
//    }
}
