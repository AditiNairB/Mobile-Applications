package com.example.homework2;

import java.io.Serializable;

public class Music implements Serializable {

    String trackName;
    String genre;
    String artist;
    String album;
    String imageURL;
    String date;
    Double trackPrice;
    Double albumPrice;

    @Override
    public String toString() {
        return "Music{" +
                "trackName='" + trackName + '\'' +
                ", genre='" + genre + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", trackPrice=" + trackPrice +
                ", albumPrice=" + albumPrice +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Double getTrackPrice() {
        return trackPrice;
    }

    public void setTrackPrice(Double trackPrice) {
        this.trackPrice = trackPrice;
    }

    public Double getAlbumPrice() {
        return albumPrice;
    }

    public void setAlbumPrice(Double albumPrice) {
        this.albumPrice = albumPrice;
    }
}
