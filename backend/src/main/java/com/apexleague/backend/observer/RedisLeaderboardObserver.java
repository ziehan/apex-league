package com.apexleague.backend.observer;

import com.apexleague.backend.event.MatchSavedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RedisLeaderboardObserver {

    private final StringRedisTemplate stringRedisTemplate;

    public RedisLeaderboardObserver(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMatchSaved(MatchSavedEvent event) {
        stringRedisTemplate.opsForZSet().incrementScore("leaderboard:wins", event.getUsername(), event.getWinsDelta());
        stringRedisTemplate.opsForZSet().incrementScore("leaderboard:goals", event.getUsername(), event.getGoalsDelta());
        stringRedisTemplate.opsForZSet().incrementScore("leaderboard:saves", event.getUsername(), event.getSavesDelta());
        stringRedisTemplate.opsForZSet().incrementScore("leaderboard:demos", event.getUsername(), event.getDemosDelta());
    }
}

