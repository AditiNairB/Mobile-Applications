package com.example.inclass_07;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TriviaActivity extends AppCompatActivity  implements MyAdapter.OnItemClickListener {


    static ArrayList<Trivia> triviaList;
    int index;
    TextView tv_QuestionNo;
    TextView tv_Timer;
    TextView tv_TriviaQuestion;
    ImageView iv_TriviaImage;
    Button button_Quit;
    Button button_Next;
    ProgressBar progressBarImage;

    static double correctAnswers = 0.0;
    int selectedAnswer = -1;

    RecyclerView recyclerView;
    RecyclerView.Adapter rv_adapter;
    RecyclerView.LayoutManager rv_layoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trivia_layout);

        setTitle("TRIVIA APP");

        tv_QuestionNo = findViewById(R.id.tv_QuestionNo);
        tv_Timer = findViewById(R.id.tv_Timer);
        tv_TriviaQuestion = findViewById(R.id.tv_TriviaQuestion);
        iv_TriviaImage = findViewById(R.id.iv_TriviaImage);
        button_Quit = findViewById(R.id.button_Quit);
        button_Next = findViewById(R.id.button_Next);
        recyclerView = findViewById(R.id.rv_Options);
        progressBarImage = findViewById(R.id.progressBarImage);
        progressBarImage.setVisibility(View.VISIBLE);

        index = 0;

        button_Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent display = new Intent(TriviaActivity.this, MainActivity.class);
                display.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                display.putExtra("EXIT",true);
                startActivity(display);
            }
        });

        button_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if( -1!= (selectedAnswer+1) && (selectedAnswer+1) == triviaList.get(--index).getAnswer()){
                    correctAnswers++;
                }
                selectedAnswer = -1;
                index++;
                if(index < triviaList.size()) {
                    progressBarImage.setVisibility(View.VISIBLE);
                    setValues();
                }else{
                    Intent display = new Intent(TriviaActivity.this, TriviaStats.class);
                    display.putExtra(MainActivity.TRIVIA,correctAnswers);
                    startActivity(display);
                }
            }
        });

        if(getIntent()!=null && getIntent().getExtras() != null) {
            triviaList = (ArrayList<Trivia>) getIntent().getExtras().getSerializable(MainActivity.TRIVIA);
            if(index==0) {

                tv_QuestionNo.setText("Q " + (index + 1));

                new CountDownTimer(2 * 60 * 1000, 1000) {
                    @Override
                    public void onTick(long l) {
                        //long minute = l/1000/60;
                        long second = (l)/1000;
                        tv_Timer.setText("Time Left: " + second + " seconds");
                    }

                    @Override
                    public void onFinish() {
                        Intent display = new Intent(TriviaActivity.this, TriviaStats.class);
                        display.putExtra(MainActivity.TRIVIA,correctAnswers);
                        startActivity(display);
                    }
                }.start();

                triviaList.get(index).setImageUrl(triviaList.get(index).getImageUrl().replace("http","https"));
                Picasso.get().load(triviaList.get(index).getImageUrl()).into(iv_TriviaImage);

                tv_TriviaQuestion.setText(triviaList.get(index).getQuestion());

                recyclerView.setHasFixedSize(true);

                rv_layoutManager = new LinearLayoutManager(this);

                recyclerView.setLayoutManager(rv_layoutManager);

                rv_adapter = new MyAdapter(triviaList.get(index).getChoices(), this, this);

                recyclerView.setAdapter(rv_adapter);

                progressBarImage.setVisibility(View.INVISIBLE);

                index++;

            }
        }
    }

    public void setValues(){
        tv_QuestionNo.setText("Q " + (index + 1));
        if(!triviaList.get(index).getImageUrl().equals("")) {
            /*triviaList.get(index).setImageUrl(triviaList.get(index).getImageUrl().replace("http", "https"));
            Picasso.get().load(triviaList.get(index).getImageUrl()).into(iv_TriviaImage);*/
            new ImageDownload().execute(triviaList.get(index).getImageUrl());
        }else{
            iv_TriviaImage.setImageDrawable(getResources().getDrawable(R.drawable.trivia));
            progressBarImage.setVisibility(View.INVISIBLE);
        }
        tv_TriviaQuestion.setText(triviaList.get(index).getQuestion());

        recyclerView.setHasFixedSize(true);

        rv_layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(rv_layoutManager);

        rv_adapter = new MyAdapter(triviaList.get(index).getChoices(), this, this);

        recyclerView.setAdapter(rv_adapter);

        index++;
    }

    public class ImageDownload extends AsyncTask<String, Void, Bitmap> {

        Bitmap bitmap = null;

        @Override
        protected Bitmap doInBackground(String... strings) {
            HttpURLConnection connection = null;
            bitmap = null;
//            iv_TriviaImage.setImageDrawable(null);

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
            iv_TriviaImage.setImageBitmap(bitmap);
            progressBarImage.setVisibility(View.INVISIBLE);

        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("demo","Clicked Answer: "+ position);
        //Toast.makeText(this, "Selected Answer " + (position+1), Toast.LENGTH_SHORT).show();
        selectedAnswer = position;
        button_Next.callOnClick();
    }
}
