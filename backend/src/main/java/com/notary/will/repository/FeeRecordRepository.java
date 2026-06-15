package com.notary.will.repository;

import com.notary.will.entity.FeeRecord;
import com.notary.will.enums.FeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeRecordRepository extends JpaRepository<FeeRecord, Long> {
    List<FeeRecord> findByCaseId(Long caseId);
    List<FeeRecord> findByCaseIdAndStatus(Long caseId, FeeStatus status);
}
