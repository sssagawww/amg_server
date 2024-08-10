package com.example.springboot;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.springboot.Global.players;

public class Room {
    private int roomId;
    private HashMap<Integer, Player> players;
    private final String miniGame;

    public Room(int roomId, String miniGame) {
        this.roomId = roomId;

        players = new HashMap<>();
//        players.put(host.getUserId(), host);


        this.miniGame = miniGame;
    }

    public void addPlayer(Player player) {
        players.put(player.getUserId(), player);
    }

    public void deletePlayer(int playerId) {
        players.remove(playerId);
    }

    public boolean isEmpty() {
        return players.isEmpty();
    }

    public Player getPlayer(int userId) {
        return players.get(userId);
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean everyoneIsReady() {
        for (Player player : players.values()) {
            if (!player.isReady()) {
                return false;
            }
        }
        return true;
    }

    public String getMiniGame() {
        return miniGame;
    }

    public String getWinner() {
        String winnerNick = "";
        float maxAcc = Float.MIN_VALUE;

        for (Player player : players.values()) {
            if (player.getAccuracy() > maxAcc) {
                maxAcc = player.getAccuracy();
                winnerNick = player.getUsername();
            }
        }
        return winnerNick;
    }
}
