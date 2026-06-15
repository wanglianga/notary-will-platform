package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "witness_videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WitnessVideo extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "video_url")
    private String videoUrl;

    private Integer duration;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "recorded_by")
    private String recordedBy;
}
