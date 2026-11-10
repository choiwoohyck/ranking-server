package com.ranking.ranking_server.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Swagger(OpenAPI) 문서 전체 설정.
@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Ranking Server API",
        version = "v1",
        description = "게임 점수 제출과 실시간 랭킹 조회 API. 점수 제출은 로그인 후 받은 JWT가 필요합니다."
))
// OpenAPI 표준은 이름이 Authorization인 헤더 파라미터를 무시한다(Swagger UI가 보내지 않음).
@SecurityScheme(
        name = "bearerAuth",              // 컨트롤러의 @SecurityRequirement(name = "bearerAuth")가 이 이름을 가리킴
        type = SecuritySchemeType.HTTP,    // HTTP Authorization 헤더 방식
        scheme = "bearer",                 // "Authorization: Bearer <토큰>"
        bearerFormat = "JWT"               // 화면 표시용
)
public class OpenApiConfig {
}
