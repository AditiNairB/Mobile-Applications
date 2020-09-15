package com.example.homework2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class MusicAdapter extends ArrayAdapter<Music> {
    public MusicAdapter(@NonNull Context context, int resource, @NonNull List<Music> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Music musicObj = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.music_item, parent,false);
        }

        TextView track = convertView.findViewById(R.id.tv_track);
        TextView trackPrice = convertView.findViewById(R.id.tv_price);
        TextView artist = convertView.findViewById(R.id.tv_artist);
        TextView date = convertView.findViewById(R.id.tv_date);

        if(!musicObj.getTrackName().isEmpty())
            track.setText("Track: "+ musicObj.getTrackName());
        if(musicObj.getTrackPrice()!=-1.0)
            trackPrice.setText("Price: "+ musicObj.getTrackPrice()  + " $");
        else
            trackPrice.setText("Price: Not Available");
        if(!musicObj.getArtist().isEmpty())
            artist.setText("Artist: "+ musicObj.getArtist());
        if(!musicObj.getDate().isEmpty()) {
                DateTimeFormatter dateTimeFormatterInitial = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                LocalDate localDate = LocalDate.parse(musicObj.getDate().split("T")[0],dateTimeFormatterInitial);
                date.setText("Date: "+ localDate.format(dateTimeFormatter));
        }


        return convertView;
    }
}
