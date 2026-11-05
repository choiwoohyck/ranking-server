package com.ranking.ranking_server.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ScoreSubmitRequest(@NotBlank String userId,@Min(0) long score) {
}
