package com.example.homework2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DisplayActivity extends AppCompatActivity {

    TextView track;
    TextView genre;
    TextView artist;
    TextView album;
    TextView trackPrice;
    TextView albumPrice;
    ImageView titlePic;
    Button finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        setTitle("iTunes Music Search");

        track = findViewById(R.id.tv_dispTrack);
        genre = findViewById(R.id.tv_dispGenre);
        artist = findViewById(R.id.tv_dispArtist);
        album = findViewById(R.id.tv_dispAlbum);
        trackPrice = findViewById(R.id.tv_dispPrice);
        albumPrice = findViewById(R.id.tv_albumPrice);
        titlePic = findViewById(R.id.iv_title);
        finish = findViewById(R.id.btn_finish);

        if(getIntent()!=null && getIntent().getExtras() !=null){

            Music music = (Music) getIntent().getExtras().getSerializable("DISPLAY");

            if(!music.getTrackName().isEmpty())
                track.setText("Track: " + music.getTrackName());
            else
                track.setText("Track: ");
            if(!music.getGenre().isEmpty())
                genre.setText("Genre: " + music.getGenre());
            else
                genre.setText("Genre: ");
            if(!music.getArtist().isEmpty())
                artist.setText("Artist: " + music.getArtist());
            else
                artist.setText("Artist: ");
            if(!music.getAlbum().isEmpty())
                album.setText("Album: " + music.getAlbum());
            else
                album.setText("Album: ");
            if(music.getTrackPrice()!=-1.0)
                trackPrice.setText("Track Price: "+ music.getTrackPrice() + " $");
            else
                trackPrice.setText("Track Price: Not Available");
            if(music.getAlbumPrice()!=-1.0)
                albumPrice.setText("Album Price: "+ music.getAlbumPrice() + " $");
            else
                albumPrice.setText("Album Price: Not Available");

            if(!(music.getImageURL()==null|| music.getImageURL().equals("")))
            Picasso.get().load(music.getImageURL()).into(titlePic);

        }

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
