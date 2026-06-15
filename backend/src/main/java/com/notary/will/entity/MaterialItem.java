package com.notary.will.entity;

import com.notary.will.enums.MaterialStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "material_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MaterialItem extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    private String type;

    @Column(nullable = false)
    private String name;

    @Column(name = "file_url")
    private String fileUrl;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaterialStatus status = MaterialStatus.PENDING;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "CLOB")
    private String comments;
}
