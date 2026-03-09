package com.newgen.DBS.SysIntegration;

public class IntegrationBean {
    private String wiName;
    // Response fields
    private String cifUpdateReturnCode;
    private String integrationDecision = "Failure"; // Default value
    private String mwErrorCode = "9999"; // Default value
    private String mwErrorDesc = "Integration call failed"; // Default value

    // Customer getters and setters
    public String getWiName() { return wiName; }
    public void setWiName(String wiName) { 
        this.wiName = (wiName != null) ? wiName : ""; 
    }
    
   
    
    // Response getters and setters
    public String getIntegrationDecision() { return integrationDecision; }
    public void setIntegrationDecision(String integrationDecision) { 
        this.integrationDecision = (integrationDecision != null) ? integrationDecision : ""; 
    }
    
    public String getCifUpdateReturnCode() { return cifUpdateReturnCode; }
    public void setCifUpdateReturnCode(String cifUpdateReturnCode) { 
        this.cifUpdateReturnCode = (cifUpdateReturnCode != null) ? cifUpdateReturnCode : ""; 
    }
    
    public String getMWErrorCode() { return mwErrorCode; }
    public void setMWErrorCode(String mwErrorCode) { 
        this.mwErrorCode = (mwErrorCode != null) ? mwErrorCode : "9999"; 
    }
    
    public String getMWErrorDesc() { return mwErrorDesc; }
    public void setMWErrorDesc(String mwErrorDesc) { 
        this.mwErrorDesc = (mwErrorDesc != null) ? mwErrorDesc : "Integration call failed"; 
    }
    
    // Helper methods
    public void setSuccessResponse() {
        this.setCifUpdateReturnCode("Success");
        this.setIntegrationDecision("Success");
    }
    
    public void setFailureResponse(String errorCode, String errorDesc) {
        this.setCifUpdateReturnCode("Failure");
        this.setIntegrationDecision("Failure");
        this.setMWErrorCode(errorCode);
        this.setMWErrorDesc(errorDesc);
    }
    

}