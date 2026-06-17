package com.notary.will.entity;

import com.notary.will.enums.SupplementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplement_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SupplementItem extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "material_type")
    private String materialType;

    @Column(columnDefinition = "CLOB")
    private String description;

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SupplementStatus status = SupplementStatus.PENDING;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "validity_period_days")
    private Integer validityPeriodDays;

    @Column(name = "alternative_materials", columnDefinition = "CLOB")
    private String alternativeMaterials;

    @Column(name = "reservation_retention_days")
    private Integer reservationRetentionDays;

    @Column(name = "reservation_expiry")
    private LocalDateTime reservationExpiry;

    @Column(name = "requires_re_interview", nullable = false)
    private Boolean requiresReInterview = false;
}
