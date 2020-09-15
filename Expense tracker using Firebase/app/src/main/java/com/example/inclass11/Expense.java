package com.example.inclass11;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

public class Expense implements Serializable {

    String title;
    Double cost;
    String category;
    String id;
    String date;

    HashMap<String,Object> hmap;

    public Expense(){

    }

    public Expense(String title, Double cost, String category) {
        this.title = title;
        this.cost = cost;
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();
    }

    HashMap<String,Object> toHashMap(){
        hmap = new HashMap<>();
        hmap.put("title",getTitle());
        hmap.put("cost",getCost());
        hmap.put("category",getCategory());
        hmap.put("date",getDate());
        return hmap;
    }
}
