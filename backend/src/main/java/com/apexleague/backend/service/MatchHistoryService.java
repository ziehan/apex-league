package com.apexleague.backend.service;

import com.apexleague.backend.dto.MatchSubmitRequestDto;
import com.apexleague.backend.model.MatchHistory;
import java.util.List;

public interface MatchHistoryService {
    MatchHistory saveMatch(MatchSubmitRequestDto dto);
    List<MatchHistory> getPlayerMatchHistory(String player1Id);
    void deleteMatchById(String matchId);
}
