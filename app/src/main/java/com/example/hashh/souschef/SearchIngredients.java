package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hashh on 4/2/2017.
 */

public class SearchIngredients extends Activity {
    static String result = "";

    //    ArrayAdapter<String> arrayAdapter;
    static ArrayList<String> arrayList;
    ListView listView;
    Button addButton;
    AutoCompleteTextView ingName;
    IngredientAdapter ingredientAdapter;
    LinearLayout searchingredientsprogresslayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_ingredients);

        ingredientAdapter = new IngredientAdapter(this, R.layout.inglist);
        searchingredientsprogresslayout = (LinearLayout)findViewById(R.id.searchingredientsprogresslayout);
        listView = (ListView)findViewById(R.id.ingListView);
        listView.setAdapter(ingredientAdapter);

        arrayList = new ArrayList<String>();
        Log.d("WORLD", "HEYYYYY!!!");

        try {
            InputStream inputStream = this.openFileInput("ingbackup.txt");
            String data = null;

            if(inputStream!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                data = bufferedReader.readLine();
                inputStream.close();
            }

            if(data != null){
                //Toast.makeText(SearchIngredients.this, data, Toast.LENGTH_LONG).show();

                arrayList = new ArrayList<String>(Arrays.asList(data.split(",")));
                for(int i=0; i<arrayList.size(); i++){
                    Ingredient ingredient = new Ingredient(arrayList.get(i));
                    ingredientAdapter.add(ingredient);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute();

        addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = 1;
                //arrayAdapter = new ArrayAdapter<String>(this, R.layout.inglist, R.id.ingItem, arrayList);

                for(int i=0; i<arrayList.size(); i++){
                    if(result.equals(arrayList.get(i))){
                        flag = 0;
                        break;
                    }
                }
                if(flag==1) {
                    //addItem(arrayAdapter);
                    arrayList.add(result);

                }
                else
                    Toast.makeText(getApplicationContext(),"Ingredient already added", Toast.LENGTH_LONG).show();

                Ingredient ingredient = new Ingredient(result);
                ingredientAdapter.add(ingredient);

                Log.d("RESULT","List: "+arrayList);

                ingName.setText("");

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Ingredient ingredient = (Ingredient)ingredientAdapter.getItem(position);
                String ing_name = ingredient.getIng_name();

                Intent intent = new Intent(SearchIngredients.this, AllRecipe1Ing.class);
                intent.putExtra("ing_name", ing_name);
                startActivity(intent);
            }
        });
    }

    public void searchButton(View view){
        Intent intent = new Intent(this, RecipeList.class);
        intent.putStringArrayListExtra("recipe_list", arrayList);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        String filename = "ingbackup.txt";
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput(filename, Context.MODE_PRIVATE));
            for(int i=0; i<arrayList.size(); i++){
                String str = arrayList.get(i)+",";
                outputStreamWriter.write(str);
            }
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        finish();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        Context ctx;

        BackgroundTask(Context ctx){
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            searchingredientsprogresslayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String ing_url = "http://"+MainActivity.url+"/selectIngredients.php";

            try {
                URL url = new URL(ing_url);

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));


                //String line = "";
                String response = bufferedReader.readLine();
                //while((line = bufferedReader.readLine())!=null)
                  //  response += line;


                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return response;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("HELLO2", "Ingredients:"+result);
            ArrayList<String> ingredientList = new ArrayList<String>();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("ingredients");
                for(int i=0; i<jsonArray.length(); i++){
                    ingredientList.add(jsonArray.getString(i));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //String[] ingredients = result.split(",");

            ingName = (AutoCompleteTextView)findViewById(R.id.ingName);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx, android.R.layout.select_dialog_item, ingredientList);
            ingName.setThreshold(1);
            ingName.setAdapter(adapter);

            ingName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SearchIngredients.result = (String)parent.getItemAtPosition(position);
                }
            });

            searchingredientsprogresslayout.setVisibility(View.GONE);
            Log.d("RESULT2", "Item: "+ SearchIngredients.result);
        }

    }
}
