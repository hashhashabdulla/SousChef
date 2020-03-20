package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
 * Created by hashh on 23-04-2017.
 */

public class HomePage extends Activity {

    TextView fullname, addFavoritesText;
    ListView recipeList;
    Button searchIngButton, logoutButton, quicksearch;
    LinearLayout homepageprogresslayout;
    static ArrayAdapter arrayAdapter;
    static RecipeAdapter recipeAdapter;
    static ArrayList<String> arrayList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        final Intent intent = getIntent();

        String username = intent.getExtras().getString("username");

        String filename = "username.txt";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(username);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        fullname = (TextView)findViewById(R.id.fullname);
        addFavoritesText = (TextView)findViewById(R.id.addFavoritesText);
        searchIngButton = (Button)findViewById(R.id.searchIngButton);
        logoutButton = (Button)findViewById(R.id.logoutButton);
        quicksearch = (Button)findViewById(R.id.quickSearch);
        recipeList = (ListView)findViewById(R.id.recipeList);
        homepageprogresslayout = (LinearLayout)findViewById(R.id.homepageprogresslayout);

        new HomePageBGTask().execute(username);

        searchIngButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFavoritesText.setText("");
                startActivity(new Intent(HomePage.this, SearchIngredients.class));
            }
        });

        quicksearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this, QuickSearch.class));
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(HomePage.this.openFileOutput("username.txt", Context.MODE_PRIVATE));
                    outputStreamWriter.write("");
                    outputStreamWriter.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    class HomePageBGTask extends AsyncTask<String, Void, String>{

        String getUserDetails = "http://"+MainActivity.url+"/favorites.php";

        @Override
        protected void onPreExecute() {
            homepageprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(getUserDetails);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("username", "utf-8")+"="+URLEncoder.encode(params[0], "utf-8");
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
        protected void onPostExecute(String result) {
            //final ArrayList<String> arrayList = new ArrayList<String>();

            String recipename = null;
            String img_url = null;
            String rating = null;

            try {
                JSONObject jsonObject = new JSONObject(result);
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                fullname.setText(firstname+" "+lastname);

                JSONArray jsonArray = jsonObject.getJSONArray("favorites");
                if(jsonArray.length()==0)
                    addFavoritesText.setText("You have not added any recipes to favorites.");

                recipeAdapter = new RecipeAdapter(HomePage.this, R.layout.reclist);

                for(int i=0; i<jsonArray.length(); i++){
                    //arrayList.add(i, jsonArray.getString(i));
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    arrayList.add(i, jsonObject1.getString("recipename"));

                    recipename = jsonObject1.getString("recipename");
                    img_url = jsonObject1.getString("img_url");
                    rating = jsonObject1.getString("rating");

                    new ImageBGTask().execute(recipename, img_url, rating);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            homepageprogresslayout.setVisibility(View.GONE);


           /* recipeList = (ListView)findViewById(R.id.recipeList);
            arrayAdapter = new ArrayAdapter(HomePage.this, R.layout.reclist, R.id.recipeName, arrayList);
            recipeList.setAdapter(arrayAdapter);

            recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(HomePage.this, RecipeDetails.class);
                    intent.putExtra("recipeName", arrayList.get(position));
                    startActivity(intent);
                }
            });*/
        }
    }

    class ImageBGTask extends AsyncTask<String, Void, Bitmap>{

        String recipename;
        float rating;

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap mIcon11 = null;
            recipename = params[0];
            rating = Float.parseFloat(params[2]);
            try {
                InputStream in = new java.net.URL(params[1]).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        @Override
        protected void onPostExecute(Bitmap recipeimage) {
            recipeList.setAdapter(recipeAdapter);

            Recipe recipe = new Recipe(recipename, recipeimage, rating);
            recipeAdapter.add(recipe);

            recipeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(HomePage.this, RecipeDetails.class);
                    intent.putExtra("recipeName", arrayList.get(position));
                    startActivity(intent);
                }
            });

        }
    }
}
