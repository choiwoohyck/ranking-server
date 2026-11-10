package com.ranking.ranking_server;

import com.ranking.ranking_server.repository.ScoreRepository;
import com.ranking.ranking_server.repository.UserRepository;
import com.ranking.ranking_server.security.JwtTokenProvider;
import com.ranking.ranking_server.service.AuthService;
import com.ranking.ranking_server.service.ScoreService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringConfig {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;

    @Bean
    public ScoreService scoreService() {
        return new ScoreService(scoreRepository);
    }

    @Bean
    public AuthService authService(JwtTokenProvider jwtTokenProvider) {
        return new AuthService(userRepository, bCryptPasswordEncoder(), jwtTokenProvider);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        return new JwtTokenProvider(secretKey, expirationSeconds);
    }

    public SpringConfig(ScoreRepository scoreRepository, UserRepository userRepository) {
        this.scoreRepository = scoreRepository;
        this.userRepository = userRepository;
    }
}
