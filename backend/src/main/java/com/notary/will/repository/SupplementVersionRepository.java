package com.notary.will.repository;

import com.notary.will.entity.SupplementVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplementVersionRepository extends JpaRepository<SupplementVersion, Long> {
    List<SupplementVersion> findBySupplementItemIdOrderByVersionNumberAsc(Long supplementItemId);
    List<SupplementVersion> findByCaseIdOrderByCreatedAtDesc(Long caseId);
}
