package com.example.inclass08;

import java.io.Serializable;

public class EmailContent implements Serializable {

    String sender_fname;
    String sender_lname;
    String id;
    String sender_id;
    String receiver_id;
    String message;
    String subject;
    String created_at;
    String updated_at;

    public String getSender_fname() {
        return sender_fname;
    }

    public String getSender_lname() {
        return sender_lname;
    }

    public String getId() {
        return id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public String getMessage() {
        return message;
    }

    public String getSubject() {
        return subject;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
