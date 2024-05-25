package com.example.springboot;

public class Player {
    private String userName;
    private int userId;
    private boolean ready;
    private float accuracy;

    public Player(int id, String userName) {
        this.userId = id;
        this.userName = userName;
        ready = false;
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

    public int getUserId() {
        return userId;
    }
}
