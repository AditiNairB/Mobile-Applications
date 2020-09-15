/**
 *
 * Assignment: In Class 06
 * Group Number: 9
 * Members: Aditi Balachandran, Luckose Manuel.
 *
 */
package com.example.inclass06;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    String[] categories;
    public String baseUrl = "https://newsapi.org/v2/top-headlines";
    Button btnGo;
    ProgressBar progressBar;
    int index = 0;
    AlertDialog.Builder builder;
    ImageView iv_articleImage;
    ImageView iv_prev;
    ImageView iv_next;
    TextView tv_title;
    TextView tv_description;
    TextView tv_publishedDate;
    TextView tv_showCategories;
    TextView tv_page;

    ArrayList<News> news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        btnGo = findViewById(R.id.btnSelect);

        tv_title = findViewById(R.id.tv_Title);
        tv_publishedDate = findViewById(R.id.tv_publishedDate);
        iv_articleImage = findViewById(R.id.iv_articleImage);
         tv_description = findViewById(R.id.tv_description);
         tv_showCategories = findViewById(R.id.tv_showCategories);
         tv_page = findViewById(R.id.tv_page);


        iv_prev = findViewById(R.id.iv_prev);
        iv_next = findViewById(R.id.iv_next);

        iv_prev.setEnabled(false);
        iv_next.setEnabled(false);

        /*
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose a Keyword");

         */

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.keyWord);
                categories = getResources().getStringArray(R.array.categories);
                builder.setItems(categories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (isConnected()) {
                            index = 0;
                            news = new ArrayList<>();
                            progressBar.setVisibility(View.VISIBLE);
                            tv_showCategories.setText(categories[i]);

                            new GetJSONData().execute(categories[i]);
                    } else {
                            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.show();

            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnected()) {
                    Toast.makeText(MainActivity.this,"Not connected to Internet",Toast.LENGTH_SHORT).show();

                }else{
                    index--;
                    if(index <0){
                        index = news.size()-1;
                    }

                    iv_articleImage.setImageDrawable(null);
                    progressBar.setVisibility(View.VISIBLE);
                    loadPageContent(index);
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isConnected()) {
                    Toast.makeText(MainActivity.this,"Not connected to Internet",Toast.LENGTH_SHORT).show();
                }else{
                    index++;
                    if(index >(news.size()-1))
                        index = 0;
                    iv_articleImage.setImageDrawable(null);
                    progressBar.setVisibility(View.VISIBLE);
                    loadPageContent(index);
                    progressBar.setVisibility(View.INVISIBLE);

                }

            }
        });
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

    public class GetJSONData extends AsyncTask<String, Void, ArrayList<News>> {

        @Override
        protected ArrayList<News> doInBackground(String... strings) {

            HttpURLConnection connection = null;
            news = new ArrayList<>();
            String category = strings[0];
            try {
                String url = baseUrl + "?" +
                        "apiKey="+getResources().getString(R.string.api_key)
                        +"&"+ "category=" + URLEncoder.encode(category, "UTF-8");
                URL urlB = new URL(url);
                connection  = (HttpURLConnection) urlB.openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray articles = root.getJSONArray("articles");
                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject articleJson = articles.getJSONObject(i);
                        News newsItem = new News();
                        newsItem.title = articleJson.getString("title");
                        newsItem.urlToImage = articleJson.getString("urlToImage");
                        newsItem.description = articleJson.getString("description");
                        newsItem.publishedAt = articleJson.getString("publishedAt");
                        news.add(newsItem);
                    }
                }

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return news;
        }

        @Override
        protected void onPostExecute(ArrayList<News> news) {
            super.onPostExecute(news);
            if(news.size()>0){
                iv_next.setEnabled(true);
                iv_prev.setEnabled(true);
                loadPageContent(0);
                progressBar.setVisibility(View.INVISIBLE);

            }else{
                // Do some thing fail
                Toast.makeText(MainActivity.this,"No News Found.",Toast.LENGTH_SHORT).show();
            }
                    for (News newsItem: news) {
                        Log.d("demo",newsItem.toString());
                    }

        }
    }

    public void loadPageContent(int index){

        String urlToImage = news.get(index).getUrlToImage();

        Picasso.get().load(urlToImage).into(iv_articleImage);

        News newsInfo = news.get(index);
        String title = newsInfo.getTitle().equals("null")?"No Title Found"
                :newsInfo.getDescription().equals("")?"No Title Found":newsInfo.getTitle();
        String description = newsInfo.getDescription().equals("null")?"No Description Found":
                newsInfo.getDescription().equals("")?"No Description Found":newsInfo.getDescription();
        String publishedAt = newsInfo.getPublishedAt().equals("null")?"No Published Date Found":
                newsInfo.getDescription().equals("")?"No Published Date Found":newsInfo.getPublishedAt();
        tv_title.setText(title);
        tv_publishedDate.setText(publishedAt);
        tv_description.setText(description);
        tv_page.setText((index+1)+" out of "+news.size());
    }

}
