package com.example.springboot;

public class Player {
    private String userName;
    private int userId;
    private boolean ready;
    private float accuracy;

    public Player(String userName) {
        this.userName = userName;
    }

    public void setReady(boolean b) {
        ready = b;
    }

    public boolean isReady() {
        return ready;
    }

    public void setAccuracy(float acc) {
        accuracy = acc;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public String getUsername() {
        return  userName;
    }
}
