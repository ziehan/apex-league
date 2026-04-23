package com.apexleague.backend.controller;

import com.apexleague.backend.dto.MatchHistoryDto;
import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.service.MatchHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    public MatchHistoryController(MatchHistoryService matchHistoryService) {
        this.matchHistoryService = matchHistoryService;
    }

    @PostMapping
    public ResponseEntity<MatchHistory> recordMatch(@Valid @RequestBody MatchHistoryDto dto) {
        return new ResponseEntity<>(matchHistoryService.saveMatch(dto), HttpStatus.CREATED);
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MatchHistory>> getHistory(@PathVariable String playerId) {
        return ResponseEntity.ok(matchHistoryService.getPlayerMatchHistory(playerId));
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long matchId) {
        matchHistoryService.deleteMatchById(matchId);
        return ResponseEntity.noContent().build();
    }
}
