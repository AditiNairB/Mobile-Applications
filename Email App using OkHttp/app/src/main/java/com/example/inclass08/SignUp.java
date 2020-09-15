package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUp extends AppCompatActivity {

    TextView tv_firstName;
    TextView tv_lastName;
    TextView tv_email;
    TextView tv_password;
    TextView tv_confirmPassword;

    Button btn_signUpDet;
    Button btn_cancel;

    String errorMsg;

    JSONObject jsonObjectBody;

//    static SharedPreferences sharedPref;
//    static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

//        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
//        editor = sharedPref.edit();

        tv_firstName = findViewById(R.id.tv_firstName);
        tv_lastName = findViewById(R.id.tv_lastName);
        tv_email = findViewById(R.id.tv_email);
        tv_password = findViewById(R.id.tv_password1);
        tv_confirmPassword = findViewById(R.id.tv_password2);

        btn_signUpDet = findViewById(R.id.btn_signUpDet);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_signUpDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isConnected()) {

                    int flag = 0;
                    if(tv_firstName.getText().length() == 0){
                        tv_firstName.setError("First Name should not be empty");
                        flag = 1;
                    }if(tv_lastName.getText().length() == 0){
                        tv_lastName.setError("Last Name should not be empty");
                        flag = 1;
                    }if(tv_email.getText().length() == 0){
                        tv_email.setError("Email should not be empty");
                        flag = 1;
                    }if(tv_password.getText().length() == 0){
                        tv_password.setError("Password should not be empty");
                        flag = 1;
                    }if(tv_confirmPassword.getText().length() == 0) {
                        tv_confirmPassword.setError("Confirm password should not be empty");
                        flag = 1;
                    }
                    if(flag == 0) {
                        if(tv_confirmPassword.getText().toString().equals(tv_password.getText().toString())){
                            new signUpCall().execute(tv_email.getText().toString(),tv_password.getText().toString(),tv_firstName.getText().toString(),tv_lastName.getText().toString());
                        }else{
                            Toast.makeText(SignUp.this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(SignUp.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

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

    public class signUpCall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            final OkHttpClient client = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", strings[0])
                    .add("password",strings[1])
                    .add("fname",strings[2])
                    .add("lname",strings[3])
                    .build();

            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/signup")
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("SignUp Not");
                }

                Headers responseHeaders = response.headers();
                for (int i = 0; i < responseHeaders.size(); i++) {
                    System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                if(response.networkResponse().code() == 201){
                    String strBody = response.body().string();
                    jsonObjectBody = new JSONObject(strBody);
                    System.out.println("SignUp Successful");
                    String token = jsonObjectBody.get("token").toString();
                    return (token);
                }else{
                    String strBody = response.body().string();
                    JSONObject jsonObjectBody = new JSONObject(strBody);
                    errorMsg = jsonObjectBody.get("message").toString();
                    return ("error");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s != null && !s.equals("error")){
                MainActivity.editor.putString("token", s);
                MainActivity.editor.commit();
//                System.out.println(sharedPref.getString("token",""));
                Toast.makeText(SignUp.this, "User has been created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, Inbox.class);
                try {
                    intent.putExtra(MainActivity.USER_NAME, jsonObjectBody.get("user_fname").toString()+" "+jsonObjectBody.get("user_lname").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
                startActivity(intent);
            }else if(s.equals("error")){
//                Toast.makeText(SignUp.this, "SignUp not successful", Toast.LENGTH_SHORT).show();
                final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SignUp.this);

                dlgAlert.setMessage(errorMsg);
                dlgAlert.setTitle("Error Message");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
            }

        }

    }
}
