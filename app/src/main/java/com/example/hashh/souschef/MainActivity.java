package com.example.hashh.souschef;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    static String Username;
    static String url = "souschef.000webhostapp.com";
    //static String url = "10.0.2.2/ingredients";
    //static String url = "192.168.225.138/ingredients";

    EditText username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            InputStream inputStream = this.openFileInput("username.txt");
            String data = null;

            if(inputStream!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                data = bufferedReader.readLine();
                inputStream.close();
            }

            if(data != null){
                Username = data;

                Intent intent = new Intent(this, HomePage.class);
                intent.putExtra("username", Username);
                startActivity(intent);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        username = (EditText) findViewById(R.id.login_username);
        password = (EditText) findViewById(R.id.login_pass);

    }

    public void login(View view){
        String login_name = username.getText().toString();
        String login_pass = password.getText().toString();
        String method = "login";

        username.setText("");
        password.setText("");

        BackgroundTask backgroundTask = new BackgroundTask(this);
        backgroundTask.execute(method, login_name, login_pass);
        /*if(isLogin==true) {
            startActivity(new Intent(this, SearchIngredients.class));
        }*/

    }

    public void register(View view){
        Register.finish = false;
        startActivity(new Intent(this, Register.class));
    }
}
