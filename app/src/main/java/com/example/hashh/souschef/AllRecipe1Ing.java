package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

/**
 * Created by hashh on 21-05-2017.
 */

public class AllRecipe1Ing extends Activity {

    TextView ingnameTextView;
    ListView ingrecipelist;
    String ingname;
    RecipeAdapter recipeAdapter;
    LinearLayout allrecipe1ingprogresslayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allrecipe1ing);

        Intent intent = getIntent();
        ingname = intent.getExtras().getString("ing_name");

        ingnameTextView = (TextView)findViewById(R.id.ingnameTextView);
        ingrecipelist = (ListView)findViewById(R.id.ingrecipelist);
        allrecipe1ingprogresslayout = (LinearLayout)findViewById(R.id.allrecipe1ingprogresslayout);

        String str = "Recipe's that can be cooked with "+ingname;
        ingnameTextView.setText(str);

        new GetRecipesBGTask().execute(ingname);
    }

    class GetRecipesBGTask extends AsyncTask<String, Void, String>{
        String allrecipe1ingurl = "http://"+MainActivity.url+"/allrecipe1ing.php";

        @Override
        protected void onPreExecute() {
            allrecipe1ingprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(allrecipe1ingurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("ing_name", "utf-8")+"="+URLEncoder.encode(params[0], "utf-8");
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
            String recipename = null;
            String img_url = null;
            String rating = null;

            try {
                JSONArray jsonArray = new JSONArray(result);

                recipeAdapter = new RecipeAdapter(AllRecipe1Ing.this, R.layout.reclist);

                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    recipename = jsonObject.getString("rec_name");
                    img_url = jsonObject.getString("img_url");
                    rating = jsonObject.getString("rating");

                    new ImageBGTask().execute(recipename, img_url, rating);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            allrecipe1ingprogresslayout.setVisibility(View.GONE);
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
            ingrecipelist.setAdapter(recipeAdapter);
            Recipe recipe = new Recipe(recipename, recipeimage, rating);
            recipeAdapter.add(recipe);

            ingrecipelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(AllRecipe1Ing.this, RecipeDetails.class);
                    Recipe recipe1 = (Recipe)recipeAdapter.getItem(position);
                    intent.putExtra("recipeName", recipe1.getRecipename());
                    startActivity(intent);
                }
            });

        }
    }
}
