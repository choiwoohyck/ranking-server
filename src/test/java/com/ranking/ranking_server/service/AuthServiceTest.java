package com.ranking.ranking_server.service;

import com.ranking.ranking_server.repository.MemoryUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

    MemoryUserRepository userRepository;
    AuthService authService;
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void beforeEach() {
        userRepository = new MemoryUserRepository();
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository,bCryptPasswordEncoder);
    }

    @AfterEach
    public void afterEach() {
        userRepository.clearStore();
    }

    @Test
    void 가입하면_저장된다() {
        authService.signUp("otter1", "password123");

        assertThat(userRepository.findById("otter1")).isPresent();
    }

    @Test
    void 같은_아이디로_가입하면_예외() {
        authService.signUp("otter1", "password123");

        assertThatThrownBy(() -> authService.signUp("otter1", "another123"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 비밀번호는_해시로_저장된다(){
        authService.signUp("otter1","password123");

        assertThat(userRepository.findById("otter1").get().getPasswordHash()).isNotEqualTo("password123");
    }
}
