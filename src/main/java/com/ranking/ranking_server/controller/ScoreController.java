package com.ranking.ranking_server.controller;

import com.ranking.ranking_server.domain.Score;
import com.ranking.ranking_server.dto.RankingResponse;
import com.ranking.ranking_server.dto.ScoreSubmitRequest;
import com.ranking.ranking_server.exception.AuthenticationException;
import com.ranking.ranking_server.security.JwtTokenProvider;
import com.ranking.ranking_server.service.ScoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ScoreController {
    private static final String BEARER_PREFIX = "Bearer ";

    private final ScoreService scoreService;
    private final JwtTokenProvider jwtTokenProvider;

    public ScoreController(ScoreService scoreService, JwtTokenProvider jwtTokenProvider) {
        this.scoreService = scoreService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/scores")
    public void submit(@RequestHeader("Authorization") String authorization,
                       @Valid @RequestBody ScoreSubmitRequest request) {
        String userId = jwtTokenProvider.getUserId(extractToken(authorization));
        scoreService.submit(userId, request.score());
    }

    // "Bearer eyJ..." → "eyJ..."
    private String extractToken(String authorization) {
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("Authorization 헤더는 'Bearer 토큰' 형식이어야 합니다.");
        }
        return authorization.substring(BEARER_PREFIX.length());
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
