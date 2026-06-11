package com.apexleague.backend.service.implementation;

import com.apexleague.backend.model.UserCarStat;
import com.apexleague.backend.repository.UserCarStatRepository;
import com.apexleague.backend.service.UserCarStatService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserCarStatServiceImpl implements UserCarStatService {

    private final UserCarStatRepository userCarStatRepository;

    public UserCarStatServiceImpl(UserCarStatRepository userCarStatRepository) {
        this.userCarStatRepository = userCarStatRepository;
    }

    @Override
    public UserCarStat updateStats(UserCarStat stats) {
        UserCarStat existing = userCarStatRepository.findById(stats.getId())
                .orElseThrow(() -> new RuntimeException("UserCarStat tidak ditemukan dengan ID: " + stats.getId()));

        if (stats.getCarModelId() != null) {
            existing.setCarModelId(stats.getCarModelId());
        }
        if (stats.getWins() >= 0) {
            existing.setWins(stats.getWins());
        }
        if (stats.getGoalsScored() >= 0) {
            existing.setGoalsScored(stats.getGoalsScored());
        }
        if (stats.getMatchesPlayed() >= 0) {
            existing.setMatchesPlayed(stats.getMatchesPlayed());
        }

        return userCarStatRepository.save(existing);
    }

    @Override
    public List<UserCarStat> getStatsByUserId(String userId) {
        UUID parsedId = UUID.fromString(userId);
        return userCarStatRepository.findByUserId(parsedId);
    }
}
