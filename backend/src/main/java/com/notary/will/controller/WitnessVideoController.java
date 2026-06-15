package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.WitnessVideo;
import com.notary.will.service.WitnessVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/videos")
@RequiredArgsConstructor
public class WitnessVideoController {

    private final WitnessVideoService witnessVideoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<WitnessVideo>> getByCaseId(@PathVariable Long caseId) {
        return ApiResponse.ok(witnessVideoService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<WitnessVideo> create(@PathVariable Long caseId,
                                             @RequestBody WitnessVideo video) {
        return ApiResponse.ok(witnessVideoService.create(caseId, video));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        witnessVideoService.delete(id);
        return ApiResponse.ok("删除成功", null);
    }
}
