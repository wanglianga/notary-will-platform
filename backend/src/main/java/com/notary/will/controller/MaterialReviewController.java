package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.MaterialItem;
import com.notary.will.entity.SupplementItem;
import com.notary.will.entity.SupplementVersion;
import com.notary.will.enums.MaterialStatus;
import com.notary.will.service.MaterialItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

    @PostMapping("/supplement-request-with-validity")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<SupplementItem> requestSupplementWithValidity(@PathVariable Long caseId,
                                                                      @RequestBody Map<String, Object> data,
                                                                      Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.requestSupplementWithValidity(caseId, data));
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

    @GetMapping("/supplements/{supplementItemId}/versions")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<SupplementVersion>> getSupplementVersions(@PathVariable Long caseId,
                                                                       @PathVariable Long supplementItemId,
                                                                       Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.getSupplementVersions(supplementItemId));
    }

    @GetMapping("/supplement-versions")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<SupplementVersion>> getSupplementVersionsByCase(@PathVariable Long caseId,
                                                                             Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(materialItemService.getSupplementVersionsByCaseId(caseId));
    }

    @PostMapping("/supplements/{supplementItemId}/versions")
    @PreAuthorize("hasRole('APPLICANT')")
    public ApiResponse<SupplementVersion> submitSupplementVersion(@PathVariable Long caseId,
                                                                    @PathVariable Long supplementItemId,
                                                                    @RequestBody Map<String, Object> data,
                                                                    Authentication auth) {
        checkCaseOwnership(caseId, auth);
        Long submittedBy = (Long) auth.getCredentials();
        return ApiResponse.ok(materialItemService.submitSupplementVersion(supplementItemId, caseId, data, submittedBy));
    }

    @PostMapping("/supplement-versions/{versionId}/review")
    @PreAuthorize("hasAnyRole('REVIEWER', 'NOTARY')")
    public ApiResponse<SupplementVersion> reviewSupplementVersion(@PathVariable Long caseId,
                                                                    @PathVariable Long versionId,
                                                                    @RequestBody Map<String, Object> data,
                                                                    Authentication auth) {
        checkCaseOwnership(caseId, auth);
        Long reviewerId = (Long) auth.getCredentials();
        String reviewResult = (String) data.get("reviewResult");
        return ApiResponse.ok(materialItemService.reviewSupplementVersion(versionId, reviewerId, reviewResult));
    }
}
