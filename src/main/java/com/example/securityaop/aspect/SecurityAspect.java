package com.example.securityaop.aspect;

import com.example.securityaop.annotation.AdminOnly;
import com.example.securityaop.security.SecurityContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityAspect {

    @Before("@annotation(adminOnly)")
    public void checkAdminAccess(AdminOnly adminOnly) {
        // TEMPORARY: bypass admin check for local testing
        // Do nothing here to allow access to dashboard and security logs
    }

    // If you want to enable role check later, uncomment this:
    /*
    @Before("@annotation(adminOnly)")
    public void checkAdminAccess(AdminOnly adminOnly) {
        String currentRole = SecurityContext.getCurrentUserRole();
        if (!"ADMIN".equals(currentRole)) {
            throw new RuntimeException("Access denied: Admins only");
        }
    }
    */
}
