package com.apexleague.game.state;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public int totalMatches = 0;
    public int totalP1Wins = 0;
    public int totalP2Wins = 0;
    public int totalBackwardGoals = 0;
    public int totalP1Goals = 0;
    public int totalP2Goals = 0;
    public int totalGoals = 0;
    public int totalSaves = 0;
    public int totalDemos = 0;
    public final Array<MatchRecord> matchHistory = new Array<>();
    public String p1CarType = "red_car";
    public String p2CarType = "blue_car";
    public String currentUserToken = "";
    public String currentUserId = "";
    private final ObjectMap<String, CarStat> carStats = new ObjectMap<>();
    private boolean matchRecorded = false;

    public static class MatchRecord {
        public String date;
        public int p1Score;
        public int p2Score;
        public String winner;
    }

    public static class CarStat {
        public int wins = 0;
        public int goals = 0;
    }

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

    public void recordMatchEnd() {
        if (matchRecorded) {
            return;
        }
        MatchRecord record = new MatchRecord();
        record.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        record.p1Score = leftScore;
        record.p2Score = rightScore;
        record.winner = winnerText;
        matchHistory.add(record);
        totalMatches++;
        if (leftScore > rightScore) {
            totalP1Wins++;
            getCarStat(p1CarType).wins++;
        } else if (rightScore > leftScore) {
            totalP2Wins++;
            getCarStat(p2CarType).wins++;
        }
        totalP1Goals += p1Goals;
        totalP2Goals += p2Goals;
        totalGoals += p1Goals + p2Goals;
        totalSaves += p1Saves + p2Saves;
        totalDemos += p1Demos + p2Demos;
        matchRecorded = true;
    }

    public CarStat getCarStat(String carType) {
        String key = carType == null || carType.isEmpty() ? "red_car" : carType;
        CarStat stat = carStats.get(key);
        if (stat == null) {
            stat = new CarStat();
            carStats.put(key, stat);
        }
        return stat;
    }

    public void syncMatchDataToBackend(MatchRecord record) {
    }

    public void fetchUserDataFromBackend() {
    }

    public void resetMatchRecordFlag() {
        matchRecorded = false;
    }
}
