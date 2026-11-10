package com.ranking.ranking_server.exception;

import com.ranking.ranking_server.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.stream.Collectors;

// 모든 컨트롤러에서 올라온 예외를 여기서 받아, 상태 코드 + ErrorResponse(JSON)로 바꾼다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 409: 이미 있는 아이디로 가입
    @ExceptionHandler(DuplicateUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(DuplicateUserException e) {
        return new ErrorResponse("DUPLICATE_USER", e.getMessage());
    }

    // 401: 로그인 실패, 위조·만료 토큰, Bearer 형식 오류
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUnauthorized(AuthenticationException e) {
        return new ErrorResponse("UNAUTHORIZED", e.getMessage());
    }

    // 401: Authorization 헤더 자체가 없음 → "인증 정보가 없다"이므로 400이 아니라 401
    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleMissingHeader(MissingRequestHeaderException e) {
        return new ErrorResponse("UNAUTHORIZED", e.getHeaderName() + " 헤더가 필요합니다.");
    }

    // 400: @Valid @RequestBody 검증 실패 (예: password 8자 미만, score 음수)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidBody(MethodArgumentNotValidException e) {
        // 틀린 필드마다 "필드명: 이유"를 만들어 한 줄로 합친다.
        // 예: "password: 크기가 8에서 72 사이여야 합니다, userId: 공백일 수 없습니다"
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse("INVALID_REQUEST", message);
    }

    // 400: @RequestParam 검증 실패 (예: /rankings?limit=0)
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidParam(HandlerMethodValidationException e) {
        String message = e.getParameterValidationResults().stream()
                .map(result -> result.getMethodParameter().getParameterName() + ": "
                        + result.getResolvableErrors().get(0).getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ErrorResponse("INVALID_REQUEST", message);
    }
}
