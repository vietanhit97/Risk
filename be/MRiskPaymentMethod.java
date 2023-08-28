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
import javax.persistence.CascadeType;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "m_risk_payment_method")
@Where(clause = "is_deleted = " + RpaConstant.IS_NOT_DELETE)
public class MRiskPaymentMethod {
    @OneToMany(mappedBy = "mRiskPaymentMethod", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    List<JobRisk> jobRisks;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String paymentMethodName;

    @Column(name = "is_deleted")
    private Short isDeleted = RpaConstant.IS_NOT_DELETE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
