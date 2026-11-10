package com.ranking.ranking_server.controller;

import com.ranking.ranking_server.dto.ErrorResponse;
import com.ranking.ranking_server.dto.LoginRequest;
import com.ranking.ranking_server.dto.LoginResponse;
import com.ranking.ranking_server.dto.SignUpRequest;
import com.ranking.ranking_server.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "회원가입, 로그인(JWT 발급)")
@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){this.authService = authService;}

    @Operation(summary = "회원가입", description = "비밀번호는 8~72자이며 BCrypt 해시로 저장됩니다.")
    @ApiResponse(responseCode = "200", description = "가입 성공")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패 (빈 아이디, 비밀번호 길이)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "이미 사용 중인 아이디",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/auth/signup")
    public void signup(@Valid @RequestBody SignUpRequest request)
    {
        authService.signUp(request.userId(),request.password());
    }

    @Operation(summary = "로그인", description = "성공하면 JWT를 반환합니다. 화면 오른쪽 위 🔒 Authorize에 이 토큰을 넣으세요.")
    @ApiResponse(responseCode = "200", description = "로그인 성공, 토큰 반환")
    @ApiResponse(responseCode = "400", description = "입력값 검증 실패",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 틀림 (어느 쪽인지 구분하지 않음)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request)
    {
        LoginResponse response = new LoginResponse(authService.login(request.userId(),request.password()));
        return response;
    }


}
