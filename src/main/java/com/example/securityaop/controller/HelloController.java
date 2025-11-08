package com.example.securityaop.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String home() {
        return "Welcome — Security AOP demo";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello! AOP logging should appear in console.";
    }
}
