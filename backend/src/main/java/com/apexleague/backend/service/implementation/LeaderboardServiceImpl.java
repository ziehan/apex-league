package com.apexleague.backend.service.implementation;

import com.apexleague.backend.dto.LeaderboardEntryDto;
import com.apexleague.backend.service.LeaderboardService;
import com.apexleague.backend.service.LeaderboardStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final Map<String, LeaderboardStrategy> strategies;

    public LeaderboardServiceImpl(Map<String, LeaderboardStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public List<LeaderboardEntryDto> getTopPlayers(String category, int limit) {
        System.out.println("Fetching leaderboard for category: " + category + " with limit: " + limit);

        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Category tidak valid");
        }

        String normalizedCategory = category.trim().toLowerCase();
        LeaderboardStrategy strategy = strategies.get(normalizedCategory);

        if (strategy == null) {
            strategy = strategies.entrySet().stream()
                    .filter(entry -> entry.getKey() != null && entry.getKey().equalsIgnoreCase(normalizedCategory))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (strategy == null) {
            throw new IllegalArgumentException("Category tidak valid");
        }

        return strategy.getTopPlayers(limit);
    }
}
