package com.ranking.ranking_server.repository;


import com.ranking.ranking_server.domain.Score;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MemoryScoreRepositoryTest {

    MemoryScoreRepository repository = new MemoryScoreRepository();
    LocalDateTime now = LocalDateTime.of(2026, 11, 3, 10, 0);

    @AfterEach
    public void afterEach() {
        repository.clearStore();
    }

    @Test
    public void save() {

        Score score = new Score("Test1", 100L, now);
        repository.save(score);

        Score result = repository.findById(score.getUserId()).get();
        assertThat(result).isEqualTo(score);
    }

    @Test
    public void findById() {
        Score score1 = new Score("Test1", 100L, now);
        repository.save(score1);

        Score score2 = new Score("Test2", 100L, now);
        repository.save(score2);

        Score result = repository.findById("Test1").get();
        assertThat(result).isEqualTo(score1);
    }

    @Test
    public void findAll() {
        Score score1 = new Score("Test1", 100L, now);
        repository.save(score1);

        Score score2 = new Score("Test2", 100L, now);
        repository.save(score2);

        List<Score> result = repository.findAll();
        assertThat(result).hasSize(2);
    }

}
