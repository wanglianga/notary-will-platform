package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MaterialReview extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "material_type", nullable = false)
    private String materialType;

    @Column(name = "review_result", nullable = false)
    private String reviewResult;

    @Column(name = "review_comment", columnDefinition = "CLOB")
    private String reviewComment;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "is_valid", nullable = false)
    private Boolean isValid = true;
}
