package com.example.inclass08;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DisplayMailActivity extends AppCompatActivity {

    TextView tv_sender;
    TextView tv_subject;
    TextView tv_createdAt;
    TextView tv_messageBox;

    Button btn_close;

    DisplayMail displayMail = new DisplayMail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_mail);

        setTitle("Mail");

        tv_sender = findViewById(R.id.tv_sender);
        tv_subject = findViewById(R.id.tv_subject);
        tv_createdAt = findViewById(R.id.tv_createdAt);
        tv_messageBox = findViewById(R.id.tv_messageBox);

        btn_close = findViewById(R.id.btn_close);

        EmailContent emailContent = (EmailContent) getIntent().getExtras().get("Mail");

        String pattern1 = "MMM dd, yyyy";
        String pattern2 = "yyyy-mm-dd";
        String dateNew = "";
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat(pattern1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern2);
        String dateSet = emailContent.getCreated_at().split(" ")[0].trim();
        try {
            Date date = simpleDateFormat.parse(dateSet);
            dateNew = simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        displayMail.setCreatedAt(dateNew);
        displayMail.setMessage(emailContent.getMessage());
        displayMail.setSender(emailContent.getSender_fname()+" "+emailContent.getSender_lname());
        displayMail.setSubject(emailContent.getSubject());

        tv_sender.setText("Sender:  " + displayMail.getSender());
        tv_subject.setText("Subject:  " +displayMail.getSubject());
        tv_createdAt.setText("Created at:  " +displayMail.getCreatedAt());
        tv_messageBox.setText("Message:\n\n" +displayMail.getMessage());

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
