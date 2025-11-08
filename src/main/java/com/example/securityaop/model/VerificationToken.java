package com.example.securityaop.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    private String token;
    private String email;
    private Instant createdAt;
    private boolean used;

    public VerificationToken() {}

    public VerificationToken(String email) {
        this.token = UUID.randomUUID().toString();
        this.email = email;
        this.createdAt = Instant.now();
        this.used = false;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
