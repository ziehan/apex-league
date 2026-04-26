package com.apexleague.backend.service.implementation;

import com.apexleague.backend.dto.MatchHistoryDto;
import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.model.User;
import com.apexleague.backend.model.UserCarStat;
import com.apexleague.backend.repository.MatchHistoryRepository;
import com.apexleague.backend.repository.UserCarStatRepository;
import com.apexleague.backend.repository.UserRepository;
import com.apexleague.backend.service.MatchHistoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MatchHistoryServiceImpl implements MatchHistoryService {

    private final MatchHistoryRepository matchHistoryRepository;
    private final UserRepository userRepository;
    private final UserCarStatRepository userCarStatRepository;

    public MatchHistoryServiceImpl(MatchHistoryRepository matchHistoryRepository, UserRepository userRepository, UserCarStatRepository userCarStatRepository) {
        this.matchHistoryRepository = matchHistoryRepository;
        this.userRepository = userRepository;
        this.userCarStatRepository = userCarStatRepository;
    }

    @Override
    @Transactional
    public MatchHistory saveMatch(MatchHistoryDto dto) {
        User player = userRepository.findById(dto.getPlayer1Id())
                .orElseThrow(() -> new NoSuchElementException("Player dengan ID " + dto.getPlayer1Id() + " tidak ditemukan"));

        player.setTotalMatchPlayed(player.getTotalMatchPlayed() + 1);
        player.setTotalGoals(player.getTotalGoals() + dto.getPlayer1Score());
        player.setTotalDemolitions(player.getTotalDemolitions() + dto.getMatchDemolitions());
        player.setTotalSaves(player.getTotalSaves() + dto.getMatchSaves());
        player.setTotalAssists(player.getTotalAssists() + dto.getMatchAssists());
        player.setTotalBackwardGoals(player.getTotalBackwardGoals() + dto.getMatchBackwardGoals());

        if (dto.getPlayer1Score() >= 3) {
            player.setTotalHatTricks(player.getTotalHatTricks() + 1);
        }

        boolean isWin = "P1_WIN".equalsIgnoreCase(dto.getMatchResult());
        if (isWin) {
            player.setTotalWins(player.getTotalWins() + 1);
        }

        userRepository.save(player);

        UserCarStat carStat = userCarStatRepository.findByUserIdAndCarModelId(dto.getPlayer1Id(), dto.getCarModelId())
                .orElse(new UserCarStat());

        if (carStat.getId() == null) {
            carStat.setUserId(dto.getPlayer1Id());
            carStat.setCarModelId(dto.getCarModelId());
        }

        carStat.setMatchesPlayed(carStat.getMatchesPlayed() + 1);
        carStat.setGoalsScored(carStat.getGoalsScored() + dto.getPlayer1Score());
        if (isWin) {
            carStat.setWins(carStat.getWins() + 1);
        }
        userCarStatRepository.save(carStat);

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
    public void deleteMatchById(String matchId) {
        if (!matchHistoryRepository.existsById(matchId)) {
            throw new NoSuchElementException("Match history tidak ditemukan");
        }
        matchHistoryRepository.deleteById(matchId);
    }
}