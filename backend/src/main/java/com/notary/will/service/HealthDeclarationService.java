package com.notary.will.service;

import com.notary.will.entity.HealthDeclaration;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.HealthDeclarationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HealthDeclarationService {

    private final HealthDeclarationRepository healthDeclarationRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public HealthDeclaration createOrUpdate(Long caseId, HealthDeclaration declaration) {
        willCaseService.getCaseById(caseId);
        declaration.setCaseId(caseId);
        return healthDeclarationRepository.findByCaseId(caseId)
                .map(existing -> {
                    declaration.setId(existing.getId());
                    return healthDeclarationRepository.save(declaration);
                })
                .orElseGet(() -> healthDeclarationRepository.save(declaration));
    }

    public HealthDeclaration getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return healthDeclarationRepository.findByCaseId(caseId)
                .orElseThrow(() -> new BusinessException("健康声明不存在"));
    }
}
