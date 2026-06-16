package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.MaterialItem;
import com.notary.will.entity.SupplementItem;
import com.notary.will.enums.MaterialStatus;
import com.notary.will.service.MaterialItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/materials")
public class MaterialReviewController extends BaseCaseController {

    @Autowired
    private MaterialItemService materialItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<MaterialItem>> getMaterials(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.getMaterialsByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<MaterialItem> createMaterial(@PathVariable Long caseId,
                                                     @RequestBody MaterialItem material,
                                                     Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.createMaterial(caseId, material));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<MaterialItem> reviewMaterial(@PathVariable Long caseId,
                                                     @PathVariable Long id,
                                                     @RequestParam MaterialStatus status,
                                                     @RequestParam(required = false) String comments,
                                                     Authentication auth) {
        checkCaseOwnership(caseId, auth);
        Long reviewerId = (Long) auth.getCredentials();
        return ApiResponse.ok(materialItemService.reviewMaterial(id, status, reviewerId, comments));
    }

    @PostMapping("/supplement-request")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<SupplementItem> requestSupplement(@PathVariable Long caseId,
                                                          @RequestParam String materialType,
                                                          @RequestParam String description,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
                                                          Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.requestSupplement(caseId, materialType, description, deadline));
    }

    @GetMapping("/supplements")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<SupplementItem>> getSupplements(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.getSupplementsByCaseId(caseId));
    }

    @PostMapping("/supplements/{id}/submit")
    @PreAuthorize("hasRole('APPLICANT')")
    public ApiResponse<SupplementItem> submitSupplement(@PathVariable Long caseId,
                                                         @PathVariable Long id,
                                                         Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.submitSupplement(id));
    }

    @PostMapping("/supplements/{id}/approve")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<SupplementItem> approveSupplement(@PathVariable Long caseId,
                                                          @PathVariable Long id,
                                                          Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.approveSupplement(id));
    }
}
