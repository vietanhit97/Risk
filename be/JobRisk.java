package com.rpa.rpaui.entities;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rpa.rpaui.utils.RpaConstant;
import org.hibernate.annotations.*;
import java.time.LocalDateTime;
import javax.persistence.*;
import javax.persistence.CascadeType;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "job_risks")
@Where(clause = "is_deleted = " + RpaConstant.IS_NOT_DELETE)
public class JobRisk {
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    MRiskPaymentMethod mRiskPaymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amount_category_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    MRiskAmountCategory mRiskAmountCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_data_creation_id")
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonBackReference
    MRiskDataCreation mRiskDataCreation;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "risk_level") 
    private String riskLevelJob;

    @Column(name = "attachment1")
    private String attachment1;
  
    @Column(name = "attachment2")
    private String attachment2;
  
    @Column(name = "manual_file_path1")
    private String manualFilePath1;

    @Column(name = "manual_file_path2")
    private String manualFilePath2;

    @Column(name = "is_deleted")
    private Short isDeleted = RpaConstant.IS_NOT_DELETE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_user")
    private String createdUser;
  
    @Column(name = "updated_user")
    private String updatedUser;

    @Version
    @Column(name = "version")
    private Integer version;
}
