package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "beneficiaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Beneficiary extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false)
    private String name;

    private String relation;

    @Column(name = "id_number")
    private String idNumber;

    private String phone;

    private BigDecimal share;

    @Column(columnDefinition = "CLOB")
    private String remarks;
}
