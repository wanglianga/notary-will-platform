package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.AppointmentSlot;
import com.notary.will.entity.InterviewRecord;
import com.notary.will.entity.WillCase;
import com.notary.will.entity.WitnessVideo;
import com.notary.will.enums.CaseStatus;
import com.notary.will.service.AppointmentService;
import com.notary.will.service.InterviewRecordService;
import com.notary.will.service.WillCaseService;
import com.notary.will.service.WitnessVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notary")
@RequiredArgsConstructor
public class NotaryController extends BaseCaseController {

    private final WillCaseService willCaseService;
    private final InterviewRecordService interviewRecordService;
    private final WitnessVideoService witnessVideoService;
    private final AppointmentService appointmentService;

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<List<AppointmentSlot>> getNotaryAppointments(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication auth) {
        Long notaryId = getCurrentUserId(auth);
        if (date != null) {
            return ApiResponse.ok(appointmentService.getSlotsByNotaryAndDate(notaryId, date));
        }
        return ApiResponse.ok(appointmentService.getSlotsByNotary(notaryId));
    }

    @GetMapping("/cases")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Map<String, Object>> getNotaryCases(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<WillCase> casePage;

        if (status != null && !status.isEmpty()) {
            casePage = willCaseRepository.findByStatus(CaseStatus.valueOf(status), pageable);
        } else {
            casePage = willCaseRepository.findByStatusIn(
                    List.of(CaseStatus.REVIEW_PASSED, CaseStatus.APPOINTMENT_SCHEDULED,
                            CaseStatus.INTERVIEWING, CaseStatus.WITNESS_SIGNING),
                    pageable);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", casePage.getContent());
        result.put("total", casePage.getTotalElements());
        result.put("stats", getNotaryStats());

        return ApiResponse.ok(result);
    }

    private Map<String, Long> getNotaryStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("pending", willCaseRepository.countByStatus(CaseStatus.REVIEW_PASSED));
        stats.put("appointed", willCaseRepository.countByStatus(CaseStatus.APPOINTMENT_SCHEDULED));
        stats.put("interviewing", willCaseRepository.countByStatus(CaseStatus.INTERVIEWING));
        stats.put("completed", willCaseRepository.countByStatus(CaseStatus.WITNESS_SIGNING));
        return stats;
    }

    @GetMapping("/cases/{caseId}")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<Map<String, Object>> getCaseDetail(@PathVariable Long caseId, Authentication auth) {
        WillCase willCase = getCaseAndCheckAccess(caseId, auth);

        Map<String, Object> result = new HashMap<>();
        result.put("caseInfo", willCase);
        result.put("identity", willCaseService.getIdentityInfo(caseId));
        result.put("properties", willCaseService.getPropertyInventory(caseId));
        result.put("kinship", willCaseService.getKinshipRelations(caseId));
        result.put("health", willCaseService.getHealthDeclaration(caseId));
        result.put("beneficiaries", willCaseService.getBeneficiaries(caseId));
        result.put("witnesses", willCaseService.getWitnesses(caseId));
        result.put("interviewRecords", interviewRecordService.getByCaseId(caseId));
        result.put("videos", witnessVideoService.getByCaseId(caseId));

        return ApiResponse.ok(result);
    }

    @PostMapping("/cases/{caseId}/interview")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<InterviewRecord> createInterviewRecord(@PathVariable Long caseId,
                                                               @RequestBody Map<String, Object> interviewData,
                                                               Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        InterviewRecord record = new InterviewRecord();
        record.setCaseId(caseId);
        record.setInterviewerId(getCurrentUserId(auth));
        record.setInterviewContent((String) interviewData.get("content"));
        record.setRiskWarnings((String) interviewData.get("riskWarnings"));
        record.setApplicantUnderstanding((Boolean) interviewData.getOrDefault("applicantUnderstanding", true));
        record.setIsFreeWill((Boolean) interviewData.getOrDefault("isFreeWill", true));
        record.setNoDuress((Boolean) interviewData.getOrDefault("noDuress", true));
        record.setNoFraud((Boolean) interviewData.getOrDefault("noFraud", true));
        record.setSignedByApplicant((Boolean) interviewData.getOrDefault("signedByApplicant", false));

        willCaseService.updateStatus(caseId, CaseStatus.INTERVIEWING, getCurrentUserId(auth));

        return ApiResponse.ok(interviewRecordService.create(caseId, record));
    }

    @PutMapping("/cases/{caseId}/risk")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<InterviewRecord> updateRiskAssessment(@PathVariable Long caseId,
                                                              @RequestBody Map<String, Object> riskData,
                                                              Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        List<InterviewRecord> records = interviewRecordService.getByCaseId(caseId);
        if (records.isEmpty()) {
            throw new com.notary.will.exception.BusinessException("请先创建谈话笔录");
        }

        InterviewRecord record = records.get(records.size() - 1);
        record.setRiskWarnings((String) riskData.get("riskWarnings"));
        record.setRiskLevel((String) riskData.getOrDefault("riskLevel", "NORMAL"));

        return ApiResponse.ok(interviewRecordService.update(record.getId(), record));
    }

    @PostMapping("/cases/{caseId}/witness-signing")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<WillCase> confirmWitnessSigning(@PathVariable Long caseId,
                                                        @RequestBody Map<String, Object> signingData,
                                                        Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        willCaseService.updateStatus(caseId, CaseStatus.WITNESS_SIGNING, getCurrentUserId(auth));

        return ApiResponse.ok(willCaseService.getCaseById(caseId));
    }

    @PostMapping("/cases/{caseId}/video")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<WitnessVideo> uploadVideoMetadata(@PathVariable Long caseId,
                                                          @RequestBody Map<String, Object> videoData,
                                                          Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        WitnessVideo video = new WitnessVideo();
        video.setCaseId(caseId);
        video.setVideoType((String) videoData.getOrDefault("videoType", "WITNESS"));
        video.setFileName((String) videoData.get("fileName"));
        video.setFilePath((String) videoData.get("filePath"));
        video.setFileSize(Long.valueOf(videoData.getOrDefault("fileSize", 0).toString()));
        video.setDuration(Integer.valueOf(videoData.getOrDefault("duration", 0).toString()));
        video.setRecordedById(getCurrentUserId(auth));

        return ApiResponse.ok(witnessVideoService.create(caseId, video));
    }

    @PostMapping("/cases/{caseId}/signoff")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<WillCase> signOffCase(@PathVariable Long caseId,
                                              @RequestBody Map<String, Object> signoffData,
                                              Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        WillCase willCase = willCaseService.getCaseById(caseId);
        willCase.setNotaryId(getCurrentUserId(auth));
        willCaseRepository.save(willCase);

        willCaseService.updateStatus(caseId, CaseStatus.WITNESS_SIGNING, getCurrentUserId(auth));

        return ApiResponse.ok(willCase);
    }

    @PostMapping("/slots")
    @PreAuthorize("hasRole('NOTARY')")
    public ApiResponse<AppointmentSlot> manageSlots(@RequestBody Map<String, Object> slotData,
                                                     Authentication auth) {
        AppointmentSlot slot = new AppointmentSlot();
        slot.setNotaryId(getCurrentUserId(auth));
        slot.setDate(LocalDate.parse((String) slotData.get("date")));
        slot.setStartTime(java.time.LocalTime.parse((String) slotData.get("startTime")));
        slot.setEndTime(java.time.LocalTime.parse((String) slotData.get("endTime")));

        return ApiResponse.ok(appointmentService.createSlot(slot));
    }
}
