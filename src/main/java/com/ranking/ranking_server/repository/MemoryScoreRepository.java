package com.ranking.ranking_server.repository;

import com.ranking.ranking_server.domain.Score;

import java.util.*;

public class MemoryScoreRepository implements ScoreRepository {

    private static Map<String,Score> store = new HashMap<>();

    @Override
    public Score save(Score score) {
        store.put(score.getUserId(),score);
        return score;
    }

    @Override
    public Optional<Score> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Score> findAll() {
        return new ArrayList<>(store.values());
    }

    public void clearStore(){store.clear();}
}
