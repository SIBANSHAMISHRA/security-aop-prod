package com.example.securityaop.controller;

import com.example.securityaop.annotation.Authenticated;
import com.example.securityaop.dto.UserDto;
import com.example.securityaop.model.User;
import com.example.securityaop.security.SecurityContext;
import com.example.securityaop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    @Autowired private UserService userService;

    @Authenticated
    @GetMapping("/me")
    public ResponseEntity<?> getProfile() {
        String email = SecurityContext.getCurrentUserEmail();
        if (email == null)
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Optional<User> opt = userService.getUserByEmail(email);
        if (opt.isEmpty())
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        User u = opt.get();
        return ResponseEntity.ok(new UserDto(u.getEmail(), u.getRole(), u.isActive(), u.getName()));
    }

    @Authenticated
    @GetMapping("/logs")
    public ResponseEntity<?> getUserLogs() {
        return ResponseEntity.ok(List.of(
                Map.of("timestamp", System.currentTimeMillis(), "action", "Logged in", "type", "INFO"),
                Map.of("timestamp", System.currentTimeMillis(), "action", "Viewed dashboard", "type", "INFO")
        ));
    }

    @Authenticated
    @PostMapping("/updateName")
    public ResponseEntity<?> updateName(@RequestParam String name) {
        String email = SecurityContext.getCurrentUserEmail();
        if (email == null)
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        boolean updated = userService.updateUserName(email, name);
        return ResponseEntity.ok(Map.of("message", updated ? "Name updated successfully" : "Failed to update name"));
    }

    @Authenticated
    @PostMapping("/report")
    public ResponseEntity<?> report(@RequestParam String message) {
        String email = SecurityContext.getCurrentUserEmail();
        return ResponseEntity.ok(Map.of("message", "Feedback received from " + email + ": " + message));
    }
}
