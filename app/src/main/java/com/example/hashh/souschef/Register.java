package com.example.hashh.souschef;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by hashh on 4/2/2017.
 */

public class Register extends Activity {

    public static boolean finish;

    EditText firstName, lastName, new_username, new_password;
    String firstname, lastname, user_name, user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        new_username = (EditText) findViewById(R.id.new_username);
        new_password = (EditText) findViewById(R.id.new_password);
    }

    public void signup(View view) {

        firstname = firstName.getText().toString();
        lastname = lastName.getText().toString();
        user_name = new_username.getText().toString();
        user_password = new_password.getText().toString();
        String method = "register";

        if (user_password.length() < 7) {
            Toast.makeText(this, "Enter password of 6 or more characters", Toast.LENGTH_LONG).show();
        } else {
            BackgroundTask backgroundTask = new BackgroundTask(this);
            backgroundTask.execute(method, firstname, lastname, user_name, user_password);
        }
    }
}
