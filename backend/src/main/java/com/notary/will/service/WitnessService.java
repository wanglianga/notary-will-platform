package com.notary.will.service;

import com.notary.will.entity.Witness;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.WitnessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WitnessService {

    private final WitnessRepository witnessRepository;

    public List<Witness> getByCaseId(Long caseId) {
        return witnessRepository.findByCaseId(caseId);
    }

    public Witness getById(Long id) {
        return witnessRepository.findById(id)
                .orElseThrow(() -> new BusinessException("见证人不存在"));
    }

    @Transactional
    public Witness create(Long caseId, Witness witness) {
        witness.setCaseId(caseId);
        return witnessRepository.save(witness);
    }

    @Transactional
    public Witness update(Long id, Witness update) {
        Witness witness = getById(id);
        if (update.getName() != null) witness.setName(update.getName());
        if (update.getIdType() != null) witness.setIdType(update.getIdType());
        if (update.getIdNumber() != null) witness.setIdNumber(update.getIdNumber());
        if (update.getPhone() != null) witness.setPhone(update.getPhone());
        if (update.getRelation() != null) witness.setRelation(update.getRelation());
        if (update.getDeclaredNoConflict() != null) witness.setDeclaredNoConflict(update.getDeclaredNoConflict());
        return witnessRepository.save(witness);
    }

    @Transactional
    public void delete(Long id) {
        witnessRepository.deleteById(id);
    }
}
