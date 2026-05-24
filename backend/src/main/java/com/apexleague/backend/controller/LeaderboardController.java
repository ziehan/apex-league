package com.apexleague.backend.controller;

import com.apexleague.backend.dto.LeaderboardEntryDto;
import com.apexleague.backend.service.LeaderboardService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;
    private final StringRedisTemplate stringRedisTemplate;

    public LeaderboardController(LeaderboardService leaderboardService, StringRedisTemplate stringRedisTemplate) {
        this.leaderboardService = leaderboardService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(
            @RequestParam(defaultValue = "mmr") String category,
            @RequestParam(value = "limit", defaultValue = "100") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopPlayers(category, limit));
    }

    @GetMapping("/keys")
    public ResponseEntity<List<String>> getLeaderboardKeys() {
        java.util.Set<String> keys = stringRedisTemplate.keys("leaderboard:*");
        java.util.List<String> list = keys == null ? java.util.Collections.emptyList() : new java.util.ArrayList<>(keys);
        return ResponseEntity.ok(list);
    }
}
