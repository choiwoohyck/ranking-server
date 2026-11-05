package com.ranking.ranking_server;

import com.ranking.ranking_server.repository.MemoryScoreRepository;
import com.ranking.ranking_server.repository.ScoreRepository;
import com.ranking.ranking_server.service.ScoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public ScoreService scoreService() {
        return new ScoreService(scoreRepository());
    }

    @Bean
    public ScoreRepository scoreRepository() {
        return new MemoryScoreRepository();
    }
}
