package com.example.flavorful.ui.discover;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.flavorful.R;
import com.example.flavorful.RecipeAdapter;
import com.example.flavorful.fragments.DetailsFragment;

public class DiscoverFragment extends Fragment {

    private DiscoverViewModel discoverViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        discoverViewModel = new ViewModelProvider(this).get(DiscoverViewModel.class);
        View root = inflater.inflate(R.layout.fragment_discover, container, false);
        final GridView gridView= root.findViewById(R.id.gridView_discover);
        discoverViewModel.getRecipeList().observe(getViewLifecycleOwner(), recipeList-> {
            // update UI
            RecipeAdapter adapter = new RecipeAdapter(getContext(), recipeList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener((parent, view, position, id) -> {
                //Load Details Fragment
                assert getFragmentManager() != null;
                requireFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.nav_host_fragment,
                                DetailsFragment.newInstance(recipeList.get(position))).commit();
            });
        });
        return root;
    }
}