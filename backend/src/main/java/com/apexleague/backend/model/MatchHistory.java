package com.apexleague.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "match_history")
public class MatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    private User player1;

    @Column(name = "player1_name", nullable = false)
    private String player1Name = "Player1";

    @Column(name = "player2_name", nullable = false)
    private String player2Name = "Guest";

    @Column(name = "player1_score")
    private int player1Score = 0;

    @Column(name = "player2_score")
    private int player2Score = 0;

    @Column(name = "match_result", nullable = false)
    private String matchResult;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getPlayer1() { return player1; }
    public void setPlayer1(User player1) { this.player1 = player1; }
    public String getPlayer1Name() { return player1Name; }
    public void setPlayer1Name(String player1Name) { this.player1Name = player1Name; }
    public String getPlayer2Name() { return player2Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }
    public int getPlayer1Score() { return player1Score; }
    public void setPlayer1Score(int player1Score) { this.player1Score = player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(int player2Score) { this.player2Score = player2Score; }
    public String getMatchResult() { return matchResult; }
    public void setMatchResult(String matchResult) { this.matchResult = matchResult; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}