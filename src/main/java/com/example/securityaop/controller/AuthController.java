package com.example.securityaop.controller;

import com.example.securityaop.model.LoginRequest;
import com.example.securityaop.model.User;
import com.example.securityaop.model.VerificationToken;
import com.example.securityaop.repository.VerificationTokenRepository;
import com.example.securityaop.security.JwtUtil;
import com.example.securityaop.service.EmailService;
import com.example.securityaop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private VerificationTokenRepository tokenRepo;
    @Autowired private EmailService emailService;

    @Value("${app.base-url:http://localhost:5173}")
    private String baseUrl;

    // ✅ Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            if (req == null || req.getEmail() == null || req.getPassword() == null)
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Missing email or password"));

            String email = req.getEmail().trim().toLowerCase();
            String password = req.getPassword().trim();

            System.out.println("🟢 Login attempt for: " + email);

            if (!userService.checkPassword(email, password))
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid credentials"));

            Optional<User> u = userService.getUserByEmail(email);
            if (u.isPresent() && !u.get().isActive())
                return ResponseEntity.status(403).body(Map.of("status", "error", "message", "Please verify your email."));

            String role = userService.getRoles(email);
            String token = jwtUtil.generateToken(email, role);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login successful",
                    "email", email,
                    "role", role,
                    "token", token
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // ✅ Token validation
    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String header) {
        try {
            if (header == null || !header.startsWith("Bearer "))
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Missing token"));

            String token = header.substring(7);
            if (!jwtUtil.validateToken(token))
                return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Invalid or expired token"));

            return ResponseEntity.ok(Map.of(
                    "email", jwtUtil.extractEmail(token),
                    "role", jwtUtil.extractRole(token)
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // ✅ Signup
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> req) {
        try {
            String email = req.get("email");
            String password = req.get("password");

            if (email == null || password == null)
                return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Missing fields"));

            email = email.trim().toLowerCase();

            if (userService.getUserByEmail(email).isPresent())
                return ResponseEntity.status(409).body(Map.of("status", "error", "message", "User already exists"));

            userService.createPendingUser(email, password);
            VerificationToken token = new VerificationToken(email);
            tokenRepo.save(token);

            String verifyLink = baseUrl + "/api/auth/verifyEmail?token=" + token.getToken();
            emailService.sendSimple(email, "Verify your Security AOP account",
                    "Hello,\n\nPlease verify your account by clicking this link:\n" + verifyLink + "\n\nIf you didn’t sign up, ignore this email.");

            return ResponseEntity.ok(Map.of("status", "success", "message", "Verification email sent to " + email));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // ✅ Email Verification
    @GetMapping("/verifyEmail")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            Optional<VerificationToken> opt = tokenRepo.findByTokenAndUsedFalse(token);
            if (opt.isEmpty())
                return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Invalid or expired token"));

            VerificationToken vt = opt.get();
            userService.activatePendingUser(vt.getEmail());
            vt.setUsed(true);
            tokenRepo.save(vt);

            return ResponseEntity.ok(Map.of("status", "success", "message", "✅ Email verified successfully. You can now log in."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}
