package com.example.securityaop.config;

import com.example.securityaop.service.SecurityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestDataLoader implements CommandLineRunner {

    @Autowired
    private SecurityLogService logService;

    @Override
    public void run(String... args) throws Exception {
        // Sample test logs for demo
        logService.addLog("admin@example.com", "Login", "INFO");
        logService.addLog("user@example.com", "Access /admin", "WARN");
        logService.addLog("disabled@example.com", "Login Attempt", "ERROR");
        logService.addLog("jane.doe@example.com", "Failed Login", "ERROR");
        logService.addLog("john.doe@example.com", "Password Change", "INFO");
    }
}
