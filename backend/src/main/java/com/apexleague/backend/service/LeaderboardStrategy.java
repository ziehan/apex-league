package com.apexleague.backend.service;

import com.apexleague.backend.dto.LeaderboardEntryDto;
import java.util.List;

public interface LeaderboardStrategy {
    List<LeaderboardEntryDto> getTopPlayers(int limit);
}

