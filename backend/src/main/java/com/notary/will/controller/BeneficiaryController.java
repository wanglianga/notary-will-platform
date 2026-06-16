package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.Beneficiary;
import com.notary.will.service.BeneficiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/beneficiaries")
public class BeneficiaryController extends BaseCaseController {

    @Autowired
    private BeneficiaryService beneficiaryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<Beneficiary>> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(beneficiaryService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<Beneficiary> create(@PathVariable Long caseId,
                                            @RequestBody Beneficiary beneficiary,
                                            Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(beneficiaryService.create(caseId, beneficiary));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<Beneficiary> update(@PathVariable Long caseId,
                                            @PathVariable Long id,
                                            @RequestBody Beneficiary beneficiary,
                                            Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(beneficiaryService.update(id, beneficiary));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long caseId,
                                     @PathVariable Long id,
                                     Authentication auth) {
        checkCaseOwnership(caseId, auth);
        beneficiaryService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
