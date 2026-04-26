package com.apexleague.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "total_match_played")
    private int totalMatchPlayed = 0;

    @Column(name = "total_wins")
    private int totalWins = 0;

    @Column(name = "total_goals")
    private int totalGoals = 0;

    @Column(name = "total_backward_goals")
    private int totalBackwardGoals = 0;

    @Column(name = "total_assists")
    private int totalAssists = 0;

    @Column(name = "total_saves")
    private int totalSaves = 0;

    @Column(name = "total_demolitions")
    private int totalDemolitions = 0;

    @Column(name = "total_hat_tricks")
    private int totalHatTricks = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}