package com.example.securityaop;

import com.example.securityaop.model.User;
import com.example.securityaop.repository.UserRepository;
import com.example.securityaop.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Ensure test uses the same transaction and the defaults are visible
    @Test
    @Transactional
    void validLoginShouldReturnTrue() {
        // Ensure defaults exist
        Assertions.assertTrue(userRepository.findById("user@example.com").isPresent(),
                "Default user should exist in DB");
        boolean result = userService.checkPassword("user@example.com", "userpass");
        Assertions.assertTrue(result, "✅ Expected valid login to return true");
    }

    @Test
    @Transactional
    void invalidLoginShouldReturnFalse() {
        boolean result = userService.checkPassword("fake@user.com", "wrong");
        Assertions.assertFalse(result, "✅ Expected invalid login to return false");
    }

    @Test
    @Transactional
    void userActivationAndStatsShouldWork() {
        userService.createPendingUser("tempuser@example.com", "password123");
        userService.activatePendingUser("tempuser@example.com");
        var stats = userService.getStats();

        System.out.println("✅ Stats: " + stats);
        Assertions.assertTrue(stats.get("totalUsers") >= 2, "Should have at least 2 users");
    }
}
