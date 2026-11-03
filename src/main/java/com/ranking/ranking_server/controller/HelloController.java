package com.ranking.ranking_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("ping")
    public String Ping() {
        return "pong";
    }

}
