package com.notary.will.controller;

import com.notary.will.dto.ApiResponse;
import com.notary.will.entity.FeeRecord;
import com.notary.will.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cases/{caseId}/fees")
@RequiredArgsConstructor
public class FeeController {

    private final FeeService feeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'CASHIER')")
    public ApiResponse<List<FeeRecord>> getFees(@PathVariable Long caseId) {
        return ApiResponse.ok(feeService.getFeesByCaseId(caseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('NOTARY', 'CASHIER')")
    public ApiResponse<FeeRecord> createFee(@PathVariable Long caseId,
                                             @RequestBody FeeRecord fee) {
        return ApiResponse.ok(feeService.createFee(caseId, fee));
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("hasAnyRole('APPLICANT', 'CASHIER')")
    public ApiResponse<FeeRecord> payFee(@PathVariable Long id, Authentication auth) {
        Long paidBy = (Long) auth.getCredentials();
        return ApiResponse.ok(feeService.payFee(id, paidBy));
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<FeeRecord> refundFee(@PathVariable Long id) {
        return ApiResponse.ok(feeService.refundFee(id));
    }

    @GetMapping("/total")
    @PreAuthorize("hasAnyRole('APPLICANT', 'NOTARY', 'CASHIER')")
    public ApiResponse<BigDecimal> getTotalFees(@PathVariable Long caseId) {
        return ApiResponse.ok(feeService.calculateTotalFees(caseId));
    }

    @GetMapping("/unpaid")
    @PreAuthorize("hasAnyRole('APPLICANT', 'CASHIER')")
    public ApiResponse<List<FeeRecord>> getUnpaidFees(@PathVariable Long caseId) {
        return ApiResponse.ok(feeService.getUnpaidFees(caseId));
    }
}
