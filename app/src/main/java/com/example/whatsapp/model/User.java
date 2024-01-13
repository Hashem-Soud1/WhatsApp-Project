package com.example.whatsapp.model;

public class User {

    String id;
    String username;
    String status;
    String password;
    String email;
    String imgUrl;
    String state;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public User() {}

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String id, String username, String status, String password, String email, String imgUrl, String state) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.password = password;
        this.email = email;
        this.imgUrl = imgUrl;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
