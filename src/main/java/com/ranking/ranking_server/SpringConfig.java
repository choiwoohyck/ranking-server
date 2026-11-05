package com.ranking.ranking_server;

import com.ranking.ranking_server.repository.ScoreRepository;
import com.ranking.ranking_server.service.ScoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final ScoreRepository scoreRepository;

    @Bean
    public ScoreService scoreService() {
        return new ScoreService(scoreRepository);
    }

    public SpringConfig(ScoreRepository scoreRepository) {
        this.scoreRepository = scoreRepository;
    }
}
