package com.notary.will.service;

import com.notary.will.entity.MaterialItem;
import com.notary.will.entity.SupplementItem;
import com.notary.will.entity.SupplementVersion;
import com.notary.will.enums.MaterialStatus;
import com.notary.will.enums.SupplementStatus;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.MaterialItemRepository;
import com.notary.will.repository.SupplementItemRepository;
import com.notary.will.repository.SupplementVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MaterialItemService {

    private final MaterialItemRepository materialItemRepository;
    private final SupplementItemRepository supplementItemRepository;
    private final SupplementVersionRepository supplementVersionRepository;
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
    public SupplementItem requestSupplementWithValidity(Long caseId, Map<String, Object> data) {
        willCaseService.getCaseById(caseId);
        SupplementItem supplement = new SupplementItem();
        supplement.setCaseId(caseId);
        supplement.setMaterialType((String) data.get("materialType"));
        supplement.setDescription((String) data.get("description"));
        supplement.setStatus(SupplementStatus.PENDING);

        if (data.get("deadline") != null) {
            supplement.setDeadline(LocalDate.parse((String) data.get("deadline")));
        }
        if (data.get("validityPeriodDays") != null) {
            supplement.setValidityPeriodDays(Integer.valueOf(data.get("validityPeriodDays").toString()));
        }
        if (data.get("alternativeMaterials") != null) {
            supplement.setAlternativeMaterials((String) data.get("alternativeMaterials"));
        }
        if (data.get("reservationRetentionDays") != null) {
            int retentionDays = Integer.valueOf(data.get("reservationRetentionDays").toString());
            supplement.setReservationRetentionDays(retentionDays);
            supplement.setReservationExpiry(LocalDateTime.now().plusDays(retentionDays));
        }
        if (data.get("requiresReInterview") != null) {
            supplement.setRequiresReInterview(Boolean.valueOf(data.get("requiresReInterview").toString()));
        }

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

    public List<SupplementVersion> getSupplementVersions(Long supplementItemId) {
        return supplementVersionRepository.findBySupplementItemIdOrderByVersionNumberAsc(supplementItemId);
    }

    public List<SupplementVersion> getSupplementVersionsByCaseId(Long caseId) {
        return supplementVersionRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
    }

    @Transactional
    public SupplementVersion submitSupplementVersion(Long supplementItemId, Long caseId, Map<String, Object> data, Long submittedBy) {
        supplementItemRepository.findById(supplementItemId)
                .orElseThrow(() -> new BusinessException("补件不存在"));

        List<SupplementVersion> existing = supplementVersionRepository.findBySupplementItemIdOrderByVersionNumberAsc(supplementItemId);
        int nextVersion = existing.size() + 1;

        SupplementVersion version = new SupplementVersion();
        version.setSupplementItemId(supplementItemId);
        version.setCaseId(caseId);
        version.setVersionNumber(nextVersion);
        version.setMaterialType((String) data.get("materialType"));
        version.setFileUrl((String) data.get("fileUrl"));
        version.setDescription((String) data.get("description"));
        version.setChangeSummary((String) data.get("changeSummary"));
        version.setSubmittedBy(submittedBy);

        return supplementVersionRepository.save(version);
    }

    @Transactional
    public SupplementVersion reviewSupplementVersion(Long versionId, Long reviewerId, String reviewResult) {
        SupplementVersion version = supplementVersionRepository.findById(versionId)
                .orElseThrow(() -> new BusinessException("补件版本不存在"));
        version.setReviewedBy(reviewerId);
        version.setReviewResult(reviewResult);
        return supplementVersionRepository.save(version);
    }

    public boolean caseRequiresReInterview(Long caseId) {
        List<SupplementItem> supplements = supplementItemRepository.findByCaseId(caseId);
        return supplements.stream().anyMatch(s ->
                Boolean.TRUE.equals(s.getRequiresReInterview()) && s.getStatus() == SupplementStatus.APPROVED);
    }
}
