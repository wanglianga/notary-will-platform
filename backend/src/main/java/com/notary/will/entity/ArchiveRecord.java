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

    @Column(name = "archivist_id")
    private Long archivistId;

    private String location;

    @Column(name = "box_number")
    private String boxNumber;

    @Column(name = "paper_materials_complete")
    private Boolean paperMaterialsComplete;

    @Column(name = "electronic_materials_complete")
    private Boolean electronicMaterialsComplete;

    private Boolean sealed;

    @Column(name = "unsealed_by")
    private Long unsealedById;

    @Column(name = "unseal_reason", columnDefinition = "CLOB")
    private String unsealReason;

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
