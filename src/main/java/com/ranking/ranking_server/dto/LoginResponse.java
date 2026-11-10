package com.ranking.ranking_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(
        @Schema(description = "JWT 액세스 토큰. 🔒 Authorize에 넣어 사용") String token) {
}
