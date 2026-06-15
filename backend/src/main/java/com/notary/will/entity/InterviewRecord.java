package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "interview_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InterviewRecord extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "notary_id")
    private Long notaryId;

    @Column(name = "interview_date")
    private LocalDate interviewDate;

    @Column(columnDefinition = "CLOB")
    private String content;

    @Column(name = "risk_alert")
    private Boolean riskAlert = false;

    @Column(name = "risk_alert_content", columnDefinition = "CLOB")
    private String riskAlertContent;

    @Column(name = "signed_by_applicant")
    private Boolean signedByApplicant = false;

    @Version
    private Integer version;
}
