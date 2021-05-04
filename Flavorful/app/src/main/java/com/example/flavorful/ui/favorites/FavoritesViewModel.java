package com.example.flavorful.ui.favorites;

import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavorful.object.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoritesViewModel extends ViewModel {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    private final MutableLiveData<Map<String, Object>> users;
    private MutableLiveData<ArrayList<Recipe>> recipeList;

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

    LiveData<ArrayList<Recipe>> getRecipeList() {
        recipeList = new MutableLiveData<>();
        retrieveSavedRecipe();
        return recipeList;
    }
    public void retrieveSavedRecipe() {

        if(currentUser != null) {
            db.collection("recipes").document(currentUser.getUid())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("TAG", "Listen failed.", e);
                                return;
                            }

                            ArrayList<String> emptyList = new ArrayList<>();
                            if (snapshot != null && snapshot.exists()) {
                                Log.d("TAG", "Current data: " + snapshot.getData());
                                Recipe recipe = new Recipe(snapshot.getData().get("name").toString(),
                                        snapshot.getData().get("imageString").toString(),
                                        snapshot.getData().get("videoUrl").toString(),
                                        emptyList,
                                        emptyList);
                                ArrayList<Recipe> recipes = new ArrayList<>();
                                recipes.add(recipe);
                                recipeList.setValue(recipes);

                            } else {
                                Log.d("TAG", "Current data: null");
                            }
                        }
                    });
        }
    }

}