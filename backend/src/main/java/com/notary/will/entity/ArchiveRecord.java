package com.notary.will.entity;

import com.notary.will.enums.ArchiveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "archive_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ArchiveRecord extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "sealed_by")
    private Long sealedBy;

    @Column(name = "sealed_at")
    private LocalDateTime sealedAt;

    @Column(name = "physical_location")
    private String physicalLocation;

    @Column(name = "electronic_path")
    private String electronicPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArchiveStatus status = ArchiveStatus.PENDING;
}
