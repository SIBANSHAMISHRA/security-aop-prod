package com.example.securityaop.repository;

import com.example.securityaop.model.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {

    // ✅ Fetch latest 20 logs ordered by newest first
    List<SecurityLog> findTop20ByOrderByTimestampDesc();
}
