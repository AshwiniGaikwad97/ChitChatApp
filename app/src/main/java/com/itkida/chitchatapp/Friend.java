package com.itkida.chitchatapp;

public class Friend {
    private String uid;
    private String name;
    private String email;

    // Required empty constructor for Firestore deserialization
    public Friend() {}

    public Friend(String uid,String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }
    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}

