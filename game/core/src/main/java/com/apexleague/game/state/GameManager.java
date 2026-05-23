package com.apexleague.game.state;

public final class GameManager {
    private static final GameManager INSTANCE = new GameManager();

    public int leftScore = 0;
    public int rightScore = 0;
    public boolean isResetting = false;
    public float resetTimer = 0f;
    public boolean isKickoff = true;
    public float kickoffTimer = 3f;
    public float matchTimer = 300f;
    public boolean isOvertime = false;
    public boolean isGameOver = false;
    public String winnerText = "";
    public boolean isPaused = false;
    public int p1Goals = 0;
    public int p1Saves = 0;
    public int p1Demos = 0;
    public int p2Goals = 0;
    public int p2Saves = 0;
    public int p2Demos = 0;

    private GameManager() {
    }

    public static GameManager getInstance() {
        return INSTANCE;
    }

    public void addScore(boolean isLeftTeam) {
        if (isLeftTeam) {
            leftScore++;
        } else {
            rightScore++;
        }
    }

    public void startReset() {
        isResetting = true;
        resetTimer = 3f;
        isKickoff = false;
        kickoffTimer = 0f;
    }

    public void startKickoff() {
        isKickoff = true;
        kickoffTimer = 3f;
    }

    public void resetStats() {
        p1Goals = 0;
        p1Saves = 0;
        p1Demos = 0;
        p2Goals = 0;
        p2Saves = 0;
        p2Demos = 0;
    }
}
