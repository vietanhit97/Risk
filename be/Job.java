package com.rpa.rpaui.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.rpa.rpaui.utils.RpaConstant;
import lombok.Data;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table(name = "jobs")
@Where(clause = "is_deleted = " + RpaConstant.IS_NOT_DELETE)
public class Job {
  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  LedgersLink ledgersLink;

  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  @NotFound(action = NotFoundAction.IGNORE)
  JobRisk jobRisk;

  @OneToOne()
  @JoinColumn(name = "department")
  @NotFound(action = NotFoundAction.IGNORE)
  MDepartment mdepartment;

  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  @NotFound(action = NotFoundAction.IGNORE)
  JobRpaInfo jobRpaInfo;

  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  @NotFound(action = NotFoundAction.IGNORE)
  JobAnalyzeTime jobAnalyzeTime;

  @OneToOne(mappedBy = "job", cascade = CascadeType.ALL)
  @NotFound(action = NotFoundAction.IGNORE)
  JobRelated jobRelated;

  @OneToOne()
  @JoinColumn(name = "status")
  @NotFound(action = NotFoundAction.IGNORE)
  MJobStatuse mJobStatuse;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "number")
  private String number;

  @Column(name = "ledger_id")
  private Integer ledgerId;

  @Column(name = "name")
  private String name;
  //  @OneToOne()
  //  @JoinColumn(name = "category")
  //  @NotFound(action = NotFoundAction.IGNORE)
  //  MCategory mCategory;
  @Column(name = "category")
  private String category;

  @Column(name = "description")
  private String description;

  @Column(name = "is_deleted")
  private Short isDeleted = RpaConstant.IS_NOT_DELETE;

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "created_user")
  private String createdUser;

  @Column(name = "updated_user")
  private String updatedUser;

  @Version
  @Column(name = "version")
  private Integer version;

  @Override
  public String toString() {
    return "";
  }
}
