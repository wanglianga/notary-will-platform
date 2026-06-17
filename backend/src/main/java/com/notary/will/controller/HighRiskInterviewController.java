package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.HighRiskInterview;
import com.notary.will.enums.ContinueDecision;
import com.notary.will.service.HighRiskInterviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cases/{caseId}/high-risk-interviews")
public class HighRiskInterviewController extends BaseCaseController {

    @Autowired
    private HighRiskInterviewService highRiskInterviewService;

    @GetMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'REVIEWER')")
    public ApiResponse<List<HighRiskInterview>> getByCaseId(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(highRiskInterviewService.getByCaseId(caseId));
    }

    @GetMapping("/reviewer-view")
    @PreAuthorize("hasRole('REVIEWER')")
    public ApiResponse<HighRiskInterview> getReviewerView(@PathVariable Long caseId, Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(highRiskInterviewService.getReviewerView(caseId));
    }

    @PostMapping
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<HighRiskInterview> create(@PathVariable Long caseId,
                                                  @RequestBody Map<String, Object> data,
                                                  Authentication auth) {
        checkCaseOwnership(caseId, auth);
        HighRiskInterview interview = new HighRiskInterview();
        interview.setCaseId(caseId);
        interview.setNotaryId(getCurrentUserId(auth));
        interview.setTriggerReason(com.notary.will.enums.InterviewTrigger.valueOf((String) data.get("triggerReason")));
        interview.setCompanionName((String) data.get("companionName"));
        interview.setCompanionRelation((String) data.get("companionRelation"));
        interview.setQaSummary((String) data.get("qaSummary"));
        interview.setVideoRecordingNumber((String) data.get("videoRecordingNumber"));
        if (data.get("continueDecision") != null) {
            interview.setContinueDecision(ContinueDecision.valueOf((String) data.get("continueDecision")));
        }
        interview.setContinueReason((String) data.get("continueReason"));
        interview.setNotaryOpinion((String) data.get("notaryOpinion"));
        return ApiResponse.ok(highRiskInterviewService.create(caseId, interview));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<HighRiskInterview> update(@PathVariable Long caseId,
                                                  @PathVariable Long id,
                                                  @RequestBody HighRiskInterview interview,
                                                  Authentication auth) {
        checkCaseOwnership(caseId, auth);
        return ApiResponse.ok(highRiskInterviewService.update(id, interview));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<HighRiskInterview> completeInterview(@PathVariable Long caseId,
                                                             @PathVariable Long id,
                                                             @RequestBody Map<String, Object> data,
                                                             Authentication auth) {
        checkCaseOwnership(caseId, auth);
        ContinueDecision decision = ContinueDecision.valueOf((String) data.get("continueDecision"));
        String qaSummary = (String) data.get("qaSummary");
        String videoRecordingNumber = (String) data.get("videoRecordingNumber");
        return ApiResponse.ok(highRiskInterviewService.completeInterview(id, decision, qaSummary, videoRecordingNumber));
    }
}
