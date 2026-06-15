package com.notary.will.repository;

import com.notary.will.entity.InterviewRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterviewRecordRepository extends JpaRepository<InterviewRecord, Long> {
    List<InterviewRecord> findByCaseId(Long caseId);
}
