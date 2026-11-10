package com.ranking.ranking_server.security;

import com.ranking.ranking_server.exception.AuthenticationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtTokenProvider(String secretKey, long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(String userId){
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();
    }

    // jjwt 예외는 이 클래스 밖으로 내보내지 않고 우리 예외(AuthenticationException)로 바꾼다.
    // → jjwt를 아는 곳은 이 파일 하나뿐이라, 라이브러리를 바꿔도 여기만 고치면 된다.
    public String getUserId(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new AuthenticationException("만료된 토큰입니다. 다시 로그인하세요.");
        } catch (JwtException | IllegalArgumentException e) {
            // JwtException: 서명 불일치, 형식 오류 등 / IllegalArgumentException: 빈 토큰
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }
    }
}
