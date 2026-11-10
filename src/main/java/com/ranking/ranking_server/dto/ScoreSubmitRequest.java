package com.ranking.ranking_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record ScoreSubmitRequest(
        @Schema(description = "점수 (0 이상, 누적 획득 골드)", example = "12500")
        @Min(0) long score) {
}
