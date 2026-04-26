package com.apexleague.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.LocalDateTime;

@JsonPropertyOrder({"id", "username", "createdAt", "goalPerMatch", "assistPerMatch", "savesPerMatch", "totalMatchPlayed", "totalWins", "totalGoals", "totalBackwardGoals", "totalAssists", "totalSaves", "totalDemolitions", "totalHatTricks"})
public class UserResponseDto {
    private String id;
    private String username;
    private LocalDateTime createdAt;
    private double goalPerMatch;
    private double assistPerMatch;
    private double savesPerMatch;
    private int totalMatchPlayed;
    private int totalWins;
    private int totalGoals;
    private int totalBackwardGoals;
    private int totalAssists;
    private int totalSaves;
    private int totalDemolitions;
    private int totalHatTricks;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public double getGoalPerMatch() { return goalPerMatch; }
    public void setGoalPerMatch(double goalPerMatch) { this.goalPerMatch = goalPerMatch; }
    public double getAssistPerMatch() { return assistPerMatch; }
    public void setAssistPerMatch(double assistPerMatch) { this.assistPerMatch = assistPerMatch; }
    public double getSavesPerMatch() { return savesPerMatch; }
    public void setSavesPerMatch(double savesPerMatch) { this.savesPerMatch = savesPerMatch; }
    public int getTotalMatchPlayed() { return totalMatchPlayed; }
    public void setTotalMatchPlayed(int totalMatchPlayed) { this.totalMatchPlayed = totalMatchPlayed; }
    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) { this.totalWins = totalWins; }
    public int getTotalGoals() { return totalGoals; }
    public void setTotalGoals(int totalGoals) { this.totalGoals = totalGoals; }
    public int getTotalBackwardGoals() { return totalBackwardGoals; }
    public void setTotalBackwardGoals(int totalBackwardGoals) { this.totalBackwardGoals = totalBackwardGoals; }
    public int getTotalAssists() { return totalAssists; }
    public void setTotalAssists(int totalAssists) { this.totalAssists = totalAssists; }
    public int getTotalSaves() { return totalSaves; }
    public void setTotalSaves(int totalSaves) { this.totalSaves = totalSaves; }
    public int getTotalDemolitions() { return totalDemolitions; }
    public void setTotalDemolitions(int totalDemolitions) { this.totalDemolitions = totalDemolitions; }
    public int getTotalHatTricks() { return totalHatTricks; }
    public void setTotalHatTricks(int totalHatTricks) { this.totalHatTricks = totalHatTricks; }
}