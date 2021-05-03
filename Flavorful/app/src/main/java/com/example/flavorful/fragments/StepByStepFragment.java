package com.example.flavorful.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.flavorful.R;
import com.example.flavorful.object.Recipe;

import java.util.Objects;

public class StepByStepFragment extends Fragment {

    private static final String ARG_RECIPE = "ARG_RECIPE_STEP";

    public static StepByStepFragment newInstance(Recipe recipe) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_RECIPE, recipe);

        StepByStepFragment fragment = new StepByStepFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_step, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getArguments() != null) {

            Recipe recipe = (Recipe) getArguments().getSerializable(ARG_RECIPE);

            TextView recipeTitle = requireView().findViewById(R.id.recipe_title);
            recipeTitle.setText(recipe.getName());

            ListView listView = requireView().findViewById(R.id.stepByStep_list);
            //Create Array Adapter
            ArrayAdapter<String> listAdapter = new ArrayAdapter<>(getContext(),
                    R.layout.list_text, recipe.getInstructionArray());
            //Set adapter
            listView.setAdapter(listAdapter);
        }
    }
}
