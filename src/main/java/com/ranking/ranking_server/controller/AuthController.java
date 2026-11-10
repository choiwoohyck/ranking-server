package com.ranking.ranking_server.controller;

import com.ranking.ranking_server.dto.LoginRequest;
import com.ranking.ranking_server.dto.LoginResponse;
import com.ranking.ranking_server.dto.SignUpRequest;
import com.ranking.ranking_server.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){this.authService = authService;}

    @PostMapping("/auth/signup")
    public void signup(@Valid @RequestBody SignUpRequest request)
    {
        authService.signUp(request.userId(),request.password());
    }

    @PostMapping("/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request)
    {
        LoginResponse response = new LoginResponse(authService.login(request.userId(),request.password()));
        return response;
    }


}
