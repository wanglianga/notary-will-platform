package com.notary.will.service;

import com.notary.will.entity.KinshipRelation;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.KinshipRelationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KinshipRelationService {

    private final KinshipRelationRepository kinshipRelationRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public KinshipRelation create(Long caseId, KinshipRelation relation) {
        willCaseService.getCaseById(caseId);
        relation.setCaseId(caseId);
        return kinshipRelationRepository.save(relation);
    }

    public List<KinshipRelation> getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return kinshipRelationRepository.findByCaseId(caseId);
    }

    public KinshipRelation getById(Long id) {
        return kinshipRelationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("亲属关系不存在"));
    }

    @Transactional
    public KinshipRelation update(Long id, KinshipRelation update) {
        KinshipRelation relation = getById(id);
        if (update.getRelativeName() != null) relation.setRelativeName(update.getRelativeName());
        if (update.getRelation() != null) relation.setRelation(update.getRelation());
        if (update.getIdNumber() != null) relation.setIdNumber(update.getIdNumber());
        if (update.getPhone() != null) relation.setPhone(update.getPhone());
        if (update.getIsBeneficiary() != null) relation.setIsBeneficiary(update.getIsBeneficiary());
        return kinshipRelationRepository.save(relation);
    }

    @Transactional
    public void delete(Long id) {
        kinshipRelationRepository.deleteById(id);
    }
}
