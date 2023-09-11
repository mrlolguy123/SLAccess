package com.danny.slaccess.model;

public class UserModel {
    int id;
    String name;
    String type;
    boolean auth;

    public UserModel(int id, String name, String type, boolean auth) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.auth = auth;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }
}
