package com.apexleague.backend.repository;

import com.apexleague.backend.model.MatchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchHistoryRepository extends JpaRepository<MatchHistory, String> {
    List<MatchHistory> findByPlayer1IdOrderByCreatedAtDesc(String player1Id);
}
