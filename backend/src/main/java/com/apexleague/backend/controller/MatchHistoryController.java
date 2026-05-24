package com.apexleague.backend.controller;

import com.apexleague.backend.dto.MatchSubmitRequestDto;
import com.apexleague.backend.model.MatchHistory;
import com.apexleague.backend.service.MatchHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class MatchHistoryController {

    private final MatchHistoryService matchHistoryService;

    public MatchHistoryController(MatchHistoryService matchHistoryService) {
        this.matchHistoryService = matchHistoryService;
    }

    @PostMapping("/matches")
    public ResponseEntity<MatchHistory> recordMatch(@Valid @RequestBody MatchSubmitRequestDto dto) {
        return new ResponseEntity<>(matchHistoryService.saveMatch(dto), HttpStatus.CREATED);
    }

    @PostMapping("/match")
    public ResponseEntity<MatchHistory> recordMatchExternal(@Valid @RequestBody MatchSubmitRequestDto request) {
        return new ResponseEntity<>(matchHistoryService.saveMatch(request), HttpStatus.CREATED);
    }

    @GetMapping("/matches/player/{playerId}")
    public ResponseEntity<List<MatchHistory>> getHistory(@PathVariable String playerId) {
        return ResponseEntity.ok(matchHistoryService.getPlayerMatchHistory(playerId));
    }

    @DeleteMapping("/matches/{matchId}")
    public ResponseEntity<Void> deleteMatch(@PathVariable String matchId) {
        matchHistoryService.deleteMatchById(matchId);
        return ResponseEntity.noContent().build();
    }
}
