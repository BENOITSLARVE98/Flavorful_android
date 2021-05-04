package com.example.flavorful.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flavorful.MainActivity;
import com.example.flavorful.R;
import com.example.flavorful.object.Recipe;
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

import java.util.HashMap;
import java.util.Map;

public class DetailsFragment extends Fragment {

    private static final String ARG_RECIPE = "ARG_RECIPE";
    Recipe recipe;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public static DetailsFragment newInstance(Recipe recipe) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, recipe);

        DetailsFragment fragment = new DetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_details, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        VideoView videoView = requireView().findViewById(R.id.video_view);
        ProgressBar progressBar = requireView().findViewById(R.id.progress_bar);
        TextView emptyVideo = requireView().findViewById(R.id.videoEmpty_text);

        if(getArguments() != null){
            recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);

            if (recipe != null && getContext() != null ) {
                //Set action bar
                //((MainActivity) requireActivity()).setActionBarTitle(recipe.getName());

                //SETUP VIDEO PLAYER
                if (!recipe.getVideo().equals("null")) {
                    emptyVideo.setVisibility(View.INVISIBLE);

                    //Use a media controller so that you can scroll the video contents
                    //and also to pause, start the video.
                    MediaController mediaController = new MediaController(getContext());
                    mediaController.setAnchorView(videoView);
                    mediaController.setMediaPlayer(videoView);

                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(Uri.parse(recipe.getVideo()));

                    videoView.setOnPreparedListener(mp -> {
                        videoView.start();
                        //Hide progress bar
                        progressBar.setVisibility(View.INVISIBLE);
                    });
                } else {
                    //Hide progress bar display message
                    progressBar.setVisibility(View.INVISIBLE);
                    emptyVideo.setVisibility(View.VISIBLE);
                }

                //SETUP INGREDIENT LIST
                ListView listView = requireView().findViewById(R.id.list_view);
                //Create Array Adapter
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(),
                        R.layout.list_text, recipe.getIngredientArray());
                //Set adapter
                listView.setAdapter(listAdapter);


                //STEP BY STEP PAGE
                Button goToStepPageBtn = requireView().findViewById(R.id.stepPage_button);
                goToStepPageBtn.setOnClickListener(v -> requireFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.nav_host_fragment,
                                StepByStepFragment.newInstance(recipe)).commit());

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.save_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.save_icon) {
            //Save recipe to FireStore
            saveRecipeToFireStore();
            Toast.makeText(getContext(), "Recipe Saved", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveRecipeToFireStore() {
        Map<String, Object> recipes = new HashMap<>();

        recipes.put("name", recipe.getName());
        recipes.put("imageString", "");
        recipes.put("videoUrl", recipe.getVideo());
        recipes.put("instructionsArray", recipe.getInstructionArray());
        recipes.put("ingredient", recipe.getIngredientArray());

        db.collection("recipes").document(currentUser.getUid())
                .set(recipes)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");


                        //SAVE RECIPE IMAGE
                        byte[] bytes= recipe.getImage().getBytes();
                        Uri uri =  Uri.parse(recipe.getImage());

                        StorageReference storageProfileRef = storageRef.child("recipeImages").child(currentUser.getUid());
                        UploadTask uploadTask = storageProfileRef.putBytes(bytes);
                        //UploadTask uploadTask = storageProfileRef.putFile(uri);

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
                                    recipes.put("imageString", downloadUri.toString());

                                    //Update recipe image with url
                                    db.collection("recipes").document(currentUser.getUid()).update(recipes);

                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });


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
