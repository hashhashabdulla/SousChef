package com.example.hashh.souschef;

import android.graphics.Bitmap;

/**
 * Created by hashh on 09-05-2017.
 */

public class Recipe {

    private String recipename;
    private Bitmap recipeimg;
    private float rating;

    public Recipe(String recipename, Bitmap recipeimg, float rating){
        this.recipename = recipename;
        this.recipeimg = recipeimg;
        this.rating = rating;
    }

    public String getRecipename() {
        return recipename;
    }

    public Bitmap getRecipeimg() {
        return recipeimg;
    }

    public float getRating() {
        return rating;
    }
}
