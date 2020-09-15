package com.example.inclass14;

import java.io.Serializable;
import java.util.ArrayList;

public class TripDetails implements Serializable {

    String city;
    String trip;
    Double lat;
    Double lng;
    String documentID;

    ArrayList<Places> placesArrayList;

    public ArrayList<Places> getPlacesArrayList() {
        return placesArrayList;
    }

    public void setPlacesArrayList(ArrayList<Places> placesArrayList) {
        this.placesArrayList = placesArrayList;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "TripDetails{" +
                "city='" + city + '\'' +
                ", trip='" + trip + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", documentID='" + documentID + '\'' +
                ", placesArrayList=" + placesArrayList +
                '}';
    }
}
