package com.notary.will.service;

import com.notary.will.entity.InterviewRecord;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.InterviewRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewRecordService {

    private final InterviewRecordRepository interviewRecordRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public InterviewRecord create(Long caseId, InterviewRecord record) {
        willCaseService.getCaseById(caseId);
        record.setCaseId(caseId);
        return interviewRecordRepository.save(record);
    }

    public List<InterviewRecord> getByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return interviewRecordRepository.findByCaseId(caseId);
    }

    public InterviewRecord getById(Long id) {
        return interviewRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("面谈记录不存在"));
    }

    @Transactional
    public InterviewRecord update(Long id, InterviewRecord update) {
        InterviewRecord record = getById(id);
        if (update.getContent() != null) record.setContent(update.getContent());
        if (update.getRiskAlert() != null) record.setRiskAlert(update.getRiskAlert());
        if (update.getRiskAlertContent() != null) record.setRiskAlertContent(update.getRiskAlertContent());
        if (update.getSignedByApplicant() != null) record.setSignedByApplicant(update.getSignedByApplicant());
        if (update.getInterviewDate() != null) record.setInterviewDate(update.getInterviewDate());
        return interviewRecordRepository.save(record);
    }
}
