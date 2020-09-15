package com.example.inclass_07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TriviaStats extends AppCompatActivity {

    TextView tv_percentage;
    TextView tv_message;
    ProgressBar progressBarResult;
    Button btn_tryAgain;
    Button btn_quit;

    double percentage = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_stats);

        setTitle("TRIVIA STATS");

        tv_message = findViewById(R.id.tv_message);
        tv_percentage = findViewById(R.id.tv_percentage);
        progressBarResult = findViewById(R.id.progressBarResult);
        btn_tryAgain = findViewById(R.id.btn_tryAgain);
        btn_quit = findViewById(R.id.btn_quit);

        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent display = new Intent(TriviaStats.this, MainActivity.class);
                display.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                display.putExtra(MainActivity.TRIVIA,MainActivity.triviaListFinal);
                startActivity(display);
            }
        });

        btn_tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TriviaActivity.correctAnswers = 0;
                Intent display = new Intent(TriviaStats.this, TriviaActivity.class);
                display.putExtra(MainActivity.TRIVIA,MainActivity.triviaListFinal);
                startActivity(display);
            }
        });

        percentage = (TriviaActivity.correctAnswers / TriviaActivity.triviaList.size()) * 100;
        tv_percentage.setText((int)percentage + "%");
        progressBarResult.setProgress((int)percentage);

        if(percentage<100){
            tv_message.setText("Try again and see if you can get all the correct answers!");
        }
        else{
            tv_message.setText("CONGRATULATIONS!");
        }
    }
}
