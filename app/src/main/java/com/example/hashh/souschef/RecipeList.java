package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

/**
 * Created by hashh on 15-04-2017.
 */

public class RecipeList extends Activity {

    ArrayList<String> arrayList;
    String recing_url = "http://"+MainActivity.url+"/getRecipeIng.php";
    String rec_url = "http://"+MainActivity.url+"/getRecipe.php";
    String JSON_STRING, JSON_STRING1,jsonstr, jsonstr1, currIng, recIng, category;
    JSONObject jsonObject, jsonObject1;
    JSONArray jsonArray, jsonArray1, ingArray;
    TextView textView;
    Spinner filterSpinner;
    ListView recipeListView;
    LinearLayout recipelistprogresslayout;
    ArrayList<Integer> rec_ids;
    ArrayList<Integer> priorities;
    ArrayList<String> recipeList;
    RecipeAdapter2 recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);

        textView = (TextView)findViewById(R.id.textView2);
        recipeListView = (ListView)findViewById(R.id.recipeListView);
        recipelistprogresslayout = (LinearLayout)findViewById(R.id.recipelistprogresslayout);
        filterSpinner = (Spinner)findViewById(R.id.filterspinner);

        recipeAdapter = new RecipeAdapter2(RecipeList.this, R.layout.reclist2);

        arrayList = new ArrayList<String>();
        arrayList = getIntent().getStringArrayListExtra("recipe_list");

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView)parent.getChildAt(0)).setTextColor(ContextCompat.getColor(RecipeList.this, R.color.bgcolor));

                category = parent.getItemAtPosition(position).toString();
                if(category.equals("I don\'t care, show me all"))
                    category = "all";
                while(recipeAdapter.getCount()!=0){
                    Recipe2 recipe2 = (Recipe2) recipeAdapter.getItem(0);
                    recipeAdapter.remove(recipe2);
                }
                new BGTask(RecipeList.this).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //BGTask bgTask = new BGTask(this);
        //bgTask.execute();

        Log.d("INGREDIENT", "Ingredients: "+arrayList);
    }

    class BGTask extends AsyncTask<Void, Void, String> {

        Context ctx;

        BGTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            recipelistprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                //To get ingredients of all recipes
                URL url = new URL(recing_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING=bufferedReader.readLine())!=null){
                    stringBuilder.append(JSON_STRING+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            jsonstr = s;
            rec_ids = new ArrayList<Integer>();
            priorities = new ArrayList<Integer>();
            try {
                jsonArray = new JSONArray(jsonstr);
                for (int i = 1; i <= jsonArray.length(); i++) {//i keeps track of number of recipes
                    jsonObject = jsonArray.getJSONObject(i - 1);
                    ingArray = jsonObject.getJSONArray("ingredients");
                    int j = 1, count = 0;
                    for (; j <= arrayList.size(); j++) {
                        currIng = arrayList.get(j - 1);
                        int k = 1;
                        for (; k <= ingArray.length() && k <= arrayList.size(); k++) {
                            recIng = ingArray.getString(k - 1);
                            if (currIng.equals(recIng)) {
                                count++;
                                break;
                            }
                        }
                    }
                    if (count == ingArray.length()) {
                        rec_ids.add(i);
                        priorities.add(arrayList.size() - ingArray.length());
                    }
                    else if(count >= ingArray.length()-2 && count < ingArray.length()){
                        rec_ids.add(i);
                        priorities.add(count - ingArray.length());
                    }
                }
                //Toast.makeText(RecipeList.this, "Number of recipes: "+priorities.get(1), Toast.LENGTH_SHORT).show();
                Log.d("Recipes", "Recipes: "+rec_ids);
                BGTask2 bgTask2 = new BGTask2(ctx);
                bgTask2.execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class BGTask2 extends AsyncTask<Void, Void, String> {

        Context ctx;

        BGTask2(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(rec_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING1=bufferedReader.readLine())!=null){
                    stringBuilder.append(JSON_STRING1+"\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            jsonstr1 = s;
            recipeList = new ArrayList<String>();
            //String recipename = null;
            //String img_url = null;
            //String rating = null;
            int count = 0;


            try {
                jsonArray1 = new JSONArray(jsonstr1);
                for(int i=1; i<=jsonArray1.length()&&count!=rec_ids.size(); i++){
                    if(i==rec_ids.get(count)) {
                        jsonObject1 = jsonArray1.getJSONObject(i - 1);
                        recipeList.add(jsonObject1.getString("rec_name"));

                        /*recipename = jsonObject1.getString("rec_name");
                        img_url = jsonObject1.getString("img_url");
                        rating = jsonObject1.getString("rating");

                        new ImageBGTask2().execute(recipename, img_url, rating);*/

                        count++;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            for(int i=0; i<recipeList.size();i++){
                for(int j=0; j<recipeList.size()-1; j++){
                    if(priorities.get(j) < priorities.get(j+1)){
                        int temp = priorities.get(j);
                        priorities.set(j, priorities.get(j+1));
                        priorities.set(j+1, temp);

                        int temp2 = rec_ids.get(j);
                        rec_ids.set(j, rec_ids.get(j+1));
                        rec_ids.set(j+1, temp2);

                        String tempstr = recipeList.get(j);
                        recipeList.set(j, recipeList.get(j+1));
                        recipeList.set(j+1, tempstr);
                    }
                }
            }

            /*try {
                JSONArray jsonArray = new JSONArray(jsonstr1);
                for (int i = 0; i < rec_ids.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(rec_ids.get(i) - 1);

                    recipename = jsonObject.getString("rec_name");
                    img_url = jsonObject.getString("img_url");
                    rating = jsonObject.getString("rating");

                    new ImageBGTask2().execute(recipename, img_url, rating, String.valueOf(priorities.get(i)));

                }
            }catch (JSONException e) {
                    e.printStackTrace();
            }*/

            for(int i=0; i<rec_ids.size(); i++){
                new BGTask3().execute(rec_ids.get(i), i);
            }

            recipelistprogresslayout.setVisibility(View.GONE);
            /*recipeListView = (ListView)findViewById(R.id.recipeListView);
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(ctx, R.layout.reclist, R.id.recipeName, recipeList);
            recipeListView.setAdapter(arrayAdapter);

            recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ctx, RecipeDetails.class);
                    intent.putExtra("recipeName", recipeList.get(position));
                    startActivity(intent);
                }
            });*/

        }
    }

    class BGTask3 extends AsyncTask<Integer, Void, String>{

        String getCategoryurl = "http://"+MainActivity.url+"/getCategory.php";
        int index;

        @Override
        protected String doInBackground(Integer... params) {
            index = params[1];
            try {
                URL url = new URL(getCategoryurl);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                String data = URLEncoder.encode("rec_id", "utf-8")+"="+URLEncoder.encode(String.valueOf(params[0]), "utf-8")+"&"+
                              URLEncoder.encode("category", "utf-8")+"="+URLEncoder.encode(category, "utf-8");
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

            if(!result.equals("skip")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    recipename = jsonObject.getString("rec_name");
                    img_url = jsonObject.getString("img_url");
                    rating = jsonObject.getString("rating");

                    new ImageBGTask2().execute(recipename, img_url, rating, String.valueOf(priorities.get(index)));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    class ImageBGTask2 extends AsyncTask<String, Void, Bitmap>{

        String recipename;
        float rating;
        int priority;

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap mIcon11 = null;
            recipename = params[0];
            rating = Float.parseFloat(params[2]);
            priority = Integer.parseInt(params[3]);
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
            recipeListView.setAdapter(recipeAdapter);

            Recipe2 recipe2 = new Recipe2(recipename, recipeimage, rating, priority);
            recipeAdapter.add(recipe2);

            recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(RecipeList.this, RecipeDetails.class);
                    Recipe2 recipe21 = (Recipe2) recipeAdapter.getItem(position);
                    intent.putExtra("recipeName", recipe21.getRecipename());
                    startActivity(intent);
                }
            });

        }
    }

}
