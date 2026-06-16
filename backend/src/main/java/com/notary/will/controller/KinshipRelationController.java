package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.KinshipRelation;
import com.notary.will.service.KinshipRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/kinship")
public class KinshipRelationController extends BaseCaseController {

    @Autowired
    private KinshipRelationService kinshipRelationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<KinshipRelation>> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(kinshipRelationService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<KinshipRelation> create(@PathVariable Long caseId,
                                                @RequestBody KinshipRelation relation,
                                                Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(kinshipRelationService.create(caseId, relation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<KinshipRelation> update(@PathVariable Long caseId,
                                                @PathVariable Long id,
                                                @RequestBody KinshipRelation relation,
                                                Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(kinshipRelationService.update(id, relation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long caseId,
                                     @PathVariable Long id,
                                     Authentication auth) {
        checkCaseOwnership(caseId, auth);
        kinshipRelationService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
