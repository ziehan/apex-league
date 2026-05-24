package com.apexleague.backend.service.implementation;

import com.apexleague.backend.dto.MatchSubmitRequestDto;
import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.model.User;
import com.apexleague.backend.model.UserCarStat;
import com.apexleague.backend.repository.MatchHistoryRepository;
import com.apexleague.backend.repository.UserCarStatRepository;
import com.apexleague.backend.repository.UserRepository;
import com.apexleague.backend.service.MatchHistoryService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class MatchHistoryServiceImpl implements MatchHistoryService {

    private final MatchHistoryRepository matchHistoryRepository;
    private final UserRepository userRepository;
    private final UserCarStatRepository userCarStatRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MatchHistoryServiceImpl.class);

    public MatchHistoryServiceImpl(MatchHistoryRepository matchHistoryRepository,
                                   UserRepository userRepository,
                                   UserCarStatRepository userCarStatRepository,
                                   StringRedisTemplate stringRedisTemplate) {
        this.matchHistoryRepository = matchHistoryRepository;
        this.userRepository = userRepository;
        this.userCarStatRepository = userCarStatRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    @Transactional
    public MatchHistory saveMatch(MatchSubmitRequestDto dto) {
        UUID playerId = UUID.fromString(dto.getPlayer1Id());
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new NoSuchElementException("Player dengan ID " + dto.getPlayer1Id() + " tidak ditemukan"));

        player.setTotalGoals(player.getTotalGoals() + dto.getP1Goals());
        player.setTotalSaves(player.getTotalSaves() + dto.getP1Saves());
        player.setTotalDemolitions(player.getTotalDemolitions() + dto.getP1Demos());
        player.setTotalMatchPlayed(player.getTotalMatchPlayed() + 1);
        player.setLastUsedP1Car(dto.getP1Car());
        player.setLastUsedP2Car(dto.getP2Car());

        int priorMmr = player.getMmr() == null ? 0 : player.getMmr();
        int currentMmr = priorMmr;
        String matchResult = dto.getMatchResult();
        if ("P1_WIN".equals(matchResult)) {
            player.setTotalWins(player.getTotalWins() + 1);
            int performanceBonus = Math.min(50, (dto.getP1Goals() * 10) + (dto.getP1Saves() * 5) + (dto.getP1Demos() * 5));
            currentMmr += (50 + performanceBonus);
        } else if ("P2_WIN".equals(matchResult)) {
            if (currentMmr > 1000) {
                int mitigation = Math.min(30, (dto.getP1Goals() * 10) + (dto.getP1Saves() * 5));
                currentMmr -= (100 - mitigation);
            } else if (currentMmr > 500) {
                int mitigation = Math.min(40, (dto.getP1Goals() * 10) + (dto.getP1Saves() * 5));
                currentMmr -= (50 - mitigation);
            }
        } else {
            currentMmr += 5;
        }
        player.setMmr(Math.max(0, currentMmr));
        userRepository.save(player);
        int mmrDelta = player.getMmr() - priorMmr;

        UserCarStat p1CarStat = userCarStatRepository.findByUserIdAndCarModelId(playerId, dto.getP1Car())
                .orElse(new UserCarStat());
        if (p1CarStat.getId() == null) {
            p1CarStat.setUser(player);
            p1CarStat.setCarModelId(dto.getP1Car());
        }
        p1CarStat.setMatchesPlayed(p1CarStat.getMatchesPlayed() + 1);
        p1CarStat.setGoalsScored(p1CarStat.getGoalsScored() + dto.getP1Goals());
        if ("P1_WIN".equals(matchResult)) {
            p1CarStat.setWins(p1CarStat.getWins() + 1);
        }

        UserCarStat p2CarStat = userCarStatRepository.findByUserIdAndCarModelId(playerId, dto.getP2Car())
                .orElse(new UserCarStat());
        if (p2CarStat.getId() == null) {
            p2CarStat.setUser(player);
            p2CarStat.setCarModelId(dto.getP2Car());
        }
        p2CarStat.setMatchesPlayed(p2CarStat.getMatchesPlayed() + 1);
        p2CarStat.setGoalsScored(p2CarStat.getGoalsScored() + dto.getP2Goals());
        if ("P2_WIN".equals(matchResult)) {
            p2CarStat.setWins(p2CarStat.getWins() + 1);
        }

        userCarStatRepository.save(p1CarStat);
        userCarStatRepository.save(p2CarStat);

        MatchHistory match = new MatchHistory();
        match.setPlayer1(player);
        match.setPlayer1Name(dto.getPlayer1Name() != null && !dto.getPlayer1Name().isBlank()
                ? dto.getPlayer1Name()
                : player.getUsername());
        match.setPlayer1Score(dto.getP1Goals());
        match.setPlayer2Score(dto.getP2Goals());
        match.setMatchResult(matchResult != null ? matchResult : "DRAW");
        MatchHistory savedMatch = matchHistoryRepository.save(match);

        String member = dto.getPlayer1Name() != null && !dto.getPlayer1Name().isBlank() ? dto.getPlayer1Name() : player.getUsername();

        if (mmrDelta != 0) {
            stringRedisTemplate.opsForZSet().incrementScore("leaderboard:mmr", member, mmrDelta);
            logger.info("Updated Redis leaderboard:{} for user: {} with value: {}", "mmr", member, mmrDelta);
        }

        int winsDelta = "P1_WIN".equals(matchResult) ? 1 : 0;
        if (winsDelta != 0) {
            stringRedisTemplate.opsForZSet().incrementScore("leaderboard:wins", member, winsDelta);
            logger.info("Updated Redis leaderboard:{} for user: {} with value: {}", "wins", member, winsDelta);
        }

        if (dto.getP1Goals() != 0) {
            stringRedisTemplate.opsForZSet().incrementScore("leaderboard:goals", member, dto.getP1Goals());
            logger.info("Updated Redis leaderboard:{} for user: {} with value: {}", "goals", member, dto.getP1Goals());
        }

        if (dto.getP1Saves() != 0) {
            stringRedisTemplate.opsForZSet().incrementScore("leaderboard:saves", member, dto.getP1Saves());
            logger.info("Updated Redis leaderboard:{} for user: {} with value: {}", "saves", member, dto.getP1Saves());
        }

        if (dto.getP1Demos() != 0) {
            stringRedisTemplate.opsForZSet().incrementScore("leaderboard:demos", member, dto.getP1Demos());
            logger.info("Updated Redis leaderboard:{} for user: {} with value: {}", "demos", member, dto.getP1Demos());
        }


        return savedMatch;
    }

    @Override
    public List<MatchHistory> getPlayerMatchHistory(String player1Id) {
        UUID parsedId = UUID.fromString(player1Id);
        return matchHistoryRepository.findByPlayer1IdOrderByCreatedAtDesc(parsedId);
    }

    @Override
    public void deleteMatchById(String matchId) {
        UUID parsedId = UUID.fromString(matchId);
        if (!matchHistoryRepository.existsById(parsedId)) {
            throw new NoSuchElementException("Match history tidak ditemukan");
        }
        matchHistoryRepository.deleteById(parsedId);
    }
}