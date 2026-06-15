package com.notary.will.service;

import com.notary.will.entity.FeeRecord;
import com.notary.will.enums.FeeStatus;
import com.notary.will.exception.BusinessException;
import com.notary.will.repository.FeeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeRecordRepository feeRecordRepository;
    private final WillCaseService willCaseService;

    @Transactional
    public FeeRecord createFee(Long caseId, FeeRecord fee) {
        willCaseService.getCaseById(caseId);
        fee.setCaseId(caseId);
        fee.setStatus(FeeStatus.UNPAID);
        return feeRecordRepository.save(fee);
    }

    public List<FeeRecord> getFeesByCaseId(Long caseId) {
        willCaseService.getCaseById(caseId);
        return feeRecordRepository.findByCaseId(caseId);
    }

    public FeeRecord getById(Long id) {
        return feeRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException("费用记录不存在"));
    }

    @Transactional
    public FeeRecord payFee(Long id, Long paidBy) {
        FeeRecord fee = getById(id);
        if (fee.getStatus() != FeeStatus.UNPAID) {
            throw new BusinessException("该费用已支付");
        }
        fee.setStatus(FeeStatus.PAID);
        fee.setPaidAt(LocalDateTime.now());
        fee.setPaidBy(paidBy);
        fee.setReceiptNumber(generateReceiptNumber());
        return feeRecordRepository.save(fee);
    }

    @Transactional
    public FeeRecord refundFee(Long id) {
        FeeRecord fee = getById(id);
        if (fee.getStatus() != FeeStatus.PAID) {
            throw new BusinessException("只能退已支付的费用");
        }
        fee.setStatus(FeeStatus.REFUNDED);
        return feeRecordRepository.save(fee);
    }

    public BigDecimal calculateTotalFees(Long caseId) {
        List<FeeRecord> fees = feeRecordRepository.findByCaseId(caseId);
        return fees.stream()
                .filter(f -> f.getStatus() == FeeStatus.PAID)
                .map(FeeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<FeeRecord> getUnpaidFees(Long caseId) {
        return feeRecordRepository.findByCaseIdAndStatus(caseId, FeeStatus.UNPAID);
    }

    private String generateReceiptNumber() {
        return "RCP" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%03d", (int) (Math.random() * 1000));
    }
}
