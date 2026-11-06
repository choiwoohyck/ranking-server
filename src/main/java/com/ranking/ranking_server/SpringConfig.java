package com.ranking.ranking_server;

import com.ranking.ranking_server.repository.ScoreRepository;
import com.ranking.ranking_server.repository.UserRepository;
import com.ranking.ranking_server.service.AuthService;
import com.ranking.ranking_server.service.ScoreService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    @Bean
    public ScoreService scoreService() {
        return new ScoreService(scoreRepository);
    }

    @Bean
    public AuthService authService() {
        return new AuthService(userRepository);
    }

    public SpringConfig(ScoreRepository scoreRepository, UserRepository userRepository) {
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }
}
