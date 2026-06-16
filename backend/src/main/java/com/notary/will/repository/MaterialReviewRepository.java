package com.notary.will.repository;

import com.notary.will.entity.MaterialReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialReviewRepository extends JpaRepository<MaterialReview, Long> {
    List<MaterialReview> findByCaseId(Long caseId);
    List<MaterialReview> findByCaseIdAndMaterialType(Long caseId, String materialType);
}
