package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.MaterialItem;
import com.notary.will.entity.MaterialReview;
import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import com.notary.will.enums.MaterialStatus;
import com.notary.will.repository.MaterialItemRepository;
import com.notary.will.repository.MaterialReviewRepository;
import com.notary.will.service.MaterialItemService;
import com.notary.will.service.MaterialReviewService;
import com.notary.will.service.HighRiskInterviewService;
import com.notary.will.service.WillCaseService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController extends BaseCaseController {

    private final WillCaseService willCaseService;
    private final MaterialReviewService materialReviewService;
    private final MaterialReviewRepository materialReviewRepository;
    private final MaterialItemService materialItemService;
    private final MaterialItemRepository materialItemRepository;
    private final HighRiskInterviewService highRiskInterviewService;

    @GetMapping("/queue")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse<Map<String, Object>> getReviewQueue(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WillCase> casePage;

        if (status != null && !status.isEmpty()) {
            CaseStatus caseStatus = status.equals("PENDING") ? CaseStatus.SUBMITTED :
                                   status.equals("IN_PROGRESS") ? CaseStatus.UNDER_REVIEW :
                                   CaseStatus.valueOf(status);
            casePage = willCaseRepository.findByStatus(caseStatus, pageable);
        } else {
            casePage = willCaseRepository.findByStatusIn(
                    List.of(CaseStatus.SUBMITTED, CaseStatus.UNDER_REVIEW,
                            CaseStatus.SUPPLEMENT_REQUIRED, CaseStatus.REVIEW_PASSED,
                            CaseStatus.REVIEW_FAILED),
                    pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", casePage.getContent());
        result.put("total", casePage.getTotalElements());
        result.put("stats", getReviewStats());

        return ApiResponse.ok(result);
    }

    private Map<String, Long> getReviewStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", willCaseRepository.countByStatus(CaseStatus.SUBMITTED));
        stats.put("inProgress", willCaseRepository.countByStatus(CaseStatus.UNDER_REVIEW));
        stats.put("approved", willCaseRepository.countByStatus(CaseStatus.REVIEW_PASSED));
        stats.put("rejected", willCaseRepository.countByStatus(CaseStatus.REVIEW_FAILED));
        return stats;
    }

    @GetMapping("/case/{caseId}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse<Map<String, Object>> getReviewDetail(@PathVariable Long caseId, Authentication auth) {
        WillCase willCase = getCaseAndCheckAccess(caseId, auth);

        Map<String, Object> result = new HashMap<>();
        result.put("caseInfo", willCase);
        result.put("identity", willCaseService.getIdentityInfo(caseId));
        result.put("properties", willCaseService.getPropertyInventory(caseId));
        result.put("kinship", willCaseService.getKinshipRelations(caseId));
        result.put("health", willCaseService.getHealthDeclaration(caseId));
        result.put("beneficiaries", willCaseService.getBeneficiaries(caseId));
        result.put("witnesses", willCaseService.getWitnesses(caseId));
        result.put("materials", materialItemRepository.findByCaseId(caseId));
        result.put("highRiskInterviewSummary", highRiskInterviewService.getReviewerView(caseId));
        result.put("supplements", materialItemService.getSupplementsByCaseId(caseId));

        return ApiResponse.ok(result);
    }

    @PutMapping("/case/{caseId}/material/{materialId}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse<MaterialItem> reviewMaterial(@PathVariable Long caseId,
                                                     @PathVariable Long materialId,
                                                     @RequestBody Map<String, Object> reviewData,
                                                     Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        String statusStr = (String) reviewData.get("status");
        String comments = (String) reviewData.get("comments");
        MaterialStatus status = MaterialStatus.valueOf(statusStr);

        return ApiResponse.ok(materialItemService.reviewMaterial(
                materialId, status, getCurrentUserId(auth), comments));
    }

    @PostMapping("/case/{caseId}/decision")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse<WillCase> submitReviewDecision(@PathVariable Long caseId,
                                                       @RequestBody Map<String, Object> decision,
                                                       Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        String decisionStr = (String) decision.get("decision");
        String comment = (String) decision.get("comment");
        Long reviewerId = getCurrentUserId(auth);

        WillCase updated = willCaseService.updateStatus(caseId,
                "APPROVED".equals(decisionStr) ? CaseStatus.REVIEW_PASSED : CaseStatus.REVIEW_FAILED,
                reviewerId);

        return ApiResponse.ok(updated);
    }

    @PostMapping("/case/{caseId}/supplement")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse<MaterialReview> generateSupplementList(@PathVariable Long caseId,
                                                               @RequestBody Map<String, Object> supplement,
                                                               Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        MaterialReview review = new MaterialReview();
        review.setCaseId(caseId);
        review.setMaterialType("SUPPLEMENT");
        review.setReviewResult("SUPPLEMENT_REQUIRED");
        review.setReviewComment((String) supplement.get("description"));
        review.setReviewerId(getCurrentUserId(auth));

        willCaseService.updateStatus(caseId, CaseStatus.SUPPLEMENT_REQUIRED, getCurrentUserId(auth));

        if (supplement.get("validityPeriodDays") != null || supplement.get("reservationRetentionDays") != null) {
            materialItemService.requestSupplementWithValidity(caseId, supplement);
        }

        return ApiResponse.ok(materialReviewService.create(review));
    }
}
