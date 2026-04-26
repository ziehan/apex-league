package com.apexleague.backend.service;

import com.apexleague.backend.dto.MatchHistoryDto;
import com.apexleague.backend.model.MatchHistory;
import java.util.List;

public interface MatchHistoryService {
    MatchHistory saveMatch(MatchHistoryDto dto);
    List<MatchHistory> getPlayerMatchHistory(String player1Id);
    void deleteMatchById(String matchId);
}
