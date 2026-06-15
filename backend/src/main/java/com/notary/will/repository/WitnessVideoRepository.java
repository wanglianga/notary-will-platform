package com.notary.will.repository;

import com.notary.will.entity.WitnessVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WitnessVideoRepository extends JpaRepository<WitnessVideo, Long> {
    List<WitnessVideo> findByCaseId(Long caseId);
}
