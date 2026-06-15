package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.MaterialItem;
import com.notary.will.entity.SupplementItem;
import com.notary.will.enums.MaterialStatus;
import com.notary.will.service.MaterialReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/materials")
@RequiredArgsConstructor
public class MaterialReviewController {

    private final MaterialReviewService materialReviewService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<MaterialItem>> getMaterials(@PathVariable Long caseId) {
        return ApiResponse.ok(materialReviewService.getMaterialsByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<MaterialItem> createMaterial(@PathVariable Long caseId,
                                                     @RequestBody MaterialItem material) {
        return ApiResponse.ok(materialReviewService.createMaterial(caseId, material));
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<MaterialItem> reviewMaterial(@PathVariable Long caseId,
                                                     @PathVariable Long id,
                                                     @RequestParam MaterialStatus status,
                                                     @RequestParam(required = false) String comments,
                                                     Authentication auth) {
        Long reviewerId = (Long) auth.getCredentials();
        return ApiResponse.ok(materialReviewService.reviewMaterial(id, status, reviewerId, comments));
    }

    @PostMapping("/supplement-request")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<SupplementItem> requestSupplement(@PathVariable Long caseId,
                                                          @RequestParam String materialType,
                                                          @RequestParam String description,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline) {
        return ApiResponse.ok(materialReviewService.requestSupplement(caseId, materialType, description, deadline));
    }

    @GetMapping("/supplements")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<SupplementItem>> getSupplements(@PathVariable Long caseId) {
        return ApiResponse.ok(materialReviewService.getSupplementsByCaseId(caseId));
    }

    @PostMapping("/supplements/{id}/submit")
    @PreAuthorize("hasRole('APPLICANT')")
    public ApiResponse<SupplementItem> submitSupplement(@PathVariable Long id) {
        return ApiResponse.ok(materialReviewService.submitSupplement(id));
    }

    @PostMapping("/supplements/{id}/approve")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<SupplementItem> approveSupplement(@PathVariable Long id) {
        return ApiResponse.ok(materialReviewService.approveSupplement(id));
    }
}
