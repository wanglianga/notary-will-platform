package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kinship_relations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KinshipRelation extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "relative_name")
    private String relativeName;

    private String relation;

    @Column(name = "id_number")
    private String idNumber;

    private String phone;

    @Column(name = "is_beneficiary")
    private Boolean isBeneficiary = false;
}
