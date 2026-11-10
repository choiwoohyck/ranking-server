package com.ranking.ranking_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "아이디", example = "otter1")
        @NotBlank String userId,
        @Schema(description = "비밀번호", example = "password123")
        @NotBlank String password) {
}
