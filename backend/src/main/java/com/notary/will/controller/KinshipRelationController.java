package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.KinshipRelation;
import com.notary.will.service.KinshipRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/kinship")
@RequiredArgsConstructor
public class KinshipRelationController {

    private final KinshipRelationService kinshipRelationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<KinshipRelation>> getByCaseId(@PathVariable Long caseId) {
        return ApiResponse.ok(kinshipRelationService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<KinshipRelation> create(@PathVariable Long caseId,
                                                @RequestBody KinshipRelation relation) {
        return ApiResponse.ok(kinshipRelationService.create(caseId, relation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<KinshipRelation> update(@PathVariable Long id,
                                                @RequestBody KinshipRelation relation) {
        return ApiResponse.ok(kinshipRelationService.update(id, relation));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        kinshipRelationService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
