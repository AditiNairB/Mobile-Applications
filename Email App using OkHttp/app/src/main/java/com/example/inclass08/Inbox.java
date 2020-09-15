package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.ConversationActions;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Inbox extends AppCompatActivity implements MyAdapter.OnItemClickListener, MyAdapter.OnDeleteClickListener {

    ImageView iv_create;
    ImageView iv_logout;
    TextView tv_userName;
    final String authKey = MainActivity.sharedPref.getString("token","");
    final ArrayList<EmailContent> emailList = new ArrayList<>();

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        setTitle("Inbox");

        Intent intent = getIntent();

        iv_create = findViewById(R.id.iv_create);
        iv_logout = findViewById(R.id.iv_logout);
        tv_userName = findViewById(R.id.tv_userName);
        recyclerView = findViewById(R.id.recyclerView);

        iv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inbox.this, SendEmail.class);
                intent.putExtra(MainActivity.USER_NAME, getIntent().getStringExtra(MainActivity.USER_NAME));
                startActivity(intent);

            }
        });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Inbox.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        new inboxAPI().execute();

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(Inbox.this,DisplayMailActivity.class);
        intent.putExtra("Mail", (Serializable) emailList.get(position));
        startActivity(intent);

    }

    @Override
    public void onItemDelete(int position) {
        emailList.remove(position);
        if(emailList.size()>0)
        rv_adapter.notifyDataSetChanged();
        else{
            recyclerView.setAdapter(null);
        }
    }

    public class inboxAPI extends AsyncTask<String, Void, List<EmailContent>> {

        @Override
        protected List<EmailContent> doInBackground(String... strings) {


            final OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://ec2-18-234-222-229.compute-1.amazonaws.com/api/inbox")
                    .header("Authorization","Bearer "+ authKey)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Data failed");
                }else{

                    String responseString = response.body().string();
                    JSONObject obj = new JSONObject(responseString);

                    Gson gson = new Gson();
                    Type emailType = new TypeToken<ArrayList<EmailContent>>(){}.getType();
                    List<EmailContent> emails = gson.fromJson(obj.getString("messages"), emailType);
                    return emails;
                }


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<EmailContent> s) {
            super.onPostExecute(s);
            if(s != null){
                emailList.clear();
                emailList.addAll(s);

                String name = getIntent().getStringExtra(MainActivity.USER_NAME);
                tv_userName.setText(name);

                recyclerView.setHasFixedSize(true);

                rv_layoutManager = new LinearLayoutManager(Inbox.this);

                recyclerView.setLayoutManager(rv_layoutManager);

                rv_adapter = new MyAdapter(emailList,  Inbox.this, Inbox.this);

                recyclerView.setAdapter(rv_adapter);
            }else{
                Toast.makeText(Inbox.this, "Could Not Load Emails", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
