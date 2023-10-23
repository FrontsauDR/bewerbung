package com.example.marketoptimizer;


public class NameSettings {
    private String playerNumber;
    private String playerName;

    public NameSettings(String numb, String name) {
        this.playerNumber = numb;
        this.playerName = name;
    }
    public NameSettings() {}

    // Getter
    public String getPlayerName() {
        return playerName;
    }
    public String getPlayerNumber() {
        return playerNumber;
    }

    // Setter
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlayerNumber(String playerNumber) {
        this.playerNumber = playerNumber;
    }
}
