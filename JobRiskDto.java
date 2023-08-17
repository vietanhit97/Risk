package com.rpa.rpaui.controllers.dtos;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class JobRiskDto {
    private String riskLevelJob;
    private String attachment1;
    private String attachment2;
    private String manualFilePath1;
    private String manualFilePath2;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdUser;
    private String updatedUser;
    private Integer version;
}
