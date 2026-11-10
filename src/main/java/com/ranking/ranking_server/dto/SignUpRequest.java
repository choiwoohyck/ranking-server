package com.ranking.ranking_server.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @NotBlank String userId,
        // 최소 8자: 너무 짧은 비밀번호 방지. 최대 72자: BCrypt는 72바이트 이후를 무시한다.
        @NotBlank @Size(min = 8, max = 72) String password) {
}
