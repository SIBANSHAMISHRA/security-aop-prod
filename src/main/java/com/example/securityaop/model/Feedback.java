package com.example.securityaop.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "feedbacks")
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String message;
    private Instant timestamp = Instant.now();

    public Feedback() {}
    public Feedback(String email, String message) {
        this.email = email;
        this.message = message;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
    public Instant getTimestamp() { return timestamp; }

    // Setters
    public void setEmail(String email) { this.email = email; }
    public void setMessage(String message) { this.message = message; }
}
