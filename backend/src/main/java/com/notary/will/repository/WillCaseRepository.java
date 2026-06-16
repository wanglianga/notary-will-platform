package com.notary.will.repository;

import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WillCaseRepository extends JpaRepository<WillCase, Long> {
    Optional<WillCase> findByCaseNumber(String caseNumber);
    List<WillCase> findByApplicantId(Long applicantId);
    List<WillCase> findByNotaryId(Long notaryId);
    List<WillCase> findByReviewerId(Long reviewerId);
    List<WillCase> findByStatus(CaseStatus status);
    Page<WillCase> findByStatus(CaseStatus status, Pageable pageable);
    Page<WillCase> findByStatusIn(List<CaseStatus> statuses, Pageable pageable);
    long countByStatus(CaseStatus status);
}
