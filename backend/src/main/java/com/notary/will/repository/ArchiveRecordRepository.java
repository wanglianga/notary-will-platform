package com.notary.will.repository;

import com.notary.will.entity.ArchiveRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArchiveRecordRepository extends JpaRepository<ArchiveRecord, Long> {
    Optional<ArchiveRecord> findTopByCaseIdOrderByCreatedAtDesc(Long caseId);
    List<ArchiveRecord> findByCaseId(Long caseId);
    List<ArchiveRecord> findByCaseIdOrderByCreatedAtDesc(Long caseId);
    long countBySealedTrue();
    List<ArchiveRecord> findBySealedFalse();
}
