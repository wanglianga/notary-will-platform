package com.notary.will.service;

import com.notary.will.entity.MaterialItem;
import com.notary.will.entity.SupplementItem;
import com.notary.will.enums.MaterialStatus;
import com.notary.will.enums.SupplementStatus;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.MaterialItemRepository;
import com.notary.will.repository.SupplementItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialItemService {

    private final MaterialItemRepository materialItemRepository;
    private final SupplementItemRepository supplementItemRepository;
    private final WillCaseService willCaseService;

    public List<MaterialItem> getMaterialsByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return materialItemRepository.findByCaseId(caseId);
    }

    @Transactional
    public MaterialItem createMaterial(Long caseId, MaterialItem material) {
        willCaseService.getCaseById(caseId);
        material.setCaseId(caseId);
        material.setStatus(MaterialStatus.PENDING);
        return materialItemRepository.save(material);
    }

    @Transactional
    public MaterialItem reviewMaterial(Long id, MaterialStatus status, Long reviewerId, String comments) {
        MaterialItem material = materialItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("材料不存在"));
        material.setStatus(status);
        material.setReviewedBy(reviewerId);
        material.setReviewedAt(LocalDateTime.now());
        material.setComments(comments);
        return materialItemRepository.save(material);
    }

    public List<SupplementItem> getSupplementsByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return supplementItemRepository.findByCaseId(caseId);
    }

    @Transactional
    public SupplementItem requestSupplement(Long caseId, String materialType, String description, LocalDate deadline) {
        willCaseService.getCaseById(caseId);
        SupplementItem supplement = new SupplementItem();
        supplement.setCaseId(caseId);
        supplement.setMaterialType(materialType);
        supplement.setDescription(description);
        supplement.setDeadline(deadline);
        supplement.setStatus(SupplementStatus.PENDING);
        return supplementItemRepository.save(supplement);
    }

    @Transactional
    public SupplementItem submitSupplement(Long id) {
        SupplementItem supplement = supplementItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补件不存在"));
        supplement.setStatus(SupplementStatus.SUBMITTED);
        supplement.setSubmittedAt(LocalDateTime.now());
        return supplementItemRepository.save(supplement);
    }

    @Transactional
    public SupplementItem approveSupplement(Long id) {
        SupplementItem supplement = supplementItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补件不存在"));
        supplement.setStatus(SupplementStatus.APPROVED);
        supplement.setApprovedAt(LocalDateTime.now());
        return supplementItemRepository.save(supplement);
    }
}
