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

    @Column(name = "interviewer_id")
    private Long interviewerId;

    @Column(name = "interview_content", columnDefinition = "CLOB")
    private String interviewContent;

    @Column(name = "risk_warnings", columnDefinition = "CLOB")
    private String riskWarnings;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "applicant_understanding")
    private Boolean applicantUnderstanding;

    @Column(name = "is_free_will")
    private Boolean isFreeWill;

    @Column(name = "no_duress")
    private Boolean noDuress;

    @Column(name = "no_fraud")
    private Boolean noFraud;

    @Version
    private Integer version;
}
