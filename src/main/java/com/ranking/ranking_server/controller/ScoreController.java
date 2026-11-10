package com.ranking.ranking_server.controller;

import com.ranking.ranking_server.domain.Score;
import com.ranking.ranking_server.dto.RankingResponse;
import com.ranking.ranking_server.dto.ScoreSubmitRequest;
import com.ranking.ranking_server.exception.AuthenticationException;
import com.ranking.ranking_server.security.JwtTokenProvider;
import com.ranking.ranking_server.service.ScoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import com.ranking.ranking_server.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "점수·랭킹", description = "점수 제출(로그인 필요), 랭킹 조회")
@RestController
public class ScoreController {
    private static final String BEARER_PREFIX = "Bearer ";

    private final ScoreService scoreService;
    private final JwtTokenProvider jwtTokenProvider;

    public ScoreController(ScoreService scoreService, JwtTokenProvider jwtTokenProvider) {
        this.scoreService = scoreService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(summary = "점수 제출",
            description = "토큰 주인의 점수로 저장됩니다. 기존 최고 기록보다 높을 때만 갱신됩니다. "
                    + "🔒 Authorize에 로그인 토큰을 먼저 넣으세요.")
    @SecurityRequirement(name = "bearerAuth")   // OpenApiConfig의 bearerAuth 적용 → Swagger가 헤더를 붙여 보냄
    @ApiResponse(responseCode = "200", description = "제출 완료 (갱신 여부와 관계없이 200)")
    @ApiResponse(responseCode = "400", description = "점수가 음수",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "토큰 없음, 형식 오류, 위조 또는 만료",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/scores")
    public void submit(@Parameter(hidden = true) @RequestHeader("Authorization") String authorization,
                       @Valid @RequestBody ScoreSubmitRequest request) {
        String userId = jwtTokenProvider.getUserId(extractToken(authorization));
        scoreService.submit(userId, request.score());
    }

    // "Bearer eyJ..." → "eyJ..."
    private String extractToken(String authorization) {
        if (!authorization.startsWith(BEARER_PREFIX)) {
            throw new AuthenticationException("Authorization 헤더는 'Bearer 토큰' 형식이어야 합니다.");
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    @Operation(summary = "랭킹 조회",
            description = "점수 높은 순, 같으면 먼저 달성한 순. 로그인 없이 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "limit이 1~100 범위를 벗어남",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/rankings")
    public List<RankingResponse> rankings(
            @Parameter(description = "가져올 순위 수 (1~100)", example = "10")
            @RequestParam(defaultValue = "100") @Min(1)@Max(100) int limit) {
        List<Score> scores = scoreService.getRankings(limit);
        List<RankingResponse> result = new ArrayList<>();
        for (int i = 0; i < scores.size(); i++) {
            Score s = scores.get(i);
            result.add(new RankingResponse(i + 1, s.getUserId(), s.getScore(), s.getAchievedAt()));
        }
        return result;
    }
}
