package com.example.marketoptimizer.database;


public class Player {

    Integer playerId;
    String playerName;
    Integer tradingCount;

    public Player() {}

    public Player(Integer playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
    }

    public void setPlayerId (Integer playerId) {
        this.playerId = playerId;
    }
    public Integer getPlayerId () {
        return playerId;
    }

    public void setPlayerName (String playerName) {
        this.playerName = playerName;
    }
    public String getPlayerName () {
        return playerName;
    }

    public Integer gettradingCount () {
        return tradingCount;
    }


}
