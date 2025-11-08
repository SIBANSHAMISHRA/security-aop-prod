package com.example.securityaop.security;

public class SecurityContext {
    private static final ThreadLocal<String> currentToken = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUserEmail = new ThreadLocal<>();

    public static void setCurrentToken(String token) {
        currentToken.set(token);
    }

    public static String getCurrentToken() {
        return currentToken.get();
    }

    public static void setCurrentUserEmail(String email) {
        currentUserEmail.set(email);
    }

    public static String getCurrentUserEmail() {
        return currentUserEmail.get();
    }

    public static void clear() {
        currentToken.remove();
        currentUserEmail.remove();
    }
}
