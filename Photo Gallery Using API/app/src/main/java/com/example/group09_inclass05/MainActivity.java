package com.example.group09_inclass05;
/**
 * Assignment - Inclass 05
 * Group 09 - Aditi Balachandran and Luckose Manuel
 */
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URL;

public class MainActivity extends AppCompatActivity {


    Button btnGo;
    String[] URL = null;
    ProgressBar progressBar;
    int index = 0;
    TextView tv_search;
    AlertDialog.Builder builder;
    ImageView showImage;
    ImageView iv_prev;
    ImageView iv_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        tv_search = findViewById(R.id.tv_search);
        btnGo = findViewById(R.id.btnGo);
        showImage = findViewById(R.id.show_image);
        iv_prev = findViewById(R.id.iv_prev);
        iv_next = findViewById(R.id.iv_next);

        iv_prev.setEnabled(false);
        iv_next.setEnabled(false);

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose a Keyword");
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new getKeywords().execute();

            }
        });

        iv_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index--;
                if(index <0){
                    index = URL.length -1;
                }
                showImage.setImageDrawable(null);
                progressBar.setVisibility(View.VISIBLE);
                new ImageDownload().execute(URL[index]);
            }
        });

        iv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index++;
                if(index >(URL.length-1))
                    index = 0;
                showImage.setImageDrawable(null);
                progressBar.setVisibility(View.VISIBLE);
                new ImageDownload().execute(URL[index]);
            }
        });
    }

    public class getKeywordImageAPIs extends AsyncTask<String, Void, String[]> {

        String[] apis = {};
        String str;
        String readLine = "";
        BufferedReader reader = null;

        @Override
        protected String[] doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            HttpURLConnection connection = null;

            try {
                URL url = new URL("https://dev.theappsdr.com/apis/photos/index.php?keyword=" + strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        while ((readLine = reader.readLine()) != null) {
                            sb.append(readLine+" ");
                        }
                        str = sb.toString();
                        apis = str.split(" ");
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return apis;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            progressBar.setVisibility(View.INVISIBLE);
            URL = strings;
            if(!(URL[index].equals("")))
                new ImageDownload().execute(URL[index]);
            else{
                Toast.makeText(MainActivity.this, "Image Not Found", Toast.LENGTH_SHORT).show();
                showImage.setImageBitmap(null);
                iv_next.setEnabled(false);
                iv_prev.setEnabled(false);
            }
        }
    }

    public class getKeywords extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String[] keywords = {};
            try {
                URL url = new URL("https://dev.theappsdr.com/apis/photos/keywords.php");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String readLine = "";
                    while ((readLine = reader.readLine()) != null) {
                        String s = readLine.toString();
                        keywords = s.split(";");
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return keywords;
        }

        @Override
        protected void onPostExecute(final String[] strings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Choose a Keyword");

            builder.setItems(strings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                        index = 0;
                        progressBar.setVisibility(View.VISIBLE);
                        tv_search.setText(strings[i]);
                        iv_next.setEnabled(true);
                        iv_prev.setEnabled(true);
                        new getKeywordImageAPIs().execute(strings[i]);
                    }
            });
            builder.show();
            super.onPostExecute(strings);
        }
    }

    public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

        Bitmap bitmap = null;

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            bitmap = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            showImage.setImageBitmap(bitmap);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }


}
