package com.notary.will.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "identity_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdentityInfo extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false)
    private String name;

    @Column(name = "id_type")
    private String idType;

    @Column(name = "id_number")
    private String idNumber;

    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private String nationality;

    private String address;

    private String phone;
}
