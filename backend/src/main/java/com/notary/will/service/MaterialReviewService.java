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
public class MaterialReviewService {

    private final MaterialItemRepository materialItemRepository;
    private final SupplementItemRepository supplementItemRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public MaterialItem createMaterial(Long caseId, MaterialItem material) {
        willCaseService.getCaseById(caseId);
        material.setCaseId(caseId);
        material.setStatus(MaterialStatus.PENDING);
        return materialItemRepository.save(material);
    }

    public List<MaterialItem> getMaterialsByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return materialItemRepository.findByCaseId(caseId);
    }

    public MaterialItem getMaterialById(Long id) {
        return materialItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("材料不存在"));
    }

    @Transactional
    public MaterialItem reviewMaterial(Long id, MaterialStatus status, Long reviewerId, String comments) {
        MaterialItem material = getMaterialById(id);
        material.setStatus(status);
        material.setReviewedBy(reviewerId);
        material.setReviewedAt(LocalDateTime.now());
        material.setComments(comments);
        return materialItemRepository.save(material);
    }

    @Transactional
    public SupplementItem requestSupplement(Long caseId, String materialType, String description, LocalDate deadline) {
        willCaseService.getCaseById(caseId);
        SupplementItem item = new SupplementItem();
        item.setCaseId(caseId);
        item.setMaterialType(materialType);
        item.setDescription(description);
        item.setDeadline(deadline);
        item.setStatus(SupplementStatus.PENDING);
        return supplementItemRepository.save(item);
    }

    @Transactional
    public SupplementItem submitSupplement(Long id) {
        SupplementItem item = supplementItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补正项不存在"));
        item.setStatus(SupplementStatus.SUBMITTED);
        item.setSubmittedAt(LocalDateTime.now());
        return supplementItemRepository.save(item);
    }

    @Transactional
    public SupplementItem approveSupplement(Long id) {
        SupplementItem item = supplementItemRepository.findById(id)
                .orElseThrow(() -> new BusinessException("补正项不存在"));
        item.setStatus(SupplementStatus.APPROVED);
        return supplementItemRepository.save(item);
    }

    public List<SupplementItem> getSupplementsByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return supplementItemRepository.findByCaseId(caseId);
    }

    public List<SupplementItem> getPendingSupplements(Long caseId) {
        return supplementItemRepository.findByCaseIdAndStatus(caseId, SupplementStatus.PENDING);
    }
}
