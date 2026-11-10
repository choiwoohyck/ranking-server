package com.ranking.ranking_server.dto;

public record ErrorResponse(String code, String message) {}
// 예: {"code": "DUPLICATE_USER", "message": "이미 사용 중인 아이디입니다."}