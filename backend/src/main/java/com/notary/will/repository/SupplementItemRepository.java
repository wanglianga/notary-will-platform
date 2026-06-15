package com.notary.will.repository;

import com.notary.will.entity.SupplementItem;
import com.notary.will.enums.SupplementStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplementItemRepository extends JpaRepository<SupplementItem, Long> {
    List<SupplementItem> findByCaseId(Long caseId);
    List<SupplementItem> findByCaseIdAndStatus(Long caseId, SupplementStatus status);
}
