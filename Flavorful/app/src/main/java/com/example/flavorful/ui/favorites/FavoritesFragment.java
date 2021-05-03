package com.example.flavorful.ui.favorites;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.flavorful.ProfileActivity;
import com.example.flavorful.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

public class FavoritesFragment extends Fragment {

    public static final String value = "Activity";

    FirebaseStorage storage = FirebaseStorage.getInstance();

    private FavoritesViewModel favoritesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        favoritesViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {
                TextView nameText = root.findViewById(R.id.pro_profileName_text);
                TextView emailText = root.findViewById(R.id.pro_profileEmail_text);
                ImageView imageView = root.findViewById(R.id.pro_profileImage_view);

                nameText.setText(stringObjectMap.get("name").toString());
                emailText.setText(stringObjectMap.get("email").toString());

                String imageUrl = stringObjectMap.get("profileImageUrl").toString();

                if (!imageUrl.isEmpty()) {
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
                }
            }
        });


        //Intent to go to Profile page
        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
        profileIntent.putExtra("key", value);

        root.findViewById(R.id.editPassword_btn).setOnClickListener(v -> {
            // Load login activity
            startActivity(profileIntent);
        });



        return root;

    }
}