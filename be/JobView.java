package com.rpa.rpaui.modelview;

import lombok.Data;

@Data
public class JobView {
    public Integer status;
    public JobRelatedView jobRelated;
    public JobRiskView jobRisk;
    public Integer department;
    public String departmentNumber;
    public String number;
    public Integer ledgerId;
    public String rpaNumber;
    public String category;
    public String name;
    public String description;
    public String personNumber;
    public JobRpaInfoView jobRpaInfo;
    public JobAnalyzeTimeView jobAnalyzeTime;
    public Integer version;
}
