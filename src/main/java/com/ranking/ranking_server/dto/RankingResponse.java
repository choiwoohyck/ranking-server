package com.ranking.ranking_server.dto;

import java.time.LocalDateTime;

public record RankingResponse(int rank, String userId, long score, LocalDateTime achievedAt) {
}