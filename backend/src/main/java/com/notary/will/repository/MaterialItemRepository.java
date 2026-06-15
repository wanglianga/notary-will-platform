package com.notary.will.repository;

import com.notary.will.entity.MaterialItem;
import com.notary.will.enums.MaterialStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaterialItemRepository extends JpaRepository<MaterialItem, Long> {
    List<MaterialItem> findByCaseId(Long caseId);
    List<MaterialItem> findByCaseIdAndStatus(Long caseId, MaterialStatus status);
}
