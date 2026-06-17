package com.notary.will.entity;

import com.notary.will.enums.ContinueDecision;
import com.notary.will.enums.InterviewTrigger;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "high_risk_interviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HighRiskInterview extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "notary_id")
    private Long notaryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "trigger_reason", nullable = false)
    private InterviewTrigger triggerReason;

    @Column(name = "companion_name")
    private String companionName;

    @Column(name = "companion_relation")
    private String companionRelation;

    @Column(name = "companion_avoidance_start")
    private LocalDateTime companionAvoidanceStart;

    @Column(name = "companion_avoidance_end")
    private LocalDateTime companionAvoidanceEnd;

    @Column(name = "qa_summary", columnDefinition = "CLOB")
    private String qaSummary;

    @Column(name = "video_recording_number")
    private String videoRecordingNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "continue_decision")
    private ContinueDecision continueDecision;

    @Column(name = "continue_reason", columnDefinition = "CLOB")
    private String continueReason;

    @Column(name = "interview_start_time")
    private LocalDateTime interviewStartTime;

    @Column(name = "interview_end_time")
    private LocalDateTime interviewEndTime;

    @Column(name = "notary_opinion", columnDefinition = "CLOB")
    private String notaryOpinion;
}
