package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.ArchiveRecord;
import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import com.notary.will.repository.ArchiveRecordRepository;
import com.notary.will.service.ArchiveService;
import com.notary.will.service.WillCaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class ArchiveController extends BaseCaseController {

    @Autowired
    private WillCaseService willCaseService;

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private ArchiveRecordRepository archiveRecordRepository;

    @GetMapping("/api/cases/{caseId}/archive")
    @PreAuthorize("hasAnyRole('ARCHIVIST', 'NOTARY', 'REVIEWER')")
    public ApiResponse<Map<String, Object>> getCaseArchiveInfo(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);

        WillCase willCase = willCaseRepository.findById(caseId)
                .orElseThrow(() -> new com.notary.will.exception.BusinessException("案件不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("caseInfo", willCase);
        result.put("identity", willCaseService.getIdentityInfo(caseId));
        result.put("properties", willCaseService.getPropertyInventory(caseId));
        result.put("kinship", willCaseService.getKinshipRelations(caseId));
        result.put("health", willCaseService.getHealthDeclaration(caseId));
        result.put("beneficiaries", willCaseService.getBeneficiaries(caseId));
        result.put("witnesses", willCaseService.getWitnesses(caseId));
        result.put("archiveRecords", archiveRecordRepository.findByCaseIdOrderByCreatedAtDesc(caseId));

        return ApiResponse.ok(result);
    }

    @PostMapping("/api/cases/{caseId}/archive")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<ArchiveRecord> archiveCase(@PathVariable Long caseId,
                                                   @RequestBody Map<String, Object> archiveData,
                                                   Authentication auth) {
        checkCaseOwnership(caseId, auth);

        ArchiveRecord record = new ArchiveRecord();
        record.setCaseId(caseId);
        record.setArchivistId(getCurrentUserId(auth));
        record.setLocation((String) archiveData.getOrDefault("location", "A区-01-01"));
        record.setBoxNumber((String) archiveData.getOrDefault("boxNumber", "BOX-001"));
        record.setPaperMaterialsComplete((Boolean) archiveData.getOrDefault("paperComplete", true));
        record.setElectronicMaterialsComplete((Boolean) archiveData.getOrDefault("electronicComplete", true));
        record.setSealed(true);

        willCaseService.updateStatus(caseId, CaseStatus.ARCHIVED, getCurrentUserId(auth));

        return ApiResponse.ok(archiveService.create(record));
    }

    @GetMapping("/api/archive/cases")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<Map<String, Object>> getArchiveList(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WillCase> casePage;

        if (status != null && !status.isEmpty()) {
            casePage = willCaseRepository.findByStatus(CaseStatus.valueOf(status), pageable);
        } else {
            casePage = willCaseRepository.findByStatusIn(
                    List.of(CaseStatus.WITNESS_SIGNING, CaseStatus.ARCHIVED, CaseStatus.CLOSED),
                    pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", casePage.getContent());
        result.put("total", casePage.getTotalElements());
        result.put("stats", getArchiveStats());

        return ApiResponse.ok(result);
    }

    private Map<String, Long> getArchiveStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", willCaseRepository.countByStatus(CaseStatus.WITNESS_SIGNING));
        stats.put("archived", willCaseRepository.countByStatus(CaseStatus.ARCHIVED));
        stats.put("sealed", archiveRecordRepository.countBySealedTrue());
        return stats;
    }

    @GetMapping("/api/archive/cases/{caseId}")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<Map<String, Object>> getArchiveDetail(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);

        WillCase willCase = willCaseRepository.findById(caseId)
                .orElseThrow(() -> new com.notary.will.exception.BusinessException("案件不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("caseInfo", willCase);
        result.put("identity", willCaseService.getIdentityInfo(caseId));
        result.put("properties", willCaseService.getPropertyInventory(caseId));
        result.put("kinship", willCaseService.getKinshipRelations(caseId));
        result.put("health", willCaseService.getHealthDeclaration(caseId));
        result.put("beneficiaries", willCaseService.getBeneficiaries(caseId));
        result.put("witnesses", willCaseService.getWitnesses(caseId));
        result.put("archiveRecords", archiveRecordRepository.findByCaseIdOrderByCreatedAtDesc(caseId));

        return ApiResponse.ok(result);
    }

    @PostMapping("/api/archive/cases/{caseId}/seal")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<ArchiveRecord> sealCase(@PathVariable Long caseId,
                                                @RequestBody Map<String, Object> sealData,
                                                Authentication auth) {
        checkCaseOwnership(caseId, auth);

        ArchiveRecord record = new ArchiveRecord();
        record.setCaseId(caseId);
        record.setArchivistId(getCurrentUserId(auth));
        record.setLocation((String) sealData.getOrDefault("location", "A区-01-01"));
        record.setBoxNumber((String) sealData.getOrDefault("boxNumber", "BOX-001"));
        record.setPaperMaterialsComplete((Boolean) sealData.getOrDefault("paperComplete", true));
        record.setElectronicMaterialsComplete((Boolean) sealData.getOrDefault("electronicComplete", true));
        record.setSealed(true);

        willCaseService.updateStatus(caseId, CaseStatus.ARCHIVED, getCurrentUserId(auth));

        return ApiResponse.ok(archiveService.create(record));
    }

    @PostMapping("/api/archive/cases/{caseId}/unseal")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<ArchiveRecord> unsealCase(@PathVariable Long caseId,
                                                  @RequestBody Map<String, Object> unsealData,
                                                  Authentication auth) {
        checkCaseOwnership(caseId, auth);

        List<ArchiveRecord> records = archiveRecordRepository.findByCaseId(caseId);
        if (records.isEmpty()) {
            throw new com.notary.will.exception.BusinessException("该案件尚未归档");
        }

        ArchiveRecord record = records.get(records.size() - 1);
        record.setSealed(false);
        record.setUnsealedById(getCurrentUserId(auth));
        record.setUnsealReason((String) unsealData.getOrDefault("reason", "查阅"));

        return ApiResponse.ok(archiveService.update(record.getId(), record));
    }

    @GetMapping("/api/archive/unseal-requests")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<List<ArchiveRecord>> getUnsealRequests(Authentication auth) {
        return ApiResponse.ok(archiveRecordRepository.findBySealedFalse());
    }
}
