package com.apexleague.game.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GameManager {
    public static final String API_BASE_URL = "http://localhost:8080/api";
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
    public int totalWins = 0;
    public int totalP1Wins = 0;
    public int totalP2Wins = 0;
    public int totalBackwardGoals = 0;
    public int totalP1Goals = 0;
    public int totalP2Goals = 0;
    public int totalGoals = 0;
    public int totalSaves = 0;
    public int totalDemos = 0;
    public int totalP1Saves = 0;
    public int totalP2Saves = 0;
    public int mmr = 0;
    public final Array<MatchRecord> matchHistory = new Array<>();
    public String p1CarType = "red_car";
    public String p2CarType = "blue_car";
    public String lastUsedP1Car = "red_car";
    public String lastUsedP2Car = "blue_car";
    public String currentUserToken = "";
    public String currentUserId = "";
    public String jwtToken = "";
    public String currentUsername = "";
    public final Array<LeaderboardEntry> globalLeaderboard = new Array<>();
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

    public static class LeaderboardEntry {
        public String username;
        public double score;
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
            totalWins++;
            getCarStat(p1CarType).wins++;
        } else if (rightScore > leftScore) {
            totalP2Wins++;
            getCarStat(p2CarType).wins++;
        }
        totalP1Goals += p1Goals;
        totalP2Goals += p2Goals;
        totalGoals += p1Goals;
        totalSaves += p1Saves;
        totalDemos += p1Demos;
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

    public void submitMatchResult() {
        if (currentUserId == null || currentUserId.isEmpty() || "GUEST".equalsIgnoreCase(currentUserId)) return;

        String url = API_BASE_URL + "/match";
        String resultText = "DRAW";
        if (recordedWinnerIsRed()) {
            resultText = "RED WIN";
        } else if (recordedWinnerIsBlue()) {
            resultText = "BLUE WIN";
        }

        String payload = "{" +
            "\"player1Id\":\"" + currentUserId + "\"," +
            "\"player1Name\":\"" + (currentUsername != null && !currentUsername.isEmpty() ? currentUsername : "Player1") + "\"," +
            "\"p1Car\":\"" + (p1CarType != null ? p1CarType : "red_car") + "\"," +
            "\"p2Car\":\"" + (p2CarType != null ? p2CarType : "blue_car") + "\"," +
            "\"p1Goals\":" + p1Goals + "," +
            "\"p2Goals\":" + p2Goals + "," +
            "\"p1Saves\":" + p1Saves + "," +
            "\"p1Demos\":" + p1Demos + "," +
            "\"matchResult\":\"" + resultText + "\"}";

        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.POST).url(url).build();
        request.setHeader("Content-Type", "application/json");
        if (jwtToken != null && !jwtToken.isEmpty()) {
            request.setHeader("Authorization", "Bearer " + jwtToken);
        }
        request.setContent(payload);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
            }

            @Override
            public void failed(Throwable t) {
            }

            @Override
            public void cancelled() {
            }
        });
    }

    private boolean recordedWinnerIsRed() {
        return leftScore > rightScore;
    }

    private boolean recordedWinnerIsBlue() {
        return rightScore > leftScore;
    }

    public void fetchUserDataFromBackend() {
    }

    public void fetchUserStats(final Runnable onSuccess) {
        if (currentUsername == null || currentUsername.isEmpty() || "GUEST".equalsIgnoreCase(currentUsername)) {
            if (onSuccess != null) onSuccess.run();
            return;
        }

        String url = API_BASE_URL + "/users/" + currentUsername + "/full";
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url).build();
        request.setHeader("Accept", "application/json");
        if (jwtToken != null && !jwtToken.isEmpty()) {
            request.setHeader("Authorization", "Bearer " + jwtToken);
        }

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                try {
                    String text = httpResponse.getResultAsString();
                    JsonValue root = new JsonReader().parse(text);
                    JsonValue userNode = root.get("user") != null ? root.get("user") : root.get("profile") != null ? root.get("profile") : root;
                    JsonValue statsNode = root.get("stats") != null ? root.get("stats") : root.get("data") != null ? root.get("data") : userNode;

                    if (userNode != null) {
                        currentUserId = userNode.getString("id", currentUserId);
                        currentUsername = userNode.getString("username", currentUsername);
                    }

                    if (statsNode != null) {
                        mmr = statsNode.getInt("mmr", statsNode.getInt("rating", mmr));
                        totalMatches = statsNode.getInt("totalMatchPlayed", statsNode.getInt("totalMatches", totalMatches));
                        totalWins = statsNode.getInt("totalWins", totalWins);
                        totalGoals = statsNode.getInt("totalGoals", totalGoals);
                        totalBackwardGoals = statsNode.getInt("totalBackwardGoals", totalBackwardGoals);
                        totalSaves = statsNode.getInt("totalSaves", totalSaves);
                        totalDemos = statsNode.getInt("totalDemolitions", totalDemos);
                        totalP1Wins = statsNode.getInt("totalP1Wins", totalP1Wins);
                        totalP2Wins = statsNode.getInt("totalP2Wins", totalP2Wins);
                        totalP1Goals = statsNode.getInt("totalP1Goals", totalP1Goals);
                        totalP2Goals = statsNode.getInt("totalP2Goals", totalP2Goals);
                        totalP1Saves = statsNode.getInt("totalP1Saves", totalP1Saves);
                        totalP2Saves = statsNode.getInt("totalP2Saves", totalP2Saves);
                        lastUsedP1Car = statsNode.getString("lastUsedP1Car", lastUsedP1Car);
                        lastUsedP2Car = statsNode.getString("lastUsedP2Car", lastUsedP2Car);
                    }

                    lastUsedP1Car = root.getString("lastUsedP1Car", lastUsedP1Car);
                    lastUsedP2Car = root.getString("lastUsedP2Car", lastUsedP2Car);

                    p1CarType = lastUsedP1Car;
                    p2CarType = lastUsedP2Car;

                    carStats.clear();
                    JsonValue carArray = root.get("carStats") != null ? root.get("carStats") : statsNode != null ? statsNode.get("carStats") : null;
                    if (carArray != null && carArray.isArray()) {
                        for (JsonValue item = carArray.child; item != null; item = item.next) {
                            String carKey = item.getString("carModelId", item.getString("carType", item.getString("type", "")));
                            if (carKey == null || carKey.isEmpty()) {
                                continue;
                            }
                            CarStat stat = new CarStat();
                            stat.wins = item.getInt("wins", item.getInt("totalWins", 0));
                            stat.goals = item.getInt("goalsScored", item.getInt("goals", 0));
                            carStats.put(carKey, stat);
                        }
                    }

                    matchHistory.clear();
                    JsonValue history = root.get("matchHistory") != null ? root.get("matchHistory") : statsNode != null ? statsNode.get("matchHistory") : null;
                    if (history != null && history.isArray()) {
                        for (JsonValue item = history.child; item != null; item = item.next) {
                            MatchRecord r = new MatchRecord();
                            r.date = item.getString("createdAt", item.getString("date", ""));
                            r.p1Score = item.getInt("player1Score", 0);
                            r.p2Score = item.getInt("player2Score", 0);
                            String res = item.getString("matchResult", "");
                            if (res.toUpperCase().contains("P1") || res.toUpperCase().contains("RED")) {
                                r.winner = "RED WIN";
                            } else if (res.toUpperCase().contains("P2") || res.toUpperCase().contains("BLUE")) {
                                r.winner = "BLUE WIN";
                            } else {
                                r.winner = "DRAW";
                            }
                            matchHistory.add(r);
                        }
                    }
                } catch (Exception ignored) {
                }
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void failed(Throwable t) {
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void cancelled() {
                if (onSuccess != null) onSuccess.run();
            }
        });
    }

    public void fetchLeaderboard(String category, final Runnable onSuccess) {
        String safeCategory = category == null || category.isEmpty() ? "mmr" : category;
        String url = API_BASE_URL + "/leaderboard?category=" + safeCategory.toLowerCase() + "&limit=100";
        HttpRequestBuilder builder = new HttpRequestBuilder();
        Net.HttpRequest request = builder.newRequest().method(Net.HttpMethods.GET).url(url).build();
        request.setHeader("Accept", "application/json");
        if (jwtToken != null && !jwtToken.isEmpty()) {
            request.setHeader("Authorization", "Bearer " + jwtToken);
        }

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                try {
                    String responseText = httpResponse.getResultAsString();
                    Gdx.app.log("LEADERBOARD", "Isi JSON API: " + responseText);
                    JsonReader reader = new JsonReader();
                    JsonValue root = reader.parse(responseText);
                    globalLeaderboard.clear();

                    JsonValue entries = root != null && root.isArray() ? root : root != null ? root.get("data") : null;
                    if (entries != null && entries.isArray()) {
                        for (JsonValue item = entries.child; item != null; item = item.next) {
                            LeaderboardEntry e = new LeaderboardEntry();
                            e.username = item.getString("username", "PLAYER");
                            e.score = item.getDouble("score", 0.0);
                            globalLeaderboard.add(e);
                        }
                    }

                    Gdx.app.log("LEADERBOARD", "Parsed " + globalLeaderboard.size + " entries");
                } catch (Exception ignored) {
                }
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void failed(Throwable t) {
                globalLeaderboard.clear();
                if (onSuccess != null) onSuccess.run();
            }

            @Override
            public void cancelled() {
                globalLeaderboard.clear();
                if (onSuccess != null) onSuccess.run();
            }
        });
    }

    public void resetMatchRecordFlag() {
        matchRecorded = false;
    }
}
