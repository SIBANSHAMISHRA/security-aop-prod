package com.example.securityaop.dto;

import java.time.LocalDateTime;

/**
 * DTO representing a single security log event.
 * Used to store audit information like who performed what action.
 */
public class SecurityLogDto {

    private String email;
    private String action;
    private String type;
    private LocalDateTime timestamp;

    public SecurityLogDto() {}

    public SecurityLogDto(String email, String action, String type, LocalDateTime timestamp) {
        this.email = email;
        this.action = action;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
