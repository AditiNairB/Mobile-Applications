package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class SignUp extends AppCompatActivity {

    EditText et_signup_firstname;
    EditText et_signup_lastname;
    EditText et_signup_email;
    EditText et_signup_password;
    EditText et_signup_confirmpassword;
    Button btn_signup;
    Button btn_cancel;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setTitle("Sign Up");

        mAuth = FirebaseAuth.getInstance();

        et_signup_firstname = findViewById(R.id.et_signup_firstname);
        et_signup_lastname = findViewById(R.id.et_signup_lastname);
        et_signup_email = findViewById(R.id.et_signup_email);
        et_signup_password = findViewById(R.id.et_signup_password);
        et_signup_confirmpassword = findViewById(R.id.et_signup_confirmpassword);
        btn_signup = findViewById(R.id.btn_signup);
        btn_cancel = findViewById(R.id.btn_cancel);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isConnected()){

                Pattern pattern = Pattern.compile("[^A-Za-z0-9]");

                String firstName = et_signup_firstname.getText().toString().trim();
                String lastName = et_signup_lastname.getText().toString().trim();
                String email = et_signup_email.getText().toString().trim();
                String password  = et_signup_password.getText().toString().trim();
                String confirmpassword = et_signup_confirmpassword.getText().toString().trim();
                boolean register = true;

                Matcher matcher = pattern.matcher(firstName);
                if(firstName.equals("")){
                    et_signup_firstname.setError("Enter FirstName");
                    register = false;
                }
                if(lastName.equals("")){
                    et_signup_lastname.setError("Enter LastName");
                    register = false;
                }
                if(email.equals("")){
                    et_signup_email.setError("Enter Email");
                    register = false;
                }
                if(!isEmailValid(email)){
                    et_signup_email.setError("Email format incorrect");
                    register = false;
                }
                if(password.equals("")){
                    et_signup_password.setError("Enter Password");
                    register = false;
                }
                if(!confirmpassword.equals(password)){
                    et_signup_confirmpassword.setError("Password does not match");
                    register = false;
                }

                if(register) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "Registered User",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Intent viewContact = new Intent(SignUp.this, ViewContacts.class);
                                        viewContact.putExtra(MainActivity.UID, user.getUid());
                                        startActivity(viewContact);
                                        finish();

                                    } else {
                                        Log.w("demo", "createUserWithEmail:failure", task.getException());
//                                        Toast.makeText(SignUp.this, task.getException().getMessage(),
//                                                Toast.LENGTH_SHORT).show();
                                        final AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
                                        dialog.setCancelable(true);
                                        dialog.setTitle("Authentication Error");
                                        dialog.setMessage(task.getException().getMessage());
                                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                        dialog.show();
                                    }
                                }
                            });
                }
                }else{
                    Toast.makeText(SignUp.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, MainActivity.class);
                startActivity(intent);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        startActivity(intent);
        finish();
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
