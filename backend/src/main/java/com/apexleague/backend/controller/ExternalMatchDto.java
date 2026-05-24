package com.apexleague.backend.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ExternalMatchDto {
    @NotBlank
    private String player1Id;
    @NotBlank
    private String player2Name;
    @Min(0)
    private int player1Score;
    @Min(0)
    private int player2Score;
    @NotBlank
    private String matchResult;
    @NotBlank
    private String p1Car;
    @NotBlank
    private String p2Car;
    @Min(0)
    private int p1Saves;
    @Min(0)
    private int p2Saves;
    @Min(0)
    private int p1Demos;
    @Min(0)
    private int p2Demos;

    public String getPlayer1Id() { return player1Id; }
    public void setPlayer1Id(String player1Id) { this.player1Id = player1Id; }
    public String getPlayer2Name() { return player2Name; }
    public void setPlayer2Name(String player2Name) { this.player2Name = player2Name; }
    public int getPlayer1Score() { return player1Score; }
    public void setPlayer1Score(int player1Score) { this.player1Score = player1Score; }
    public int getPlayer2Score() { return player2Score; }
    public void setPlayer2Score(int player2Score) { this.player2Score = player2Score; }
    public String getMatchResult() { return matchResult; }
    public void setMatchResult(String matchResult) { this.matchResult = matchResult; }
    public String getP1Car() { return p1Car; }
    public void setP1Car(String p1Car) { this.p1Car = p1Car; }
    public String getP2Car() { return p2Car; }
    public void setP2Car(String p2Car) { this.p2Car = p2Car; }
    public int getP1Saves() { return p1Saves; }
    public void setP1Saves(int p1Saves) { this.p1Saves = p1Saves; }
    public int getP2Saves() { return p2Saves; }
    public void setP2Saves(int p2Saves) { this.p2Saves = p2Saves; }
    public int getP1Demos() { return p1Demos; }
    public void setP1Demos(int p1Demos) { this.p1Demos = p1Demos; }
    public int getP2Demos() { return p2Demos; }
    public void setP2Demos(int p2Demos) { this.p2Demos = p2Demos; }
}
