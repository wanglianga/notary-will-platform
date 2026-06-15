package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.InterviewRecord;
import com.notary.will.service.InterviewRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/interviews")
@RequiredArgsConstructor
public class InterviewRecordController {

    private final InterviewRecordService interviewRecordService;
    private final com.notary.will.service.WillCaseService willCaseService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER')")
    public ApiResponse<List<InterviewRecord>> getByCaseId(@PathVariable Long caseId) {
        return ApiResponse.ok(interviewRecordService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<InterviewRecord> create(@PathVariable Long caseId,
                                                @RequestBody InterviewRecord record) {
        return ApiResponse.ok(interviewRecordService.create(caseId, record));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<InterviewRecord> update(@PathVariable Long id,
                                                @RequestBody InterviewRecord record) {
        return ApiResponse.ok(interviewRecordService.update(id, record));
    }

    @GetMapping("/{id}/applicant-view")
    @PreAuthorize("hasRole('APPLICANT')")
    public ApiResponse<InterviewRecord> getApplicantView(@PathVariable Long caseId,
                                                          @PathVariable Long id,
                                                          Authentication auth) {
        Long userId = (Long) auth.getCredentials();
        var willCase = willCaseService.getCaseById(caseId);
        if (!willCase.getApplicantId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("无权限访问");
        }
        InterviewRecord record = interviewRecordService.getById(id);
        return ApiResponse.ok(record);
    }
}
