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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

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
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by hashh on 16-05-2017.
 */

public class QuickSearch extends Activity {

    AutoCompleteTextView quicksearchbox;
    Button quicksearchButton;
    ListView quicksearchListView;
    RecipeAdapter recipeAdapter;
    LinearLayout quicksearchprogresslayout;
    int index = 0;

    String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quicksearch);

        quicksearchbox = (AutoCompleteTextView)findViewById(R.id.quicksearchbox);
        quicksearchButton = (Button)findViewById(R.id.quicksearchButton);
        quicksearchListView = (ListView)findViewById(R.id.quicksearchListView);
        quicksearchprogresslayout = (LinearLayout)findViewById(R.id.quicksearchprogresslayout);

        Button loadMoreButton = new Button(this);
        loadMoreButton.setText("Load More...");

        quicksearchListView.addFooterView(loadMoreButton);
        recipeAdapter = new RecipeAdapter(this, R.layout.reclist);
        //quicksearchListView.setAdapter(recipeAdapter);

        new AutoCompleteBGTask().execute();

        new LoadMoreBGTask().execute();

        loadMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index+=10;
                new LoadMoreBGTask().execute();
            }
        });

        quicksearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuickSearch.this, RecipeDetails.class);
                intent.putExtra("recipeName", result);
                startActivity(intent);
            }
        });

    }

    class AutoCompleteBGTask extends AsyncTask<Void, Void, String>{

        String selectRecipe_url = "http://"+MainActivity.url+"/selectRecipe.php";

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(selectRecipe_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            ArrayList<String> arrayList = new ArrayList<String>();

            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("recipes");
                for(int i=0; i<jsonArray.length(); i++){
                    arrayList.add(jsonArray.getString(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(QuickSearch.this, android.R.layout.select_dialog_item, arrayList);
            quicksearchbox.setThreshold(1);
            quicksearchbox.setAdapter(arrayAdapter);

            quicksearchbox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    result = (String) parent.getItemAtPosition(position);
                }
            });

        }
    }

    class LoadMoreBGTask extends AsyncTask<Void, Void, String>{

        String quickSearchList_url = "http://"+MainActivity.url+"/quickSearchList.php";

        @Override
        protected void onPreExecute() {
            quicksearchListView.setVisibility(View.GONE);
            quicksearchprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(quickSearchList_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("index", "utf-8")+"="+URLEncoder.encode(String.valueOf(index), "utf-8");
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
            } catch (ProtocolException e) {
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

            //recipeAdapter = new RecipeAdapter(QuickSearch.this, R.layout.reclist);

            try {
                JSONArray jsonArray = new JSONArray(result);

                for(int i=0; i<jsonArray.length(); i++){
                    //arrayList.add(i, jsonArray.getString(i));
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    recipename = jsonObject1.getString("rec_name");
                    img_url = jsonObject1.getString("img_url");
                    rating = jsonObject1.getString("rating");

                    new ImageBGTask().execute(recipename, img_url, rating);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            quicksearchListView.setVisibility(View.VISIBLE);
            quicksearchprogresslayout.setVisibility(View.GONE);
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
            quicksearchListView.setAdapter(recipeAdapter);

            final Recipe recipe = new Recipe(recipename, recipeimage, rating);
            recipeAdapter.add(recipe);
            recipeAdapter.notifyDataSetChanged();

            quicksearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(QuickSearch.this, RecipeDetails.class);
                    Recipe recipe1 = (Recipe) recipeAdapter.getItem(position);
                    intent.putExtra("recipeName", recipe1.getRecipename());
                    startActivity(intent);
                }
            });

        }
    }
}
