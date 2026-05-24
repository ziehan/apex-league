package com.apexleague.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchSubmitRequestDto {
    private String player1Id;
    private String player1Name;
    private String p1Car;
    private String p2Car;
    private int p1Goals;
    private int p2Goals;
    private int p1Saves;
    private int p1Demos;
    private String matchResult;
}

