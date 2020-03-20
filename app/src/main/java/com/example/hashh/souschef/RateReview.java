package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by hashh on 06-05-2017.
 */

public class RateReview extends Activity {

    String recipeName, reviewString;
    RatingBar ratingBar;
    Button submitButton;
    float ratingValue;
    EditText review;
    ListView reviewListView;
    RateReviewAdapter rateReviewAdapter;
    LinearLayout ratereviewprogresslayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ratereview);

        Intent intent = getIntent();
        recipeName = intent.getExtras().getString("recipeName");

        review = (EditText)findViewById(R.id.review);
        submitButton = (Button)findViewById(R.id.submitButton);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        reviewListView = (ListView)findViewById(R.id.reviewListView);
        ratereviewprogresslayout = (LinearLayout)findViewById(R.id.ratereviewprogresslayout);

        new DisplayReviewsBGTask().execute();

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingValue = rating;
                //Toast.makeText(RateReview.this, String.valueOf(ratingValue), Toast.LENGTH_LONG).show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewString = review.getText().toString();
                new RateReviewBGTask().execute();
                //finish();
            }
        });
    }


    //BGTask to insert or update ratings
    class RateReviewBGTask extends AsyncTask<Void, Void, String>{

        String putURL = "http://"+MainActivity.url+"/rateReview.php";

        @Override
        protected void onPreExecute() {
            ratereviewprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(putURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"utf-8"));
                String data = URLEncoder.encode("username", "utf-8")+"="+URLEncoder.encode(MainActivity.Username, "utf-8")+
                        "&"+URLEncoder.encode("recipename", "utf-8")+"="+URLEncoder.encode(recipeName, "utf-8")+
                        "&"+URLEncoder.encode("rating", "utf-8")+"="+URLEncoder.encode(String.valueOf(ratingValue), "utf-8")+
                        "&"+URLEncoder.encode("review", "utf-8")+"="+URLEncoder.encode(reviewString, "utf-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                bufferedReader.close();
                inputStream.close();

                httpURLConnection.disconnect();

                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            ratereviewprogresslayout.setVisibility(View.GONE);
            Toast.makeText(RateReview.this, "Thank You for your review", Toast.LENGTH_LONG).show();
        }
    }

    class DisplayReviewsBGTask extends AsyncTask<Void, Void, String>{

        String getReviewsURL = "http://"+MainActivity.url+"/getUserReviews.php";

        @Override
        protected void onPreExecute() {
            ratereviewprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(getReviewsURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("recipename", "utf-8")+"="+URLEncoder.encode(recipeName, "utf-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream =httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            rateReviewAdapter = new RateReviewAdapter(RateReview.this, R.layout.ratereviewlist);
            reviewListView.setAdapter(rateReviewAdapter);

            String username, review;
            float rating;

            try {
                JSONArray jsonArray = new JSONArray(result);
                for(int i=1; i<=jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i-1);

                    username = jsonObject.getString("username");
                    rating = Float.parseFloat(jsonObject.getString("rating"));
                    review = jsonObject.getString("review");

                    RatingsAndReviews ratingsAndReviews = new RatingsAndReviews(username, rating, review);
                    rateReviewAdapter.add(ratingsAndReviews);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ratereviewprogresslayout.setVisibility(View.GONE);

        }
    }

}
