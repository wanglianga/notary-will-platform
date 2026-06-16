package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.FeeRecord;
import com.notary.will.entity.WillCase;
import com.notary.will.enums.CaseStatus;
import com.notary.will.enums.FeeStatus;
import com.notary.will.repository.FeeRecordRepository;
import com.notary.will.service.FeeService;
import com.notary.will.service.WillCaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cashier")
@RequiredArgsConstructor
public class CashierController extends BaseCaseController {

    private final WillCaseService willCaseService;
    private final FeeService feeService;
    private final FeeRecordRepository feeRecordRepository;

    @GetMapping("/cases/{caseId}/fees")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<List<FeeRecord>> getFeeList(@PathVariable Long caseId, Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);
        return ApiResponse.ok(feeRecordRepository.findByCaseId(caseId));
    }

    @PostMapping("/cases/{caseId}/fees")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<FeeRecord> addFee(@PathVariable Long caseId,
                                          @RequestBody Map<String, Object> feeData,
                                          Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        FeeRecord fee = new FeeRecord();
        fee.setCaseId(caseId);
        fee.setFeeType((String) feeData.getOrDefault("feeType", "NOTARY_FEE"));
        fee.setAmount(new BigDecimal(feeData.get("amount").toString()));
        fee.setStatus(FeeStatus.UNPAID);
        fee.setPaidBy(getCurrentUserId(auth));

        return ApiResponse.ok(feeService.createFee(caseId, fee));
    }

    @PostMapping("/cases/{caseId}/payment")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<FeeRecord> recordPayment(@PathVariable Long caseId,
                                                 @RequestBody Map<String, Object> paymentData,
                                                 Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        Long feeId = Long.valueOf(paymentData.get("feeId").toString());
        FeeRecord fee = feeService.payFee(feeId, getCurrentUserId(auth));

        willCaseService.updateStatus(caseId, CaseStatus.CLOSED, getCurrentUserId(auth));

        return ApiResponse.ok(fee);
    }

    @PostMapping("/cases/{caseId}/refund")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<FeeRecord> refundPayment(@PathVariable Long caseId,
                                                 @RequestBody Map<String, Object> refundData,
                                                 Authentication auth) {
        getCaseAndCheckAccess(caseId, auth);

        Long feeId = Long.valueOf(refundData.get("feeId").toString());
        FeeRecord fee = feeService.refundFee(feeId);

        return ApiResponse.ok(fee);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<Map<String, Object>> getPaymentStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication auth) {

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPaid", feeRecordRepository.sumByStatus(FeeStatus.PAID));
        stats.put("totalUnpaid", feeRecordRepository.sumByStatus(FeeStatus.UNPAID));
        stats.put("totalRefunded", feeRecordRepository.sumByStatus(FeeStatus.REFUNDED));
        stats.put("paidCount", feeRecordRepository.countByStatus(FeeStatus.PAID));
        stats.put("unpaidCount", feeRecordRepository.countByStatus(FeeStatus.UNPAID));

        return ApiResponse.ok(stats);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<Map<String, Object>> getPendingPayments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication auth) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<FeeRecord> feePage = feeRecordRepository.findByStatus(FeeStatus.UNPAID, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("list", feePage.getContent());
        result.put("total", feePage.getTotalElements());

        return ApiResponse.ok(result);
    }
}
