package com.notary.will.repository;

import com.notary.will.entity.Witness;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WitnessRepository extends JpaRepository<Witness, Long> {
    List<Witness> findByCaseId(Long caseId);
}
