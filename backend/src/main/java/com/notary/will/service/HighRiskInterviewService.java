package com.notary.will.service;

import com.notary.will.entity.HighRiskInterview;
import com.notary.will.enums.ContinueDecision;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.HighRiskInterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HighRiskInterviewService {

    private final HighRiskInterviewRepository highRiskInterviewRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public HighRiskInterview create(Long caseId, HighRiskInterview interview) {
        willCaseService.getCaseById(caseId);
        interview.setCaseId(caseId);
        interview.setInterviewStartTime(LocalDateTime.now());
        return highRiskInterviewRepository.save(interview);
    }

    public List<HighRiskInterview> getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return highRiskInterviewRepository.findByCaseIdOrderByCreatedAtDesc(caseId);
    }

    public HighRiskInterview getById(Long id) {
        return highRiskInterviewRepository.findById(id)
                .orElseThrow(() -> new BusinessException("高风险谈话记录不存在"));
    }

    @Transactional
    public HighRiskInterview update(Long id, HighRiskInterview update) {
        HighRiskInterview interview = getById(id);
        if (update.getCompanionAvoidanceStart() != null) interview.setCompanionAvoidanceStart(update.getCompanionAvoidanceStart());
        if (update.getCompanionAvoidanceEnd() != null) interview.setCompanionAvoidanceEnd(update.getCompanionAvoidanceEnd());
        if (update.getQaSummary() != null) interview.setQaSummary(update.getQaSummary());
        if (update.getVideoRecordingNumber() != null) interview.setVideoRecordingNumber(update.getVideoRecordingNumber());
        if (update.getContinueDecision() != null) interview.setContinueDecision(update.getContinueDecision());
        if (update.getContinueReason() != null) interview.setContinueReason(update.getContinueReason());
        if (update.getNotaryOpinion() != null) interview.setNotaryOpinion(update.getNotaryOpinion());
        return highRiskInterviewRepository.save(interview);
    }

    @Transactional
    public HighRiskInterview completeInterview(Long id, ContinueDecision decision, String qaSummary, String videoRecordingNumber) {
        HighRiskInterview interview = getById(id);
        interview.setContinueDecision(decision);
        interview.setQaSummary(qaSummary);
        interview.setVideoRecordingNumber(videoRecordingNumber);
        interview.setInterviewEndTime(LocalDateTime.now());
        if (interview.getCompanionAvoidanceStart() != null) {
            interview.setCompanionAvoidanceEnd(LocalDateTime.now());
        }
        return highRiskInterviewRepository.save(interview);
    }

    public HighRiskInterview getReviewerView(Long caseId) {
        List<HighRiskInterview> interviews = getByCaseId(caseId);
        if (interviews.isEmpty()) return null;
        HighRiskInterview interview = interviews.get(0);
        HighRiskInterview view = new HighRiskInterview();
        view.setId(interview.getId());
        view.setCaseId(interview.getCaseId());
        view.setTriggerReason(interview.getTriggerReason());
        view.setContinueDecision(interview.getContinueDecision());
        view.setVideoRecordingNumber(interview.getVideoRecordingNumber());
        return view;
    }
}
