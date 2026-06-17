package com.notary.will.repository;

import com.notary.will.entity.HighRiskInterview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HighRiskInterviewRepository extends JpaRepository<HighRiskInterview, Long> {
    List<HighRiskInterview> findByCaseId(Long caseId);
    List<HighRiskInterview> findByCaseIdOrderByCreatedAtDesc(Long caseId);
}
