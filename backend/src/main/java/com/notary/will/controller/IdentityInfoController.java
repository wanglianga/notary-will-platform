package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.IdentityInfo;
import com.notary.will.service.IdentityInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases/{caseId}/identity")
@RequiredArgsConstructor
public class IdentityInfoController {

    private final IdentityInfoService identityInfoService;
    private final com.notary.will.service.WillCaseService willCaseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<IdentityInfo> getIdentityInfo(@PathVariable Long caseId, Authentication auth) {
        checkAccess(caseId, auth);
        return ApiResponse.ok(identityInfoService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<IdentityInfo> createOrUpdate(@PathVariable Long caseId,
                                                     @RequestBody IdentityInfo info, Authentication auth) {
        checkAccess(caseId, auth);
        return ApiResponse.ok(identityInfoService.createOrUpdate(caseId, info));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<IdentityInfo> updateIdentity(@PathVariable Long caseId,
                                                     @RequestBody IdentityInfo info, Authentication auth) {
        checkAccess(caseId, auth);
        return ApiResponse.ok(identityInfoService.createOrUpdate(caseId, info));
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
