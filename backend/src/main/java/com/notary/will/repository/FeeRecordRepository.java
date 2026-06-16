package com.notary.will.repository;

import com.notary.will.entity.FeeRecord;
import com.notary.will.enums.FeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface FeeRecordRepository extends JpaRepository<FeeRecord, Long> {
    List<FeeRecord> findByCaseId(Long caseId);
    List<FeeRecord> findByCaseIdAndStatus(Long caseId, FeeStatus status);
    Page<FeeRecord> findByStatus(FeeStatus status, Pageable pageable);
    long countByStatus(FeeStatus status);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FeeRecord f WHERE f.status = ?1")
    BigDecimal sumByStatus(FeeStatus status);
}
