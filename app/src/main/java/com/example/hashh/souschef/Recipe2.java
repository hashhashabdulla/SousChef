package com.example.hashh.souschef;

import android.graphics.Bitmap;

/**
 * Created by hashh on 09-05-2017.
 */

public class Recipe2 {

    private String recipename;
    private Bitmap recipeimg;
    private float rating;
    private int priority;

    public Recipe2(String recipename, Bitmap recipeimg, float rating, int priority){
        this.recipename = recipename;
        this.recipeimg = recipeimg;
        this.rating = rating;
        this.priority = priority;
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

    public int getPriority() {
        return priority;
    }
}
