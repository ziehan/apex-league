package com.apexleague.backend.repository;

import com.apexleague.backend.model.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, UUID> {
    List<MatchHistory> findTop20ByPlayer1IdOrderByCreatedAtDesc(UUID player1Id);
}
