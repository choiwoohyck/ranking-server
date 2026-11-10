package com.ranking.ranking_server.dto;

import jakarta.validation.constraints.Min;

public record ScoreSubmitRequest(@Min(0) long score) {
}
