/**
 * Group No: 9
 * Members: Aditi Balachandran and Luckose Manuel
 * Assigment: In Class 08
 */

package com.example.inclass08;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    static TextView tv_email;
    static TextView tv_password;
    Button btn_login;
    Button btn_signUp;
    JSONObject jsonObjectBody;
    static SharedPreferences sharedPref;
    static SharedPreferences.Editor editor;

    static String USER_NAME = "UserName";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Mailer");

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        tv_email = findViewById(R.id.tv_email);
        tv_password = findViewById(R.id.tv_password);
        btn_login = findViewById(R.id.btn_login);
        btn_signUp = findViewById(R.id.btn_signUp);

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(isConnected()) {

                    if(tv_email.getText().toString().length() > 0 && tv_password.getText().toString().length() > 0){
                        new asyncCall().execute(tv_email.getText().toString(),tv_password.getText().toString());
                    }
                    else {
                        if (tv_email.getText().toString().length() == 0) {
                            tv_email.setError("Email cannot be empty");
                        }if (tv_password.getText().toString().length() == 0) {
                            tv_password.setError("Password cannot be empty");
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }        return true;
    }

    public class asyncCall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            final OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", strings[0])
                    .add("password",strings[1])
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/login")
                    .post(formBody)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                     System.out.println("Login Not");
                }

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                if(response.networkResponse().code() == 201){
                        String strBody = response.body().string();
                        jsonObjectBody = new JSONObject(strBody);
                        System.out.println("Login Successful");
                        String token = jsonObjectBody.get("token").toString();
                        return (token);

                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null){
                editor.putString("token", s);
                editor.commit();
//                System.out.println(sharedPref.getString("token",""));
                Intent intent = new Intent(MainActivity.this, Inbox.class);
                try {
                    intent.putExtra(USER_NAME, jsonObjectBody.get("user_fname").toString()+" "+jsonObjectBody.get("user_lname").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(MainActivity.this, "Login not successful", Toast.LENGTH_SHORT).show();
            }

        }

    }

}
