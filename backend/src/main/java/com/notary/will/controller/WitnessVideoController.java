package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.WitnessVideo;
import com.notary.will.service.WitnessVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/videos")
public class WitnessVideoController extends BaseCaseController {

    @Autowired
    private WitnessVideoService witnessVideoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<WitnessVideo>> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(witnessVideoService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<WitnessVideo> create(@PathVariable Long caseId,
                                             @RequestBody WitnessVideo video,
                                             Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(witnessVideoService.create(caseId, video));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long caseId,
                                     @PathVariable Long id,
                                     Authentication auth) {
        checkCaseOwnership(caseId, auth);
        witnessVideoService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
