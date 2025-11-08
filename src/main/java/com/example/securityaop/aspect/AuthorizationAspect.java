package com.example.securityaop.aspect;

import com.example.securityaop.annotation.AdminOnly;
import com.example.securityaop.annotation.Authenticated;
import com.example.securityaop.security.JwtUtil;
import com.example.securityaop.security.SecurityContext;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
public class AuthorizationAspect {

    @Autowired private JwtUtil jwtUtil;

    @Around("@annotation(authenticated)")
    public Object handleAuthenticated(ProceedingJoinPoint pjp, Authenticated authenticated) throws Throwable {
        return authorizeAndProceed(pjp, false);
    }

    @Around("@annotation(adminOnly)")
    public Object handleAdminOnly(ProceedingJoinPoint pjp, AdminOnly adminOnly) throws Throwable {
        return authorizeAndProceed(pjp, true);
    }

    private Object authorizeAndProceed(ProceedingJoinPoint pjp, boolean adminOnly) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return pjp.proceed();

        HttpServletRequest req = attrs.getRequest();
        String authHeader = req.getHeader("Authorization");
        System.out.println("🔐 AOP triggered for: " + pjp.getSignature().getName());

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header");
        }

        String token = authHeader.substring(7);
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }

            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);

            SecurityContext.setCurrentUserEmail(email);

            if (adminOnly && !"ADMIN".equalsIgnoreCase(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin access required");
            }

            return pjp.proceed();
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token validation failed");
        } finally {
            SecurityContext.clear();
        }
    }
}
