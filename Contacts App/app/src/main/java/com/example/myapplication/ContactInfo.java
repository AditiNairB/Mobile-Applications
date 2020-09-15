package com.example.myapplication;

import java.io.Serializable;
import java.util.HashMap;

public class ContactInfo implements Serializable {

    private String documentID;
    private String contactImageUrl;
    private String contactName;
    private String contactEmail;
    private String contactNumber;

    HashMap<String, Object> hmap;

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getContactImageUrl() {
        return contactImageUrl;
    }

    public void setContactImageUrl(String contactImageUrl) {
        this.contactImageUrl = contactImageUrl;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public ContactInfo(String contactImageUrl, String contactName, String contactEmail, String contactNumber) {

        this.contactImageUrl = contactImageUrl;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactNumber = contactNumber;
    }

    HashMap<String, Object> toHashMap(){
        hmap = new HashMap<>();
        hmap.put("imageurl", getContactImageUrl());
        hmap.put("name", getContactName());
        hmap.put("email", getContactEmail());
        hmap.put("number", getContactNumber());

        return this.hmap;
    }

}
