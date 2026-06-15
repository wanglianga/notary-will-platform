package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.HealthDeclaration;
import com.notary.will.service.HealthDeclarationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases/{caseId}/health")
@RequiredArgsConstructor
public class HealthDeclarationController {

    private final HealthDeclarationService healthDeclarationService;
    private final com.notary.will.service.WillCaseService willCaseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<HealthDeclaration> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkAccess(caseId, auth);
        return ApiResponse.ok(healthDeclarationService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<HealthDeclaration> createOrUpdate(@PathVariable Long caseId,
                                                          @RequestBody HealthDeclaration declaration,
                                                          Authentication auth) {
        checkAccess(caseId, auth);
        return ApiResponse.ok(healthDeclarationService.createOrUpdate(caseId, declaration));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<HealthDeclaration> update(@PathVariable Long caseId,
                                                  @RequestBody HealthDeclaration declaration,
                                                  Authentication auth) {
        checkAccess(caseId, auth);
        return ApiResponse.ok(healthDeclarationService.createOrUpdate(caseId, declaration));
    }

    private void checkAccess(Long caseId, Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        if ("APPLICANT".equals(role)) {
            Long userId = (Long) auth.getCredentials();
            var willCase = willCaseService.getCaseById(caseId);
            if (!willCase.getApplicantId().equals(userId)) {
                throw new org.springframework.security.access.AccessDeniedException("无权限访问");
            }
        }
    }
}
