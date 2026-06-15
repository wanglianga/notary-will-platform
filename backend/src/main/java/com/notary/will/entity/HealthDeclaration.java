package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "health_declarations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HealthDeclaration extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "mental_status")
    private String mentalStatus;

    @Column(name = "chronic_diseases", columnDefinition = "CLOB")
    private String chronicDiseases;

    @Column(name = "medication_info", columnDefinition = "CLOB")
    private String medicationInfo;

    @Column(name = "declaration_date")
    private LocalDate declarationDate;

    @Column(name = "declared_by")
    private String declaredBy;

    @Version
    private Integer version;
}
