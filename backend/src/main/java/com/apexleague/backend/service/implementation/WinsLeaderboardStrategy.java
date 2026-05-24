package com.apexleague.backend.service.implementation;

import com.apexleague.backend.dto.LeaderboardEntryDto;
import com.apexleague.backend.service.LeaderboardStrategy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component("wins")
public class WinsLeaderboardStrategy implements LeaderboardStrategy {

    private final StringRedisTemplate stringRedisTemplate;

    public WinsLeaderboardStrategy(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public List<LeaderboardEntryDto> getTopPlayers(int limit) {
        String key = "leaderboard:wins";
        if (!stringRedisTemplate.hasKey(key)) {
            System.out.println("DEBUG: Redis key not found: " + key + " for category wins");
            return new ArrayList<>();
        }
        Set<ZSetOperations.TypedTuple<String>> results = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, Math.max(0, limit - 1));
        System.out.println("DEBUG: Fetching wins from Redis key: " + key + ". Result size: " + (results != null ? results.size() : 0));
        return toEntries(results);
    }

    private List<LeaderboardEntryDto> toEntries(Set<ZSetOperations.TypedTuple<String>> results) {
        List<LeaderboardEntryDto> entries = new ArrayList<>();
        if (results == null) {
            return entries;
        }
        for (ZSetOperations.TypedTuple<String> tuple : results) {
            if (tuple.getValue() == null || tuple.getScore() == null) {
                continue;
            }
            entries.add(LeaderboardEntryDto.fromEntity(tuple.getValue(), tuple.getScore()));
        }
        return entries;
    }
}

