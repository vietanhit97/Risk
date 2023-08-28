package com.rpa.rpaui.utils;

import com.rpa.rpaui.entities.MRiskAmountCategory;

public enum RpaMessage {
  USER_PASS_INVALID("invalid_user_pass"),
  EXEC_DAY_NOT_FOUND("exec_day_not_found"),
  INTERVAL_NOT_FOUND("interval_not_found"),
  DEPARTMENT_NOT_FOUND("department_not_found"),
  LEDGER_NOT_FOUND("ledger_not_found"),
  HOLIDAY_NOT_FOUND("holiday_not_found"),
  HOLIDAY_EXISTS("holiday_exists"),
  RPA_SPEC_NOT_FOUND("rpa_spec_not_found"),
  UPLOAD_FILE_EXISTS("upload_file_exists"),
  USER_NOT_FOUND("user_not_found"),
  JOB_NOT_FOUND("job_not_found"),
  COMMON_NOT_FOUND("common_not_found"),
  PROVIDE_FROM_NOT_FOUND("provide_from_not_found"),
  PROVIDE_TO_NOT_FOUND("provide_to_not_found"),
  MPROVIDE_METHOD("mprovide_method_not_found"),
  MPERSONALIZEDS("mpersonalized_not_found"),
  MPROVIDE_TO_DETAIL("m_provide_to_details_not_found"),
  FOLDER_NOT_EXISTS("folder_not_exists"),
  UPLOAD_MAX_SIZE("upload_max_size"),
  CATEGORY_NOT_FOUND("category_not_found"),
  MPERSON_NOT_FOUND("mperson_not_found"),
  MCONSIGN_NOT_FOUND("mconsign_not_found"),
  MCONSIGNEE_NOT_FOUND("mconsignee_not_found"),
  MJOBSTATUSE_NOT_FOUND("m_job_statuses_not_found"),
  MSTATUSES_NOT_FOUND("m_statuses_not_found"),
  MJOBINTERVAL_NOT_FOUND("m_job_intervals_not_found"),
  PERSON_STATUS_NOT_FOUND("m_person_status_not_found"),
  PERSON_OPTIMISTIC_LOCK("m_person_optimistic_lock"),
  AMOUNTCATEGORY_NOT_FOUND("amoutcategory_not_found"),
  DATACREATION_NOT_FOUND("datacreation_not_found"),
  PAYMENTMETHOD_NOT_FOUND("paymentmethod_not_found"),
  JOB_RISK_NOT_FOUND("jobrisk_not_found"),
  END_EXPIRY_DATE("end_expiry_date");
  private final String label;

  private RpaMessage(String label) {
    this.label = label;
  }

  public String val() {
    return label;
  }
}
