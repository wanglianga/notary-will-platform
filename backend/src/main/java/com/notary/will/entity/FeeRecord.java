package com.notary.will.entity;

import com.notary.will.enums.FeeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fee_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class FeeRecord extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "fee_type")
    private String feeType;

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "paid_by")
    private Long paidBy;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeeStatus status = FeeStatus.UNPAID;

    @Column(name = "is_re_interview_fee", nullable = false)
    private Boolean isReInterviewFee = false;

    @Column(name = "re_interview_reason", columnDefinition = "CLOB")
    private String reInterviewReason;
}
