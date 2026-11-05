package com.ranking.ranking_server.service;

import com.ranking.ranking_server.domain.Score;
import com.ranking.ranking_server.repository.MemoryScoreRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreServiceTest {

    MemoryScoreRepository scoreRepository;
    ScoreService scoreService;

    @BeforeEach
    public void beforeEach() {
        scoreRepository = new MemoryScoreRepository();
        scoreService = new ScoreService(scoreRepository);
    }

    @AfterEach
    public void afterEach() {
        scoreRepository.clearStore();
    }

    @Test
    void 처음_제출하면_저장된다() {
        scoreService.submit("user1", 100L);

        Score result = scoreRepository.findById("user1").get();
        assertThat(result.getScore()).isEqualTo(100L);
    }

    @Test
    void 더_높은_점수를_제출하면_갱신된다() {
        scoreService.submit("user1", 100L);
        scoreService.submit("user1", 200L);

        Score result = scoreRepository.findById("user1").get();
        assertThat(result.getScore()).isEqualTo(200L);
    }

    @Test
    void 더_낮은_점수를_제출하면_유지된다() {
        scoreService.submit("user1", 200L);
        scoreService.submit("user1", 100L);

        Score result = scoreRepository.findById("user1").get();
        assertThat(result.getScore()).isEqualTo(200L);
    }

    @Test
    void 랭킹은_점수_높은_순이다() {
        scoreService.submit("user1", 100L);
        scoreService.submit("user2", 300L);
        scoreService.submit("user3", 200L);

        List<Score> rankings = scoreService.getRankings(10);

        assertThat(rankings).extracting(Score::getUserId)
                .containsExactly("user2", "user3", "user1");
    }

    @Test
    void 동점이면_먼저_달성한_사람이_위다() {
        // 서비스는 시각을 now()로 정하므로, 시각을 정할 수 있게 저장소에 직접 넣는다
        LocalDateTime ten = LocalDateTime.of(2026, 11, 5, 10, 0);
        LocalDateTime eleven = LocalDateTime.of(2026, 11, 5, 11, 0);
        scoreRepository.save(new Score("late", 100L, eleven));
        scoreRepository.save(new Score("early", 100L, ten));

        List<Score> rankings = scoreService.getRankings(10);

        assertThat(rankings).extracting(Score::getUserId)
                .containsExactly("early", "late");
    }

    @Test
    void limit_만큼만_반환한다() {
        scoreService.submit("user1", 100L);
        scoreService.submit("user2", 200L);
        scoreService.submit("user3", 300L);

        assertThat(scoreService.getRankings(2)).hasSize(2);
        assertThat(scoreService.getRankings(100)).hasSize(3);
    }
}
