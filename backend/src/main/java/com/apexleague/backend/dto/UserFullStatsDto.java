package com.apexleague.backend.dto;

import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.model.User;
import com.apexleague.backend.model.UserCarStat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserFullStatsDto {
    private User user;
    private List<MatchHistory> matchHistory;
    private List<UserCarStat> carStats;
    private String lastUsedP1Car;
    private String lastUsedP2Car;
}
