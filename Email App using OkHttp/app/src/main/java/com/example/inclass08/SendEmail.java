package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendEmail extends AppCompatActivity {

    Spinner spinner_sendusers;
    EditText et_subject;
    EditText et_emailBody;
    Button button_sendEmail;
    Button button_cancelEmail;
    List<UserData> userList = new ArrayList<>();
    String errorMsg = "Missing subject/message";
    final String authKey = MainActivity.sharedPref.getString("token","");
    //= "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1ODQwNTM2MjAsImV4cCI6MTYxNTU4OTYyMCwianRpIjoiNVN4R05sUkt3MkdFTjVuM2dyNXZJaCIsInVzZXIiOjEzN30.5n6m_8iiqnxaq-opzeP8qcsRTqWCIi8AP1NuheG4y-4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        et_subject = findViewById(R.id.et_subject);
        et_emailBody = findViewById(R.id.et_emailBody);
        button_sendEmail = findViewById(R.id.button_sendEmail);
        button_cancelEmail = findViewById(R.id.button_cancelEmail);

        spinner_sendusers = findViewById(R.id.spinner_sendusers);

        new SetSpinnerData().execute(authKey);


        button_sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String index = userList.get(spinner_sendusers.getSelectedItemPosition()).getUserId();
                String subject = et_subject.getText().toString();
                String message = et_emailBody.getText().toString();
                new SendEmailData().execute(authKey, index, subject, message);
            }
        });

        button_cancelEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SendEmail.this, Inbox.class);
                String name = getIntent().getStringExtra(MainActivity.USER_NAME);
                intent.putExtra(MainActivity.USER_NAME, name);
                startActivity(intent);
            }
        });
    }

    public class SetSpinnerData extends AsyncTask<String, Void, List<UserData>> {


        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected List<UserData> doInBackground(String... strings) {


            String authkey = strings[0];
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/users")
                    .header("Authorization","Bearer "+authkey)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful()) {

                    String responseString = response.body().string();
                    JSONObject obj = new JSONObject(responseString);
                    return null;
                }else {
                    String responseString = response.body().string();
                    JSONObject obj = new JSONObject(responseString);
                    Gson gson = new Gson();
                    Type userListType = new TypeToken<ArrayList<UserData>>(){}.getType();
                    List<UserData> users = gson.fromJson(obj.getString("users"), userListType);

                    return users;
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<UserData> userDataList) {
            super.onPostExecute(userDataList);
            userList.addAll(userDataList);

            List<String> userNames = new ArrayList<>();
            for (UserData user: userDataList ) {
                userNames.add(user.getFirstName()+" "+user.getLastName());
            }
            ArrayAdapter<String> userDataArrayAdapter = new ArrayAdapter<String>(SendEmail.this, android.R.layout.simple_list_item_1, userNames);

            spinner_sendusers.setAdapter(userDataArrayAdapter);
        }

    }

    public class SendEmailData extends AsyncTask<String, Void, Boolean> {


        private final OkHttpClient client = new OkHttpClient();

        @Override
        protected Boolean doInBackground(String... strings) {


            String authkey = strings[0];
            String recieverId = strings[1];
            String subject = strings[2];
            String message = strings[3];

            if(subject.equals(""))
                subject= "(No Subject)";
            RequestBody formBody = new FormBody.Builder()
                    .add("receiver_id", recieverId)
                    .add("subject", subject)
                    .add("message", message)
                    .build();
            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox/add")
                    .header("Authorization","Bearer "+authkey)
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {

                if (!response.isSuccessful())
                    return false;
                else
                    return true;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isMessageSent) {
            super.onPostExecute(isMessageSent);

            if(isMessageSent) {
                Toast.makeText(SendEmail.this, "Email Sent", Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(SendEmail.this, Inbox.class);
                String name = getIntent().getStringExtra(MainActivity.USER_NAME);
                intent.putExtra(MainActivity.USER_NAME, name);
                startActivity(intent);
            }else {

                final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(SendEmail.this);

                dlgAlert.setMessage(errorMsg);
                dlgAlert.setTitle("Error Message");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }

        }

    }
}

