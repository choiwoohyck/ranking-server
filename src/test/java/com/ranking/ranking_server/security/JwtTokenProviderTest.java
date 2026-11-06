package com.ranking.ranking_server.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenProviderTest {
    JwtTokenProvider jwtTokenProvider = new JwtTokenProvider("test-secret-test-secret-test-secret!!", 3600);

    @Test
    public void 키_생성_테스트()
    {
        String token = jwtTokenProvider.createToken("otter1");
        System.out.println(token);

        // JWT는 헤더.내용.서명 세 부분으로 나뉜다
        assertThat(token.split("\\.")).hasSize(3);
    }
}
