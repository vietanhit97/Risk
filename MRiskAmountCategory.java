package com.rpa.rpaui.entities;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rpa.rpaui.utils.RpaConstant;
import org.hibernate.annotations.*;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.*;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "m_risk_amount_category")
@Where(clause = "is_deleted = " + RpaConstant.IS_NOT_DELETE)
public class MRiskAmountCategory {
    @OneToMany(mappedBy = "mRiskAmountCategory")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    List<JobRisk> risks;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String amountCategoryName;

    @Column(name = "risk_level")
    private String riskLevel;

    @Column(name = "is_deleted")
    private Short isDeleted = RpaConstant.IS_NOT_DELETE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
