package com.ranking.ranking_server.service;

import com.ranking.ranking_server.repository.MemoryUserRepository;
import com.ranking.ranking_server.security.JwtTokenProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

class AuthServiceTest {

    MemoryUserRepository userRepository;
    AuthService authService;
    BCryptPasswordEncoder bCryptPasswordEncoder;
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void beforeEach() {
        userRepository = new MemoryUserRepository();
        bCryptPasswordEncoder = new BCryptPasswordEncoder();
        jwtTokenProvider = new JwtTokenProvider("test-secret-test-secret-test-secret!!", 3600);
        authService = new AuthService(userRepository, bCryptPasswordEncoder, jwtTokenProvider);
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
    void 비밀번호는_해시로_저장된다() {
        authService.signUp("otter1", "password123");

        String hash = userRepository.findById("otter1").get().getPasswordHash();
        assertThat(hash).isNotEqualTo("password123");               // 평문 그대로가 아니고
        assertThat(bCryptPasswordEncoder.matches("password123", hash)).isTrue(); // 원래 비밀번호와 맞는다
    }

    @Test
    void 로그인하면_토큰이_나온다() {
        authService.signUp("otter1", "password123");

        String token = authService.login("otter1", "password123");

        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void 비밀번호가_틀리면_로그인_예외() {
        authService.signUp("otter1", "password123");

        assertThatThrownBy(() -> authService.login("otter1", "wrong-password"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 없는_아이디면_로그인_예외() {
        assertThatThrownBy(() -> authService.login("nobody", "password123"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 아이디가_없을_때와_비밀번호가_틀릴_때_메시지가_같다() {
        authService.signUp("otter1", "password123");

        Throwable noUser = catchThrowable(
                () -> authService.login("nobody", "password123"));
        Throwable wrongPassword = catchThrowable(
                () -> authService.login("otter1", "wrong-password"));

        assertThat(noUser.getMessage()).isEqualTo(wrongPassword.getMessage());
    }
}
