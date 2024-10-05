package com.itkida.chitchatapp;

public class UserProfile {
    private String name;
    private String uid;
    private String nameLower;
    private String email;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    }

    public UserProfile(String uid,String name,String nameLower, String email) {
        this.uid = uid;
        this.name = name;
        this.nameLower = nameLower;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }
    public String getName() {
        return name;
    }
    public String getNameLower() {
        return nameLower;
    }

    public String getEmail() {
        return email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNameLower(String name) {
        this.nameLower = nameLower;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

