package com.example.securityaop.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "security_logs")
public class SecurityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // who performed the action (may be null for unauthenticated attempts)
    private String email;

    // a short action description: "LOGIN_SUCCESS", "LOGIN_FAIL", "ACCESS_PROTECTED", "ADMIN_DELETE_USER", etc
    private String action;

    // optional details or metadata (e.g. target user, endpoint, message)
    @Column(columnDefinition = "TEXT")
    private String details;

    // INFO, ERROR, ADMIN_ACTION...
    private String type = "INFO";

    // timestamp
    private Instant timestamp = Instant.now();

    public SecurityLog() {}

    public SecurityLog(String email, String action, String details, String type) {
        this.email = email;
        this.action = action;
        this.details = details;
        this.type = type;
        this.timestamp = Instant.now();
    }

    // getters & setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public String getType() { return type; }
    public Instant getTimestamp() { return timestamp; }

    public void setEmail(String email) { this.email = email; }
    public void setAction(String action) { this.action = action; }
    public void setDetails(String details) { this.details = details; }
    public void setType(String type) { this.type = type; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
