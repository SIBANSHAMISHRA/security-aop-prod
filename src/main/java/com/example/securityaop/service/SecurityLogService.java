package com.example.securityaop.service;

import com.example.securityaop.model.SecurityLog;
import com.example.securityaop.repository.SecurityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SecurityLogService {

    @Autowired
    private SecurityLogRepository repo;

    /**
     * ✅ New flexible save method (4 args: email, action, details, type)
     */
    public void save(String email, String action, String details, String type) {
        SecurityLog log = new SecurityLog(email, action, details, type);
        log.setTimestamp(Instant.now());
        repo.save(log);
    }

    /**
     * ✅ Backward compatibility (old code in LoggingAspect/TestDataLoader uses addLog)
     * This keeps older classes working without refactoring.
     */
    public void addLog(String email, String action, String details) {
        save(email, action, details, "INFO");
    }

    /**
     * ✅ Get the latest 20 security logs (newest first)
     */
    public List<SecurityLog> getLatestLogs() {
        return repo.findTop20ByOrderByTimestampDesc();
    }
}
