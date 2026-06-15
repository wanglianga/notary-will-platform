package com.notary.will.repository;

import com.notary.will.entity.KinshipRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KinshipRelationRepository extends JpaRepository<KinshipRelation, Long> {
    List<KinshipRelation> findByCaseId(Long caseId);
}
