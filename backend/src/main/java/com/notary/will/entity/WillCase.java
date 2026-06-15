package com.notary.will.entity;

import com.notary.will.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "will_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WillCase extends BaseEntity {

    @Column(name = "case_number", nullable = false, unique = true)
    private String caseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status = CaseStatus.DRAFT;

    @Column(name = "applicant_id")
    private Long applicantId;

    @Column(name = "notary_id")
    private Long notaryId;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    private String title;

    @Column(name = "will_type")
    private String willType;

    @Column(columnDefinition = "CLOB")
    private String remarks;
}
