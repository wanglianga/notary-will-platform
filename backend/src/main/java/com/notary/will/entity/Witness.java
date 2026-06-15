package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "witnesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Witness extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false)
    private String name;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    private String phone;

    private String relation;

    @Column(name = "declared_no_conflict")
    private Boolean declaredNoConflict = false;
}
