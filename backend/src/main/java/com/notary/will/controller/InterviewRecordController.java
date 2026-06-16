package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.InterviewRecord;
import com.notary.will.service.InterviewRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/interviews")
public class InterviewRecordController extends BaseCaseController {

    @Autowired
    private InterviewRecordService interviewRecordService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER', 'ARCHIVIST')")
    public ApiResponse<List<InterviewRecord>> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(interviewRecordService.getByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<InterviewRecord> create(@PathVariable Long caseId,
                                                @RequestBody InterviewRecord record,
                                                Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(interviewRecordService.create(caseId, record));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<InterviewRecord> update(@PathVariable Long caseId,
                                                @PathVariable Long id,
                                                @RequestBody InterviewRecord record,
                                                Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(interviewRecordService.update(id, record));
    }

    @GetMapping("/{id}/applicant-view")
    @PreAuthorize("hasRole('APPLICANT')")
    public ApiResponse<InterviewRecord> getApplicantView(@PathVariable Long caseId,
                                                          @PathVariable Long id,
                                                          Authentication auth) {
        checkCaseOwnership(caseId, auth);
        InterviewRecord record = interviewRecordService.getById(id);
        return ApiResponse.ok(record);
    }
}
