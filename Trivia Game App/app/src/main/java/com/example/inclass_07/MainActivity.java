/**
 * Assignment : In Class 07
 * Group No: 9
 * Members: Aditi Balachandran and Luckose Manuel.
 */

package com.example.inclass_07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tv_TriviaReady;
    ImageView iv_TriviaImage;
    ProgressBar progressBar;
    Button button_Exit;
    Button button_Start;
    static String TRIVIA = "Trivia";
    static ArrayList<Trivia> triviaListFinal = new ArrayList<>();
    public static String aptUrl = "https://dev.theappsdr.com/apis/trivia_json/index.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("TRIVIA APP");

        tv_TriviaReady = findViewById(R.id.tv_TriviaReady);
        progressBar = findViewById(R.id.progressBar);
        iv_TriviaImage = findViewById(R.id.iv_TriviaImage);
        button_Start = findViewById(R.id.button_Start);
        button_Exit = findViewById(R.id.button_Exit);

        progressBar.setVisibility(View.VISIBLE);
        tv_TriviaReady.setVisibility(View.INVISIBLE);
        iv_TriviaImage.setVisibility(View.INVISIBLE);
        button_Start.setEnabled(false);

        if(isConnected()) {
            new GetTrivia().execute();
        }else{
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        button_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //finish();
                    System.exit(0);
            }
        });

        button_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent display = new Intent(MainActivity.this, TriviaActivity.class);
                display.putExtra(TRIVIA,triviaListFinal);
                startActivity(display);
            }
        });


    }

    public class GetTrivia extends AsyncTask<Void,Void,ArrayList<Trivia>>{

        @Override
        protected ArrayList<Trivia> doInBackground(Void... voids) {
            HttpURLConnection connection = null;
            String url = aptUrl;
            ArrayList<Trivia> triviaList = new ArrayList<>();
            URL urlB = null;
            try {
                urlB = new URL(url);
                connection  = (HttpURLConnection) urlB.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray questions = root.getJSONArray("questions");

                    for (int i=0; i<questions.length();i++){
                        JSONObject question = questions.getJSONObject(i);
                        Trivia trivia = new Trivia();
                        trivia.setQuestion(question.getString("text"));//.isEmpty()?"Not Available":question.getString("text"));
                        trivia.setImageUrl(question.has("image")?question.getString("image"):"");
                        //trivia.setQuestion(question.getString("text").isEmpty()?"");
                        JSONObject choicesObj = question.getJSONObject("choices");
                        JSONArray choiceList = choicesObj.getJSONArray("choice");

                        ArrayList<String> choices = new ArrayList<>();

                        for (int j=0; j<choiceList.length();j++)
                            choices.add(choiceList.get(j).toString());

                        trivia.setChoices(choices);
                        trivia.setAnswer(Integer.parseInt(choicesObj.get("answer").toString()));

                        triviaList.add(trivia);
                    }


                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            triviaListFinal = triviaList;
            return triviaList;
        }

        @Override
        protected void onPostExecute(ArrayList<Trivia> triviaList) {
            super.onPostExecute(triviaList);

            if(triviaList.size()>0){
                progressBar.setVisibility(View.INVISIBLE);
                iv_TriviaImage.setVisibility(View.VISIBLE);
                tv_TriviaReady.setVisibility(View.VISIBLE);
                button_Start.setEnabled(true);
            }else {
                Toast.makeText(MainActivity.this, "No Trivia Found.", Toast.LENGTH_SHORT).show();
            }
        }
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
