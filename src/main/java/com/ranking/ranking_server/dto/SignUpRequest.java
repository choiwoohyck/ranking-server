package com.ranking.ranking_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Schema(description = "아이디", example = "otter1")
        @NotBlank String userId,
        // 최소 8자: 너무 짧은 비밀번호 방지. 최대 72자: BCrypt는 72바이트 이후를 무시한다.
        @Schema(description = "비밀번호 (8~72자)", example = "password123")
        @NotBlank @Size(min = 8, max = 72) String password) {
}
