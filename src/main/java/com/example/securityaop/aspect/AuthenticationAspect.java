package com.example.securityaop.aspect;

import com.example.securityaop.model.LoginRequest;
import com.example.securityaop.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class AuthenticationAspect {

    @Autowired private UserService userService;

    @Before("execution(* com.example.securityaop.controller.AuthController.login(..)) && args(req)")
    public void authenticate(JoinPoint jp, LoginRequest req) {
        boolean ok = userService.checkPassword(req.getEmail(), req.getPassword());
        if (!ok) {
            System.out.println("[AUTH] Invalid credentials: " + req.getEmail());
        } else {
            System.out.println("[AUTH] Credentials OK for: " + req.getEmail());
        }
    }
}
