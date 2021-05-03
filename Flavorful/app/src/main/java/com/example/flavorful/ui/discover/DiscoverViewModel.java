package com.example.flavorful.ui.discover;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.flavorful.object.Recipe;

import java.util.ArrayList;

public class DiscoverViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Recipe>> recipeList;
    static ArrayList<Recipe> feedRecipes;

    LiveData<ArrayList<Recipe>> getRecipeList() {
        if (recipeList == null) {
            recipeList = new MutableLiveData<>();
            recipeList.setValue(feedRecipes);
        }
        return recipeList;
    }

    public static void loadRecipes(ArrayList<Recipe> recipes) {
        feedRecipes = new ArrayList<>();
        feedRecipes = recipes;
    }
}