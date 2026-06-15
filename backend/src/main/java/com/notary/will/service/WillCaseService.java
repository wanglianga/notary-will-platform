package com.notary.will.service;

import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.WillCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WillCaseService {

    private final WillCaseRepository willCaseRepository;

    private static final Map<CaseStatus, List<CaseStatus>> STATUS_TRANSITIONS = new ConcurrentHashMap<>();

    static {
        STATUS_TRANSITIONS.put(CaseStatus.DRAFT, List.of(CaseStatus.SUBMITTED));
        STATUS_TRANSITIONS.put(CaseStatus.SUBMITTED, List.of(CaseStatus.UNDER_REVIEW));
        STATUS_TRANSITIONS.put(CaseStatus.UNDER_REVIEW, List.of(CaseStatus.REVIEW_PASSED, CaseStatus.REVIEW_FAILED));
        STATUS_TRANSITIONS.put(CaseStatus.REVIEW_FAILED, List.of(CaseStatus.DRAFT, CaseStatus.SUBMITTED));
        STATUS_TRANSITIONS.put(CaseStatus.REVIEW_PASSED, List.of(CaseStatus.APPOINTMENT_SCHEDULED));
        STATUS_TRANSITIONS.put(CaseStatus.APPOINTMENT_SCHEDULED, List.of(CaseStatus.INTERVIEWING));
        STATUS_TRANSITIONS.put(CaseStatus.INTERVIEWING, List.of(CaseStatus.WITNESS_SIGNING));
        STATUS_TRANSITIONS.put(CaseStatus.WITNESS_SIGNING, List.of(CaseStatus.ARCHIVED));
        STATUS_TRANSITIONS.put(CaseStatus.ARCHIVED, List.of(CaseStatus.CLOSED));
    }

    @Transactional
    public WillCase createCase(WillCase willCase) {
        willCase.setCaseNumber(generateCaseNumber());
        willCase.setStatus(CaseStatus.DRAFT);
        WillCase saved = willCaseRepository.save(willCase);
        return saved;
    }

    public WillCase getCaseById(Long id) {
        return willCaseRepository.findById(id)
                .orElseThrow(() -> new BusinessException("案件不存在"));
    }

    public WillCase getCaseByCaseNumber(String caseNumber) {
        return willCaseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new BusinessException("案件不存在"));
    }

    public List<WillCase> getCasesByApplicantId(Long applicantId) {
        return willCaseRepository.findByApplicantId(applicantId);
    }

    public List<WillCase> getCasesByNotaryId(Long notaryId) {
        return willCaseRepository.findByNotaryId(notaryId);
    }

    public List<WillCase> getCasesByStatus(CaseStatus status) {
        return willCaseRepository.findByStatus(status);
    }

    public List<WillCase> getAllCases() {
        return willCaseRepository.findAll();
    }

    @Transactional
    public WillCase updateCase(Long id, WillCase update) {
        WillCase willCase = getCaseById(id);
        if (update.getNotaryId() != null) willCase.setNotaryId(update.getNotaryId());
        if (update.getReviewerId() != null) willCase.setReviewerId(update.getReviewerId());
        return willCaseRepository.save(willCase);
    }

    @Transactional
    public WillCase transitionStatus(Long id, CaseStatus newStatus) {
        WillCase willCase = getCaseById(id);
        CaseStatus currentStatus = willCase.getStatus();

        if (currentStatus == newStatus) {
            return willCase;
        }

        List<CaseStatus> allowedTransitions = STATUS_TRANSITIONS.get(currentStatus);
        if (allowedTransitions == null || !allowedTransitions.contains(newStatus)) {
            throw new BusinessException(
                    String.format("不允许从 %s 转换到 %s", currentStatus, newStatus));
        }

        willCase.setStatus(newStatus);
        return willCaseRepository.save(willCase);
    }

    @Transactional
    public void deleteCase(Long id) {
        willCaseRepository.deleteById(id);
    }

    private String generateCaseNumber() {
        return "WC" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", (int) (Math.random() * 1000));
    }
}
