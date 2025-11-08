package com.example.securityaop.dto;

public class UserDto {
    private String email;
    private String role;
    private boolean active;
    private String name;

    public UserDto() {}

    public UserDto(String email, String role, boolean active, String name) {
        this.email = email;
        this.role = role;
        this.active = active;
        this.name = name;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
