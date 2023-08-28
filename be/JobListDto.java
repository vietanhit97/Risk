package com.rpa.rpaui.controllers.dtos;

import lombok.Data;

@Data
public class JobListDto {
  Integer id;
  String number;
  String name;
  String rpaNumber;
  String departmentName;
  String category;
  String personName;
  String jobRisk;
  String consigneeName;
  String jobStatusesName;
  Integer ledgerId;
  String updatedAt;
}
