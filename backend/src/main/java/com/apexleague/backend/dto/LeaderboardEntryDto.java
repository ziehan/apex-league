package com.apexleague.backend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeaderboardEntryDto {
    private String username;
    private double score;

    public static LeaderboardEntryDto fromEntity(String username, double score) {
        return LeaderboardEntryDto.builder()
                .username(username)
                .score(score)
                .build();
    }
}
