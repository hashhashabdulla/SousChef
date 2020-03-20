package com.example.hashh.souschef;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
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

/**
 * Created by hashh on 4/2/2017.
 */

public class BackgroundTask extends AsyncTask<String,Void,String> {

    Context ctx;
    String username;

    BackgroundTask(Context ctx){
        this.ctx = ctx;
    }



    @Override
    protected String doInBackground(String... params) {
        String reg_url = "http://"+MainActivity.url+"/insertLogin.php";
        String login_url = "http://"+MainActivity.url+"/displayLogin.php";

        String method = params[0];
        if(method.equals("register")){
            String firstname = params[1];
            String lastname = params[2];
            String user_name = params[3];
            String user_password = params[4];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("firstname", "UTF-8")+"="+ URLEncoder.encode(firstname, "UTF-8")+"&"
                              + URLEncoder.encode("lastname", "UTF-8")+"="+ URLEncoder.encode(lastname, "UTF-8")+"&"
                              + URLEncoder.encode("username", "UTF-8")+"="+ URLEncoder.encode(user_name, "UTF-8")+"&"
                              + URLEncoder.encode("password", "UTF-8")+"="+ URLEncoder.encode(user_password, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = bufferedReader.readLine();
                inputStream.close();
                if(response.equals("Duplicate"))
                    return  "Username already exists!";
                else
                    return "Registration Success...";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        else if(method.equals("login")){
            String login_name = params[1];
            String login_pass = params[2];

            try {
                URL url = new URL(login_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String data = URLEncoder.encode("login_name", "UTF-8")+"="+ URLEncoder.encode(login_name, "UTF-8")+"&"
                              + URLEncoder.encode("login_pass", "UTF-8")+"="+ URLEncoder.encode(login_pass, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while((line=bufferedReader.readLine())!=null){
                    response += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                username = login_name;

                return response;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String result) {
        if (result.equals("Registration Success...")){
            Register.finish = true;
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        }
        else if(result.equals("Username already exists!"))
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        else if(result.equals("Invalid User!")) {
            Toast.makeText(ctx, "Username and Password didn't match!", Toast.LENGTH_LONG).show();
        }
        else if(result.equals(username)){
            MainActivity.Username = username;

            Intent intent = new Intent(ctx, HomePage.class);
            intent.putExtra("username", result);
            ctx.startActivity(intent);
        }
    }
}
