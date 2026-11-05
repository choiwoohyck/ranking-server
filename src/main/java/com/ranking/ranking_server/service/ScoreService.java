package com.ranking.ranking_server.service;

import com.ranking.ranking_server.domain.Score;
import com.ranking.ranking_server.repository.ScoreRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Transactional
public class ScoreService {
    private final ScoreRepository scoreRepository;

    public ScoreService(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }

    public void submit(String userId, long score) {
        scoreRepository.findById(userId).ifPresentOrElse(s ->
                        s.updateIfHigher(score, LocalDateTime.now()),
                () -> {
                    Score newScore = new Score(userId, score, LocalDateTime.now());
                    scoreRepository.save(newScore);
                }
        );
    }

    public List<Score> getRankings(int limit) {
        return scoreRepository.findAll().stream()
                .sorted(Comparator.comparing(Score::getScore).reversed()
                        .thenComparing(Score::getAchievedAt))
                .limit(limit)
                .toList();
    }

}
