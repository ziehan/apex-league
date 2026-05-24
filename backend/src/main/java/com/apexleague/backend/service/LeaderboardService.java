package com.apexleague.backend.service;

import com.apexleague.backend.dto.LeaderboardEntryDto;
import java.util.List;

public interface LeaderboardService {
    List<LeaderboardEntryDto> getTopPlayers(String category, int limit);
}
