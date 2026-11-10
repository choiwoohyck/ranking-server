package com.ranking.ranking_server.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ErrorResponse(
        @Schema(description = "클라이언트가 분기에 쓰는 에러 코드", example = "UNAUTHORIZED") String code,
        @Schema(description = "사람이 읽는 설명", example = "아이디 또는 비밀번호가 올바르지 않습니다.") String message) {
}
