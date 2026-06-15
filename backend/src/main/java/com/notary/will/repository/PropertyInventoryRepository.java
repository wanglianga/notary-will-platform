package com.notary.will.repository;

import com.notary.will.entity.PropertyInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyInventoryRepository extends JpaRepository<PropertyInventory, Long> {
    List<PropertyInventory> findByCaseId(Long caseId);
}
