package com.example.springboot;

import java.util.ArrayList;
import java.util.HashMap;

public class Global {
    public static HashMap<Integer, Player> players = new HashMap<>();
    public static String miniGame = "";

    public static HashMap<Integer, Room> getRoomByPlayerID = new HashMap<>();
    public static HashMap<Integer, Room> getRoomByRoomID = new HashMap<>();


    public static final String MUSHROOMS = "mushroomsMiniGame";
    public static final String PAINT = "paintMiniGame";
}
