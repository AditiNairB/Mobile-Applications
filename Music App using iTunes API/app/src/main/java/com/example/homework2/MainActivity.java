/**
 * Assignment : Homework 2
 * Group No   : 9
 * Members   : Aditi Balachandran and Luckose Manuel
 */

package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBar;
    TextView tv_limit;
    SearchView searchView;
    Button search;
    Button reset;
    Switch priceDateSwitch;
    String baseURL ="https://itunes.apple.com/search";
    ArrayList<Music> searchResults = new ArrayList<>();
    ListView listView;
    //ArrayAdapter<Music> musicArrayAdapter;
    MusicAdapter musicAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("iTunes Music Search");

        listView = findViewById(R.id.listView);
        seekBar = findViewById(R.id.seekBar);
        tv_limit = findViewById(R.id.tv_limit);
        searchView = findViewById(R.id.searchView);
        search = findViewById(R.id.btn_search);
        reset = findViewById(R.id.btn_reset);
        priceDateSwitch = findViewById(R.id.switch1);
        priceDateSwitch.setChecked(true);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_limit.setText("Limit: " + seekBar.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchResults = new ArrayList<>();
                if(isConnected()) {

                    progressBar.setVisibility(View.VISIBLE);
                    String searchQuery = searchView.getQuery().toString();
                    searchQuery = searchQuery.replaceAll(" ","+");

                    new GetSearchDetails().execute(searchQuery);

                }else{
                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Log.d("demo","Details"+ searchResults.toString());

                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);

                intent.putExtra("DISPLAY",searchResults.get(i));

                startActivity(intent);
            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(10);
                tv_limit.setText("Limit: 10");
                searchView.setQuery("", false);
                priceDateSwitch.setChecked(true);
            }
        });

        priceDateSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                ArrayList<Music> sortResults = new ArrayList<>(searchResults);

                if(b==false) {
                    Collections.sort(sortResults, new SortByPrice());
                    if(sortResults.size()>0) {
                        ArrayList<Music> sortLast = new ArrayList<>();
                        ArrayList<Music> sortFirst = new ArrayList<>();
                        int i = 0;
                        for (Music item : sortResults)
                            if (item.getTrackPrice() == (-1.0))
                                sortLast.add(item);
                            else
                                sortFirst.add(item);

                        sortResults = new ArrayList<>();
                        sortResults.addAll(sortFirst);
                        sortResults.addAll(sortLast);

                    }
                    musicAdapter = new MusicAdapter(MainActivity.this, R.layout.music_item, sortResults);
                    listView.setAdapter(musicAdapter);
                }else {
                    Collections.sort(sortResults, new SortByDate());
                    musicAdapter = new MusicAdapter(MainActivity.this, R.layout.music_item, sortResults);
                    listView.setAdapter(musicAdapter);
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
            }
            return true;
        }


        public class GetSearchDetails extends AsyncTask<String, Void, ArrayList<Music>>{

            @Override
            protected ArrayList<Music> doInBackground(String... strings) {

                HttpURLConnection connection = null;

                if(strings!=null) {
                    String url = baseURL + "?" +
                            "term=" + strings[0]
                            + "&" + "limit=" + seekBar.getProgress();
                    try {
                        URL urlOrg = new URL(url);
                        connection  = (HttpURLConnection) urlOrg.openConnection();
                        connection.connect();

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                            JSONObject root = new JSONObject(json);
                            JSONArray articles = root.getJSONArray("results");
                            for (int i = 0; i < articles.length(); i++) {
                                JSONObject articleJson = articles.getJSONObject(i);
                                Music results = new Music();
                                results.artist = articleJson.has("artistName")?articleJson.getString("artistName"):"Not Available";
                                results.genre = articleJson.has("primaryGenreName")?articleJson.getString("primaryGenreName"):"Not Available";
                                results.trackName = articleJson.has("trackName")?articleJson.getString("trackName"):"Not Available";
                                results.album = articleJson.has("collectionName")?articleJson.getString("collectionName"):"Not Available";
                                results.trackPrice = articleJson.has("trackPrice")?articleJson.getDouble("trackPrice"):-1.0;
                                results.albumPrice = articleJson.has("collectionPrice")?articleJson.getDouble("collectionPrice"):-1.0;
                                results.imageURL = articleJson.has("artworkUrl100")?articleJson.getString("artworkUrl100"):"";
                                results.date = articleJson.has("releaseDate")?articleJson.getString("releaseDate"):"Not Available";
                                searchResults.add(results);
                            }

                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    Toast.makeText(MainActivity.this, "No Records returned", Toast.LENGTH_SHORT).show();
                }


                return  searchResults;
            }

            @Override
            protected void onPostExecute(ArrayList<Music> music) {

                ArrayList<Music> sortResults = new ArrayList<>(searchResults);
                if(priceDateSwitch.isChecked()) {
                    Collections.sort(sortResults, new SortByDate());

                }else {
                    Collections.sort(sortResults, new SortByPrice());
                    if(sortResults.size()>0) {
                        ArrayList<Music> sortLast = new ArrayList<>();
                        ArrayList<Music> sortFirst = new ArrayList<>();
                        int i = 0;
                        for (Music item : sortResults)
                            if (item.getTrackPrice() == (-1.0))
                                sortLast.add(item);
                            else
                                sortFirst.add(item);

                        sortResults = new ArrayList<>();
                        sortResults.addAll(sortFirst);
                        sortResults.addAll(sortLast);

                    }
                }
                musicAdapter = new MusicAdapter(MainActivity.this, R.layout.music_item, sortResults);
                listView.setAdapter(musicAdapter);
                progressBar.setVisibility(View.INVISIBLE);

                if(searchResults.size() ==0){
                    Toast.makeText(MainActivity.this, "No Results Found", Toast.LENGTH_SHORT).show();
                }

         }
    }


    class SortByPrice implements Comparator{

        @Override
        public int compare(Object o1, Object o2) {

            Music m1 = (Music) o1;
            Music m2 = (Music) o2;
            return m1.getTrackPrice().compareTo(m2.getTrackPrice());
        }
    }

    class SortByDate implements Comparator{

        @Override
        public int compare(Object o1, Object o2) {

            Music m1 = (Music) o1;
            Music m2 = (Music) o2;
            return m1.getDate().compareTo(m2.getDate());
        }
    }

}
