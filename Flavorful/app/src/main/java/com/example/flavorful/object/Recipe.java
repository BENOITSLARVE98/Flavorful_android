package com.example.flavorful.object;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private final String name;
    private final String image;
    private final String video;


    public ArrayList<String> getInstructionArray() {
        return instructionArray;
    }

    private final ArrayList<String> instructionArray;
    private final ArrayList<String> ingredientArray;



    public ArrayList<String> getIngredientArray() {
        return ingredientArray;
    }


    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

    public Recipe(String name, String image, String video, ArrayList<String> instructionArray, ArrayList<String> ingredientArray) {
        this.name = name;
        this.image = image;
        this.video = video;
        this.instructionArray = instructionArray;
        this.ingredientArray = ingredientArray;
    }
}
