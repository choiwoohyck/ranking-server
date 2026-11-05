package com.ranking.ranking_server.controller;

import com.ranking.ranking_server.domain.Score;
import com.ranking.ranking_server.dto.RankingResponse;
import com.ranking.ranking_server.dto.ScoreSubmitRequest;
import com.ranking.ranking_server.service.ScoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScoreController {
    private final ScoreService scoreService;

    public ScoreController(ScoreService scoreService) {
        this.scoreService = scoreService;
    }

    @PostMapping("/scores")
    public void submit(@Valid @RequestBody ScoreSubmitRequest request) {
        scoreService.submit(request.userId(), request.score());
    }

    @GetMapping("/rankings")
    public List<RankingResponse> rankings(@RequestParam(defaultValue = "100") @Min(1)@Max(100) int limit) {
        List<Score> scores = scoreService.getRankings(limit);
        List<RankingResponse> result = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            result.add(new RankingResponse(i + 1, s.getUserId(), s.getScore(), s.getAchievedAt()));
        }
        return result;
    }
}
