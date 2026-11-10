package com.ranking.ranking_server.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtTokenProviderTest {
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("test-secret-test-secret-test-secret!!", 3600);

    @Test
    public void 키_생성_테스트()
    {
        String token = jwtTokenProvider.createToken("otter1");

        // JWT는 헤더.내용.서명 세 부분으로 나뉜다
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void 토큰에서_userId를_꺼낸다() {
        String token = jwtTokenProvider.createToken("otter1");

        assertThat(jwtTokenProvider.getUserId(token)).isEqualTo("otter1");
    }

    @Test
    void 다른_키로_만든_토큰은_거부된다() {
        JwtTokenProvider otherServer = new JwtTokenProvider("other-secret-other-secret-other-secret!!", 3600);
        String forged = otherServer.createToken("otter1");

        assertThatThrownBy(() -> jwtTokenProvider.getUserId(forged))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void 만료된_토큰은_거부된다() {
        JwtTokenProvider expiredProvider = new JwtTokenProvider("test-secret-test-secret-test-secret!!", -1);
        String expired = expiredProvider.createToken("otter1");

        assertThatThrownBy(() -> jwtTokenProvider.getUserId(expired))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void 내용을_조작한_토큰은_거부된다() {
        String token = jwtTokenProvider.createToken("otter1");
        String[] parts = token.split("\\.");
        // 가운데(내용)를 다른 유저의 토큰 내용으로 바꿔치기 → 서명과 안 맞음
        String otherPayload = jwtTokenProvider.createToken("hacker").split("\\.")[1];
        String tampered = parts[0] + "." + otherPayload + "." + parts[2];

        assertThatThrownBy(() -> jwtTokenProvider.getUserId(tampered))
                .isInstanceOf(JwtException.class);
    }
}
