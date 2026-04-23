package com.apexleague.backend.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "username", "totalGoals", "totalBackwardGoals", "totalAssists", "totalSaves", "totalDemolitions"})
public class UserResponseDto {
    private String id;
    private String username;
    private int totalGoals;
    private int totalBackwardGoals;
    private int totalAssists;
    private int totalSaves;
    private int totalDemolitions;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
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
}