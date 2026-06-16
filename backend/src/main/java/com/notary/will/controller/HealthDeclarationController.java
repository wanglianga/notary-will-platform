package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.HealthDeclaration;
import com.notary.will.service.HealthDeclarationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases/{caseId}/health")
public class HealthDeclarationController extends BaseCaseController {

    @Autowired
    private HealthDeclarationService healthDeclarationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<HealthDeclaration> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(healthDeclarationService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<HealthDeclaration> createOrUpdate(@PathVariable Long caseId,
                                                          @RequestBody HealthDeclaration declaration,
                                                          Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(healthDeclarationService.createOrUpdate(caseId, declaration));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<HealthDeclaration> update(@PathVariable Long caseId,
                                                  @RequestBody HealthDeclaration declaration,
                                                  Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(healthDeclarationService.createOrUpdate(caseId, declaration));
    }
}
