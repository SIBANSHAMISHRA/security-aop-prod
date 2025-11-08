package com.example.securityaop.controller;

import com.example.securityaop.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AdminControllerTest {

    @Autowired
    private UserService userService;

    @Test
    void getStatsShouldReturnNonEmptyResults() {
        var stats = userService.getStats();
        Assertions.assertNotNull(stats);
        Assertions.assertTrue(stats.get("totalUsers") >= 2);
        System.out.println("✅ Admin stats: " + stats);
    }
}
