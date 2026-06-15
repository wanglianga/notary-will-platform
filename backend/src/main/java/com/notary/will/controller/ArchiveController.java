package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.ArchiveRecord;
import com.notary.will.service.ArchiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cases/{caseId}/archive")
@RequiredArgsConstructor
public class ArchiveController {

    private final ArchiveService archiveService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<ArchiveRecord> getArchive(@PathVariable Long caseId) {
        return ApiResponse.ok(archiveService.getByCaseId(caseId));
    }

    @PostMapping("/seal")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<ArchiveRecord> seal(@PathVariable Long caseId,
                                            @RequestParam String physicalLocation,
                                            @RequestParam String electronicPath,
                                            Authentication auth) {
        Long sealedBy = (Long) auth.getCredentials();
        return ApiResponse.ok(archiveService.seal(caseId, sealedBy, physicalLocation, electronicPath));
    }

    @PostMapping("/unseal-request")
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER')")
    public ApiResponse<ArchiveRecord> requestUnseal(@PathVariable Long caseId) {
        return ApiResponse.ok(archiveService.requestUnseal(caseId));
    }

    @PostMapping("/unseal")
    @PreAuthorize("hasRole('ARCHIVIST')")
    public ApiResponse<ArchiveRecord> unseal(@PathVariable Long caseId) {
        return ApiResponse.ok(archiveService.unseal(caseId));
    }
}
