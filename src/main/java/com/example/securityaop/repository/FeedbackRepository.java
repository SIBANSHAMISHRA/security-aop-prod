package com.example.securityaop.repository;

import com.example.securityaop.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> { }
