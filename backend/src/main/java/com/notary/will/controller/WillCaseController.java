package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.AppointmentSlot;
import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import com.notary.will.service.AppointmentService;
import com.notary.will.service.WillCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class WillCaseController {

    private final WillCaseService willCaseService;
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<WillCase> createCase(@RequestBody WillCase willCase, Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        if ("APPLICANT".equals(role)) {
            willCase.setApplicantId(getUserId(auth));
        }
        return ApiResponse.ok(willCaseService.createCase(willCase));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST', 'CASHIER')")
    public ApiResponse<List<WillCase>> getAllCases() {
        return ApiResponse.ok(willCaseService.getAllCases());
    }

    @GetMapping("/{id}")
    public ApiResponse<WillCase> getCase(@PathVariable Long id, Authentication auth) {
        WillCase willCase = willCaseService.getCaseById(id);
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        if ("APPLICANT".equals(role) && !willCase.getApplicantId().equals(getUserId(auth))) {
            throw new org.springframework.security.access.AccessDeniedException("无权限查看该案件");
        }
        return ApiResponse.ok(willCase);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('APPLICANT')")
    public ApiResponse<List<WillCase>> getMyCases(Authentication auth) {
        return ApiResponse.ok(willCaseService.getCasesByApplicantId(getUserId(auth)));
    }

    @GetMapping("/notary/{notaryId}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<List<WillCase>> getCasesByNotary(@PathVariable Long notaryId) {
        return ApiResponse.ok(willCaseService.getCasesByNotaryId(notaryId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<WillCase>> getCasesByStatus(@PathVariable CaseStatus status) {
        return ApiResponse.ok(willCaseService.getCasesByStatus(status));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER')")
    public ApiResponse<WillCase> updateCase(@PathVariable Long id, @RequestBody WillCase willCase) {
        return ApiResponse.ok(willCaseService.updateCase(id, willCase));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<WillCase> transitionStatus(@PathVariable Long id, @RequestParam CaseStatus status) {
        return ApiResponse.ok(willCaseService.transitionStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Void> deleteCase(@PathVariable Long id) {
        willCaseService.deleteCase(id);
        return ApiResponse.ok("删除成功", null);
    }

    @GetMapping("/{caseId}/appointment/slots")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'REVIEWER')")
    public ApiResponse<List<AppointmentSlot>> getAvailableSlotsForCase(
            @PathVariable Long caseId,
            @RequestParam(required = false) Long notaryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (notaryId != null && date != null) {
            return ApiResponse.ok(appointmentService.getAvailableSlots(notaryId, date));
        }
        return ApiResponse.ok(appointmentService.getAvailableSlots());
    }

    @PostMapping("/{caseId}/appointment")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY')")
    public ApiResponse<AppointmentSlot> bookAppointmentForCase(@PathVariable Long caseId,
                                                                @RequestBody Map<String, Object> request) {
        Long slotId = Long.valueOf(request.get("slotId").toString());
        return ApiResponse.ok(appointmentService.bookSlot(slotId, caseId));
    }

    private Long getUserId(Authentication auth) {
        return (Long) auth.getCredentials();
    }
}
