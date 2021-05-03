package com.example.flavorful.ui.favorites;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class FavoritesViewModel extends ViewModel {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    private final MutableLiveData<Map<String, Object>> users;

    public FavoritesViewModel() {
        users= new MutableLiveData<>();
        retrieveUserInfo();
    }

    public MutableLiveData<Map<String, Object>> getUsers() {
        return users;
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
                        users.setValue(snapshot.getData());

                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
            });
        }
    }

}