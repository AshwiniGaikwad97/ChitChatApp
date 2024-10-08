package com.itkida.chitchatapp;

public class User {
    private String uid;
    private String name;
    private String email;

    // Required empty constructor for Firebase deserialization
    public User() {}

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

