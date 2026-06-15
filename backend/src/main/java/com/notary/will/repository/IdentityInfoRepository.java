package com.notary.will.repository;

import com.notary.will.entity.IdentityInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IdentityInfoRepository extends JpaRepository<IdentityInfo, Long> {
    Optional<IdentityInfo> findByCaseId(Long caseId);
    List<IdentityInfo> findByCaseIdIn(List<Long> caseIds);
}
