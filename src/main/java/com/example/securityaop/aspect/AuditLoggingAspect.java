package com.example.securityaop.aspect;

import com.example.securityaop.model.SecurityLog;
import com.example.securityaop.repository.SecurityLogRepository;
import com.example.securityaop.security.SecurityContext;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

@Aspect
@Component
public class AuditLoggingAspect {

    @Autowired
    private SecurityLogRepository logRepo;

    // Pointcut: any method annotated with our security annotations
    @Around("@annotation(com.example.securityaop.annotation.AdminOnly) || @annotation(com.example.securityaop.annotation.Authenticated)")
    public Object logAnnotatedMethods(ProceedingJoinPoint pjp) throws Throwable {
        return logAround(pjp, "ACCESS_PROTECTED", "INFO");
    }

    // Pointcut: login method — we want to log login attempts and result
    @Around("execution(* com.example.securityaop.controller.AuthController.login(..))")
    public Object logLogin(ProceedingJoinPoint pjp) throws Throwable {
        // before: extract incoming email (if any) from args
        Object[] args = pjp.getArgs();
        String emailArg = "unknown";
        try {
            // expecting LoginRequest as first arg
            if (args != null && args.length > 0 && args[0] != null) {
                Object loginReq = args[0];
                // using reflection to get getEmail() if present
                try {
                    emailArg = (String) loginReq.getClass().getMethod("getEmail").invoke(loginReq);
                } catch (Exception ignored) {}
            }
        } catch (Exception ignore) {}

        // try proceed and capture result / exception
        try {
            Object result = pjp.proceed();
            // successful login endpoints typically return ResponseEntity map with status
            logRepo.save(new SecurityLog(emailArg, "LOGIN_ATTEMPT", "Login attempt processed", "INFO"));
            return result;
        } catch (Throwable t) {
            logRepo.save(new SecurityLog(emailArg, "LOGIN_ERROR", t.getMessage(), "ERROR"));
            throw t;
        }
    }

    // Pointcut: user feedback (UserController.report)
    @Around("execution(* com.example.securityaop.controller.UserController.report(..))")
    public Object logFeedback(ProceedingJoinPoint pjp) throws Throwable {
        return logAroundWithArgs(pjp, "USER_FEEDBACK", "INFO");
    }

    // Pointcut: admin actions (deleteUser, updateRole, setActive)
    @Around("execution(* com.example.securityaop.controller.AdminController.*(..))")
    public Object logAdminActions(ProceedingJoinPoint pjp) throws Throwable {
        // determine method name and args
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String method = ms.getMethod().getName();
        return logAround(pjp, "ADMIN_" + method.toUpperCase(), "ADMIN_ACTION");
    }

    // generic logging wrapper: uses SecurityContext to know current user (set earlier by AuthorizationAspect)
    private Object logAround(ProceedingJoinPoint pjp, String action, String type) throws Throwable {
        String currentEmail = SecurityContext.getCurrentUserEmail();
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String pathInfo = null;
        if (sra != null) {
            HttpServletRequest req = sra.getRequest();
            pathInfo = req.getMethod() + " " + req.getRequestURI();
        }
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String details = ms.getMethod().getName() + " args=" + Arrays.toString(pjp.getArgs()) + (pathInfo != null ? (" path=" + pathInfo) : "");
        try {
            Object result = pjp.proceed();
            logRepo.save(new SecurityLog(currentEmail, action, details, type));
            return result;
        } catch (Throwable t) {
            logRepo.save(new SecurityLog(currentEmail, action + "_ERROR", details + " error=" + t.getMessage(), "ERROR"));
            throw t;
        }
    }

    // variant that composes args into details but tries to avoid dumping large objects
    private Object logAroundWithArgs(ProceedingJoinPoint pjp, String action, String type) throws Throwable {
        String currentEmail = SecurityContext.getCurrentUserEmail();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        Object[] args = pjp.getArgs();
        String argsStr = Arrays.toString(Arrays.stream(args == null ? new Object[]{} : args)
                .map(a -> a == null ? "null" : shorten(a.toString()))
                .toArray());
        String details = ms.getMethod().getName() + " args=" + argsStr;
        try {
            Object result = pjp.proceed();
            logRepo.save(new SecurityLog(currentEmail, action, details, type));
            return result;
        } catch (Throwable t) {
            logRepo.save(new SecurityLog(currentEmail, action + "_ERROR", details + " error=" + t.getMessage(), "ERROR"));
            throw t;
        }
    }

    private String shorten(String s) {
        if (s == null) return "null";
        return s.length() > 200 ? s.substring(0, 200) + "...(truncated)" : s;
    }
}
