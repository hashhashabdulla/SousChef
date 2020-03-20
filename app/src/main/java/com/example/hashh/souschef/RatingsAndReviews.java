package com.example.hashh.souschef;

/**
 * Created by hashh on 08-05-2017.
 */

//Getters and setters for ratings and reviews

public class RatingsAndReviews {

    private String username;
    private float rating;
    private String review;

    public RatingsAndReviews(String username, float rating, String review){
        this.username = username;
        this.rating = rating;
        this.review = review;
    }

    public String getUsername() {
        return username;
    }

    public float getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

}
