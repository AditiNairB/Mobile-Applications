package com.example.inclass08;

public class UserData {

    String id;
    String fname;
    String lname;

    public String getUserId() {
        return id;
    }

    public String getFirstName() {
        return fname;
    }

    public String getLastName() {
        return lname;
    }

    public String getName(){
        return fname+" "+lname;
    }
}
