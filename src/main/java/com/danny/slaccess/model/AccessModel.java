package com.danny.slaccess.model;

public class AccessModel {
    int id;
    String in;
    String out;

    public AccessModel(int id, String in, String out) {
        this.id = id;
        this.in = in;
        this.out = out;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getOut() {
        return out;
    }

    public void setOut(String out) {
        this.out = out;
    }
}
