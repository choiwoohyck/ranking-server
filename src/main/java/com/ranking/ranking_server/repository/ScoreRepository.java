package com.ranking.ranking_server.repository;

import com.ranking.ranking_server.domain.Score;

import java.util.List;
import java.util.Optional;

public interface ScoreRepository {
    Score save(Score score);
    Optional<Score>findById(String id);
    List<Score> findAll();
}
