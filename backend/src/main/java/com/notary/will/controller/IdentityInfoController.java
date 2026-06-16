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
public class IdentityInfoController extends BaseCaseController {

    private final IdentityInfoService identityInfoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<IdentityInfo> getIdentityInfo(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(identityInfoService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<IdentityInfo> createOrUpdate(@PathVariable Long caseId,
                                                     @RequestBody IdentityInfo info, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(identityInfoService.createOrUpdate(caseId, info));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<IdentityInfo> updateIdentity(@PathVariable Long caseId,
                                                     @RequestBody IdentityInfo info, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(identityInfoService.createOrUpdate(caseId, info));
    }
}
