package com.example.securityaop.aspect;

import com.example.securityaop.service.SecurityLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Autowired private SecurityLogService securityLogService;

    // Simple AOP hook: log entry to any admin controller method
    @Before("execution(* com.example.securityaop.controller.AdminController.*(..))")
    public void beforeAdmin(JoinPoint jp) {
        String method = jp.getSignature().getName();
        securityLogService.addLog("system", "Called AdminController." + method, "INFO");
    }

    // Also catch delete operations for higher severity
    @AfterReturning("execution(* com.example.securityaop.controller.AdminController.deleteUser(..))")
    public void afterDelete(JoinPoint jp) {
        securityLogService.addLog("system", "deleteUser executed", "WARN");
    }
}
