package com.rpa.rpaui.modelview;

import lombok.Data;

@Data
public class JobRiskView {
    public Integer id;
    public String riskLevel;
    public String attachment1;
    public String attachment2;
    public String manualFilePath1;
    public String manualFilePath2;
    public Integer amountCategoryId;
    public Integer paymentMethodId;
    public Integer dataCreationId;
    public String pathAttachment1Del;
    public String pathAttachment2Del;
    public Integer version;
}
