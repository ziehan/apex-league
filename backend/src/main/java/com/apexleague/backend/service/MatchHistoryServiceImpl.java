package com.apexleague.backend.service;

import com.apexleague.backend.dto.MatchHistoryDto;
import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.repository.MatchHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MatchHistoryServiceImpl implements MatchHistoryService {

    private final MatchHistoryRepository matchHistoryRepository;

    public MatchHistoryServiceImpl(MatchHistoryRepository matchHistoryRepository) {
        this.matchHistoryRepository = matchHistoryRepository;
    }

    @Override
    public MatchHistory saveMatch(MatchHistoryDto dto) {
        MatchHistory match = new MatchHistory();
        match.setPlayer1Id(dto.getPlayer1Id());
        match.setPlayer2Name(dto.getPlayer2Name());
        match.setPlayer1Score(dto.getPlayer1Score());
        match.setPlayer2Score(dto.getPlayer2Score());
        match.setMatchResult(dto.getMatchResult());
        return matchHistoryRepository.save(match);
    }

    @Override
    public List<MatchHistory> getPlayerMatchHistory(String player1Id) {
        return matchHistoryRepository.findByPlayer1IdOrderByCreatedAtDesc(player1Id);
    }

    @Override
    public void deleteMatchById(Long matchId) {
        if (!matchHistoryRepository.existsById(matchId)) {
            throw new NoSuchElementException("Match history tidak ditemukan");
        }
        matchHistoryRepository.deleteById(matchId);
    }
}
