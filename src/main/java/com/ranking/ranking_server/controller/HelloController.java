package com.ranking.ranking_server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상태 확인")
@RestController
public class HelloController {

    @Operation(summary = "서버 동작 확인", description = "서버가 살아 있으면 pong을 반환합니다.")
    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

}
