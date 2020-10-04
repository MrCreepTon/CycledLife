package me.mrcreepton.cycledlife.models;

import me.mrcreepton.cycledlife.GameManager;

public class PlayerModel {

    private String playerName;
    private int timeLeft;
    private String worldName;
    private int worldType = 0;

    public PlayerModel(String playerName, String worldName) {
        this.playerName = playerName;
        this.timeLeft = GameManager.time;
        this.worldName = worldName;
    }

    public PlayerModel(String playerName, int timeLeft, String worldName) {
        this.playerName = playerName;
        this.timeLeft = timeLeft;
        this.worldName = worldName;
    }

    public int getWorldType() {
        return worldType;
    }

    public void setWorldType(int worldType) {
        this.worldType = worldType;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }
}
