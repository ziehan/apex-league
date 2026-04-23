package com.apexleague.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MatchHistoryDto {

    @NotNull(message = "ID Player 1 wajib diisi")
    private String player1Id;

    @NotBlank(message = "Nama Player 2 wajib diisi")
    private String player2Name;

    @Min(value = 0, message = "Skor tidak boleh negatif")
    private int player1Score;

    @Min(value = 0, message = "Skor tidak boleh negatif")
    private int player2Score;

    @NotBlank(message = "Hasil pertandingan wajib diisi")
    private String matchResult;

    public String getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(String player1Id) {
        this.player1Id = player1Id;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public String getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(String matchResult) {
        this.matchResult = matchResult;
    }
}
