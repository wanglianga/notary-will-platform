package com.notary.will.repository;

import com.notary.will.entity.ArchiveRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArchiveRecordRepository extends JpaRepository<ArchiveRecord, Long> {
    Optional<ArchiveRecord> findByCaseId(Long caseId);
}
