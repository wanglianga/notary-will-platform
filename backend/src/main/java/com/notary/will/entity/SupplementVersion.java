package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplement_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SupplementVersion extends BaseEntity {

    @Column(name = "supplement_item_id", nullable = false)
    private Long supplementItemId;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "material_type")
    private String materialType;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "description", columnDefinition = "CLOB")
    private String description;

    @Column(name = "change_summary", columnDefinition = "CLOB")
    private String changeSummary;

    @Column(name = "submitted_by")
    private Long submittedBy;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "review_result")
    private String reviewResult;
}
