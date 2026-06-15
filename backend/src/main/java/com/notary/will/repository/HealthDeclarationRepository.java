package com.notary.will.repository;

import com.notary.will.entity.HealthDeclaration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthDeclarationRepository extends JpaRepository<HealthDeclaration, Long> {
    Optional<HealthDeclaration> findByCaseId(Long caseId);
}
