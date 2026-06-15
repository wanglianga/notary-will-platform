package com.notary.will.entity;

import com.notary.will.enums.PropertyType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "property_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PropertyInventory extends BaseEntity {

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType type;

    @Column(name = "prop_value", precision = 18, scale = 2)
    private BigDecimal value;

    private String address;

    @Column(precision = 18, scale = 2)
    private BigDecimal area;

    @Column(name = "certificate_no")
    private String certificateNo;

    private String bank;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "share_ratio", precision = 10, scale = 4)
    private BigDecimal shareRatio;

    @Column(columnDefinition = "CLOB")
    private String description;

    @Column(columnDefinition = "CLOB")
    private String remarks;

    @Version
    private Integer version;
}
