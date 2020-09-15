/**
 *
 * Assignment: InClass12
 * Group No: 9
 * Members: Aditi Balachandran and Luckose Manuel
 */

package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {


    EditText et_loginemail;
    EditText et_loginpassword;
    Button button_login;
    Button button_signup;
    private FirebaseAuth mAuth;
    public static String UID = "uid";

    @Override
    protected void onStart() {
        super.onStart();

        setTitle("Login");

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Log.d("demo", "Current User: "+currentUser.getEmail());
//            Toast.makeText(getApplicationContext(), "Logged in as "+currentUser.getDisplayName()+"!!!", Toast.LENGTH_SHORT);
            Intent viewContact = new Intent(MainActivity.this, ViewContacts.class);
            viewContact.putExtra(UID,currentUser.getUid());
            startActivity(viewContact);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         et_loginemail = findViewById(R.id.et_loginemail);
            et_loginpassword = findViewById(R.id.et_loginpassword);
            button_login = findViewById(R.id.button_login);
            button_signup = findViewById(R.id.button_signup);
            button_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isConnected()) {
                        String email = et_loginemail.getText().toString().trim();
                        String password = et_loginpassword.getText().toString().trim();
                        if (email.equals("")) {
                            et_loginemail.setError("Enter Email");
                        } else if (!isEmailValid(email)) {
                            et_loginemail.setError("Email format incorrect");
                        } else if (password.equals("")) {
                            et_loginpassword.setError("Enter Password");
                        } else {
                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Login Success!!!", Toast.LENGTH_SHORT);
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                if (user != null) {
                                                    Intent viewContact = new Intent(MainActivity.this, ViewContacts.class);
                                                    viewContact.putExtra(UID, user.getUid());
                                                    startActivity(viewContact);
                                                    finish();
                                                }
                                            } else {
                                                Log.w("demo", "signInWithEmail:failure", task.getException());
                                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }

                                            // ...
                                        }
                                    });
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            button_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent signUp = new Intent(MainActivity.this, SignUp.class);
                    startActivity(signUp);
                    finish();
                }
            });
    }
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }        return true;
    }
}
