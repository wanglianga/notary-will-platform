package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.Witness;
import com.notary.will.service.WitnessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/witnesses")
public class WitnessController extends BaseCaseController {

    @Autowired
    private WitnessService witnessService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<Witness>> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(witnessService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<Witness> create(@PathVariable Long caseId,
                                        @RequestBody Witness witness,
                                        Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(witnessService.create(caseId, witness));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<Witness> update(@PathVariable Long caseId,
                                        @PathVariable Long id,
                                        @RequestBody Witness witness,
                                        Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(witnessService.update(id, witness));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long caseId,
                                     @PathVariable Long id,
                                     Authentication auth) {
        checkCaseOwnership(caseId, auth);
        witnessService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
