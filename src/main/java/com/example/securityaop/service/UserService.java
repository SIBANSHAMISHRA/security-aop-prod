package com.example.securityaop.service;

import com.example.securityaop.dto.UserDto;
import com.example.securityaop.model.User;
import com.example.securityaop.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ UserService — Core business logic for authentication, signup, user actions, and admin management
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // -------------------------------------------------------------
    // 🌟 INITIAL SETUP: Default users on startup
    // -------------------------------------------------------------
    @PostConstruct
    public void initDefaults() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            userRepository.save(new User(
                    "admin@example.com",
                    "Admin User",
                    "ADMIN",
                    encoder.encode("adminpass"),
                    true
            ));
            System.out.println("✅ Default admin created (admin@example.com / adminpass)");
        }

        if (!userRepository.existsByEmail("user@example.com")) {
            userRepository.save(new User(
                    "user@example.com",
                    "Normal User",
                    "USER",
                    encoder.encode("userpass"),
                    true
            ));
            System.out.println("✅ Default user created (user@example.com / userpass)");
        }
    }

    // -------------------------------------------------------------
    // 🔐 AUTHENTICATION METHODS
    // -------------------------------------------------------------

    /** ✅ Verify login credentials */
    public boolean checkPassword(String email, String password) {
        if (email == null || password == null) return false;

        return userRepository.findByEmail(email.toLowerCase())
                .filter(User::isActive)
                .map(user -> encoder.matches(password, user.getPassword()))
                .orElse(false);
    }

    /** ✅ Get user role for a given email */
    public String getRoles(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .map(User::getRole)
                .orElse("USER");
    }

    // -------------------------------------------------------------
    // 👤 USER OPERATIONS
    // -------------------------------------------------------------

    /** ✅ Fetch user by email */
    public Optional<User> getUserByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        return userRepository.findByEmail(email.toLowerCase());
    }

    /** ✅ Update user's name */
    public boolean updateUserName(String email, String newName) {
        if (email == null || newName == null || newName.isBlank()) return false;
        Optional<User> opt = getUserByEmail(email);
        if (opt.isEmpty()) return false;

        User user = opt.get();
        user.setName(newName);
        userRepository.save(user);
        return true;
    }

    /** ✅ Update user's password (securely) */
    public boolean updatePassword(String email, String newPassword) {
        if (email == null || newPassword == null || newPassword.isBlank()) return false;

        Optional<User> opt = getUserByEmail(email);
        if (opt.isEmpty()) return false;

        User user = opt.get();
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    /** ✅ Fetch all users for admin dashboard (simplified DTO) */
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserDto(u.getEmail(), u.getRole(), u.isActive(), u.getName()))
                .collect(Collectors.toList());
    }

    /** ✅ Fetch all full user objects (for Excel export) */
    public List<User> getAllUsersFull() {
        return userRepository.findAll();
    }

    // -------------------------------------------------------------
    // 📨 SIGNUP + EMAIL VERIFICATION SUPPORT
    // -------------------------------------------------------------

    /** ✅ Create a pending (inactive) user account for signup */
    public boolean createPendingUser(String email, String rawPassword) {
        if (email == null || rawPassword == null || email.isBlank() || rawPassword.isBlank()) return false;
        email = email.toLowerCase();

        if (userRepository.existsByEmail(email)) return false;

        User user = new User(
                email,
                "",              // name left empty until user updates
                "USER",          // default role
                encoder.encode(rawPassword),
                false            // inactive until verified
        );
        userRepository.save(user);
        System.out.println("🟡 Created pending user: " + email);
        return true;
    }

    /** ✅ Activate user after email verification */
    public boolean activatePendingUser(String email) {
        if (email == null || email.isBlank()) return false;
        Optional<User> opt = userRepository.findByEmail(email.toLowerCase());
        if (opt.isEmpty()) return false;

        User user = opt.get();
        user.setActive(true);
        userRepository.save(user);
        System.out.println("🟢 User activated: " + email);
        return true;
    }

    // -------------------------------------------------------------
    // 🛠️ ADMIN UTILITIES
    // -------------------------------------------------------------

    /** ✅ Delete user by email */
    public boolean deleteByEmail(String email) {
        if (email == null || email.isBlank()) return false;
        if (!userRepository.existsByEmail(email.toLowerCase())) return false;

        userRepository.deleteById(email.toLowerCase());
        return true;
    }

    /** ✅ Change a user's role (USER → ADMIN / ADMIN → USER) */
    public boolean updateUserRole(String email, String role) {
        if (email == null || role == null || email.isBlank() || role.isBlank()) return false;

        Optional<User> opt = getUserByEmail(email);
        if (opt.isEmpty()) return false;

        User user = opt.get();
        user.setRole(role.toUpperCase());
        userRepository.save(user);
        return true;
    }

    /** ✅ Enable or disable a user account */
    public boolean setUserActive(String email, boolean active) {
        if (email == null || email.isBlank()) return false;

        Optional<User> opt = getUserByEmail(email);
        if (opt.isEmpty()) return false;

        User user = opt.get();
        user.setActive(active);
        userRepository.save(user);
        return true;
    }

    // -------------------------------------------------------------
    // 📊 DASHBOARD STATS
    // -------------------------------------------------------------

    /** ✅ Compute system-wide statistics for admin analytics */
    public Map<String, Integer> getStats() {
        List<User> all = userRepository.findAll();
        int total = all.size();
        int admins = (int) all.stream().filter(u -> "ADMIN".equalsIgnoreCase(u.getRole())).count();
        int active = (int) all.stream().filter(User::isActive).count();
        int inactive = total - active;

        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalUsers", total);
        stats.put("admins", admins);
        stats.put("activeUsers", active);
        stats.put("inactiveUsers", inactive);
        return stats;
    }
}
