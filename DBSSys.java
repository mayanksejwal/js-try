package com.newgen.DBS.SysIntegration;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;

public class DBSSys implements Runnable {
    private static final String DBS_WIHISTORY = "USR_0_DBS_WIHISTORY";
    private static final String DBS_EXTTABLE = "RB_DBS_EXTTABLE";
    private static final String WS_NAME = "Sys_Integration";
    
    private static NGEjbClient ngEjbClientDBSSys;
    private static Map<String, String> configParamMap = new HashMap<>();
    private DBSSysIntegration integrationService = new DBSSysIntegration();

    @Override
    public void run() {
        DBSLog.setLogger();
        DBSLog.DBSLogger.info("Starting DBSSys utility");
        
        try {
            // Initialize EJB client
            ngEjbClientDBSSys = NGEjbClient.getSharedInstance();
            
            // Read configuration
            if (readConfig() != 0) {
                DBSLog.DBSLogger.error("Failed to read configuration");
                return;
            }
            
            // Get connection parameters
            String cabinetName = CommonConnection.getCabinetName();
            String jtsIP = CommonConnection.getJTSIP();
            String jtsPort = CommonConnection.getJTSPort();
            String smsPort = CommonConnection.getsSMSPort();
            String queueID = configParamMap.get("queueID");
            int integrationWaitTime = Integer.parseInt(configParamMap.get("INTEGRATION_WAIT_TIME"));
            int sleepIntervalInMin = Integer.parseInt(configParamMap.get("SleepIntervalInMin"));
            int socket_connection_timeout = Integer.parseInt(
                    configParamMap.getOrDefault("socket_connection_timeout", "5"));
            
            logConnectionParams(cabinetName, jtsIP, jtsPort, smsPort, queueID, 
                    integrationWaitTime, sleepIntervalInMin);
            
            // Main processing loop
            while (true) {
                String sessionID = CommonConnection.getSessionID(DBSLog.DBSLogger, false);
                
                if (sessionID == null || sessionID.isEmpty()) {
                    DBSLog.DBSLogger.error("Could not connect to server - invalid session ID");
                } else {
                    HashMap<String, String> socketDetailsMap = getSocketConnectionDetails(
                            cabinetName, jtsIP, jtsPort, sessionID);
                    
                    DBSLog.setLogger();
                    DBSLog.DBSLogger.info("Processing DBS integration queue");
                    
                    processSysIntegrationQueue(cabinetName, jtsIP, jtsPort, smsPort, queueID,
                            sleepIntervalInMin, integrationWaitTime, sessionID, 
                            socketDetailsMap, socket_connection_timeout);
                }
                
                Thread.sleep(sleepIntervalInMin * 60 * 1000);
            }
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Exception in DBSSys thread: " + e.getMessage(), e);
        }
    }
    
    private void logConnectionParams(String cabinetName, String jtsIP, String jtsPort, 
            String smsPort, String queueID, int integrationWaitTime, int sleepIntervalInMin) {
        DBSLog.DBSLogger.debug("Cabinet Name: " + cabinetName);
        DBSLog.DBSLogger.debug("JTS IP: " + jtsIP);
        DBSLog.DBSLogger.debug("JTS Port: " + jtsPort);
        DBSLog.DBSLogger.debug("SMS Port: " + smsPort);
        DBSLog.DBSLogger.debug("Queue ID: " + queueID);
        DBSLog.DBSLogger.debug("Integration Wait Time: " + integrationWaitTime);
        DBSLog.DBSLogger.debug("Sleep Interval (min): " + sleepIntervalInMin);
    }
    
    private int readConfig() {
        try {
            String configPath = System.getProperty("user.dir") + File.separator + 
                    "ConfigFiles" + File.separator + "DBS_SysConfig.properties";
            
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream(new File(configPath))) {
                properties.load(fis);
            }
            
            Enumeration<?> names = properties.propertyNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                configParamMap.put(name, properties.getProperty(name));
            }
            
            return 0;
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Error reading configuration: " + e.getMessage(), e);
            return -1;
        }
    }
    
    public static HashMap<String, String> getSocketConnectionDetails(String cabinetName, 
            String sJtsIp, String iJtsPort, String sessionID) {
        HashMap<String, String> socketDetailsMap = new HashMap<>();
        
        try {
            DBSLog.DBSLogger.debug("Fetching Socket Connection Details");
            
            String query = "SELECT SocketServerIP, SocketServerPort FROM NG_BPM_MQ_TABLE " +
                    "WITH (NOLOCK) WHERE ProcessName = 'DBS' AND CallingSource = 'Utility'";
            
            String inputXML = CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
            String outputXML = WFNGExecute(inputXML, sJtsIp, iJtsPort, 1);
            
            XMLParser parser = new XMLParser(outputXML);
            String mainCode = parser.getValueOf("MainCode");
            int totalRecords = Integer.parseInt(parser.getValueOf("TotalRetrieved"));
            
            if ("0".equals(mainCode) && totalRecords > 0) {
                String recordData = parser.getNextValueOf("Record")
                        .replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");
                
                XMLParser recordParser = new XMLParser(recordData);
                
                socketDetailsMap.put("SocketServerIP", recordParser.getValueOf("SocketServerIP"));
                socketDetailsMap.put("SocketServerPort", recordParser.getValueOf("SocketServerPort"));
                
                DBSLog.DBSLogger.debug("Socket server details found: IP=" + 
                        socketDetailsMap.get("SocketServerIP") + ", Port=" + 
                        socketDetailsMap.get("SocketServerPort"));
            }
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Error fetching socket details: " + e.getMessage(), e);
        }
        
        return socketDetailsMap;
    }
    
    private void processSysIntegrationQueue(String cabinetName, String jtsIP, String jtsPort, 
            String smsPort, String queueId, int sleepIntervalTime, int integrationWaitTime, 
            String sessionID, Map<String, String> socketDetailsMap, int socket_connection_timeout) {
        
        try {
            // Fetch workitems from the queue
            DBSLog.DBSLogger.debug("Fetching workitems from Core System Update queue");
            String fetchWorkitemListInputXML = CommonMethods.fetchWorkItemsInput(cabinetName, sessionID, queueId);
            String fetchWorkitemListOutputXML = WFNGExecute(fetchWorkitemListInputXML, jtsIP, jtsPort, 1);
            
            XMLParser xmlParser = new XMLParser(fetchWorkitemListOutputXML);
            String mainCode = xmlParser.getValueOf("MainCode");
            int workitemCount = Integer.parseInt(xmlParser.getValueOf("RetrievedCount"));
            
            DBSLog.DBSLogger.debug("Found " + workitemCount + " workitems in the queue");
            
            if ("0".equals(mainCode) && workitemCount > 0) {
                for (int i = 0; i < workitemCount; i++) {
                    String workitemData = xmlParser.getNextValueOf("Instrument")
                            .replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");
                    
                    processWorkItem(cabinetName, jtsIP, jtsPort, smsPort, queueId, 
                            sleepIntervalTime, integrationWaitTime, sessionID, 
                            workitemData, socketDetailsMap, socket_connection_timeout);
                }
            }
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Error processing queue: " + e.getMessage(), e);
        }
    }
    
    private void processWorkItem(String cabinetName, String jtsIP, String jtsPort, String smsPort,
            String queueId, int sleepIntervalTime, int integrationWaitTime, String sessionID, 
            String workitemData, Map<String, String> socketDetailsMap, int socket_connection_timeout) 
            throws Exception {
        
        XMLParser workItemParser = new XMLParser(workitemData);
        DBSLog.DBSLogger.debug("workItemParser " + workItemParser);
        
        // Extract workitem details
        String processInstanceID = workItemParser.getValueOf("ProcessInstanceId");
        String workitemID = workItemParser.getValueOf("WorkItemId");
        String activityName = workItemParser.getValueOf("ActivityName");
        String entryDateTime = workItemParser.getValueOf("EntryDateTime");
        
        DBSLog.DBSLogger.debug("Processing workitem: " + processInstanceID);
        System.out.println("Processing workitem: " + processInstanceID);
        
        // Get document info map from the bean
        Map<String, String> docInfoMap = getDocumentInfo(cabinetName, jtsIP, 
                 jtsPort,  sessionID,  processInstanceID);
        
        String integrationErrorReceived = docInfoMap.get("INTEGRATION_ERROR_RECEIVED");
        DBSLog.DBSLogger.debug("Workitem data " + docInfoMap);
        if (!"Success".equalsIgnoreCase(integrationErrorReceived)) {
            // Attempt integration
            DBSLog.DBSLogger.debug("Attempting integration for workitem " + processInstanceID);
            
            IntegrationBean response = integrationService.DBSSys_CifUpdateIntegration(
                    cabinetName, sessionID, jtsIP, jtsPort, smsPort, socket_connection_timeout, 
                    integrationWaitTime, docInfoMap, socketDetailsMap, 
                    processInstanceID, activityName);
            
            String integrationCode = response.getCifUpdateReturnCode();
           // String mwErrorCode = response.getMWErrorCode();
            String mwErrorDesc = response.getMWErrorDesc();
            String integrationRemarks;
            
            if ("Success".equals(integrationCode)) {
                integrationRemarks = "CIF Update Successful";
            } else if ("CIF_UnderVerification".equals(integrationCode)) {
                integrationCode = "Failure";
                integrationRemarks = "CIF UNDER VERIFICATION";
                integrationService.errorMailTrigger(processInstanceID, integrationRemarks,cabinetName, sessionID,jtsIP,jtsPort);
            } else {
                integrationCode = "Failure";
                integrationRemarks = "Error in CIF Update: " + mwErrorDesc;
                integrationService.errorMailTrigger(processInstanceID, integrationRemarks,cabinetName, sessionID,jtsIP,jtsPort);
            }
            
            // Complete the workitem with integration results
            completeWorkItem(
                    cabinetName, jtsIP, jtsPort, smsPort, sessionID, processInstanceID, workitemID, 
                    integrationCode, integrationRemarks, entryDateTime);
        } else {
            DBSLog.DBSLogger.debug("Workitem " + processInstanceID + 
                    " already processed successfully, skipping");
        }
    }
    
   private Map<String, String> getDocumentInfo(String cabinetName, String jtsIP, 
            String jtsPort, String sessionID, String processInstanceID) throws Exception {
        
        Map<String, String> docInfoMap = new HashMap<>();
        
        String query = "SELECT WINAME, RCIF, ISNULL(INTEGRATION_ERROR_RECEIVED,'') as INTEGRATION_ERROR_RECEIVED, " +
                "ISNULL(DOC_TYPE,'') as DOC_TYPE, ISNULL(NEW_DOC_ID,'')as NEW_DOC_ID, " +
                "ISNULL(NEW_DOC_ISSUE_DATE,'') as NEW_DOC_ISSUE_DATE, ISNULL(NEW_DOC_EXP_DATE,'')as NEW_DOC_EXP_DATE " +
                "FROM " + DBS_EXTTABLE + " WITH (NOLOCK) WHERE WINAME='" + processInstanceID + "'";
      
        String inputXML = CommonMethods.apSelectWithColumnNames(query, cabinetName, sessionID);
        String outputXML = WFNGExecute(inputXML, jtsIP, jtsPort, 1);
        
        XMLParser parser = new XMLParser(outputXML);
        String mainCode = parser.getValueOf("MainCode");
        int totalRecords = Integer.parseInt(parser.getValueOf("TotalRetrieved"));
        
        if ("0".equals(mainCode) && totalRecords > 0) {
            String recordData = parser.getNextValueOf("Record");
            
            docInfoMap.put("WINAME", parser.getValueOf("WINAME"));
            docInfoMap.put("RCIF", parser.getValueOf("RCIF"));
            docInfoMap.put("INTEGRATION_ERROR_RECEIVED", parser.getValueOf("INTEGRATION_ERROR_RECEIVED"));
            docInfoMap.put("DOC_TYPE", parser.getValueOf("DOC_TYPE"));
            docInfoMap.put("NEW_DOC_ID", parser.getValueOf("NEW_DOC_ID"));
            docInfoMap.put("NEW_DOC_ISSUE_DATE", DateConverter(parser.getValueOf("NEW_DOC_ISSUE_DATE")));
            docInfoMap.put("NEW_DOC_EXP_DATE", DateConverter(parser.getValueOf("NEW_DOC_EXP_DATE")));
            //docInfoMap.put("NEW_DOC_ISSUE_DATE", parser.getValueOf("NEW_DOC_ISSUE_DATE"));
            //docInfoMap.put("NEW_DOC_EXP_DATE",parser.getValueOf("NEW_DOC_EXP_DATE"));
            
        }
        DBSLog.DBSLogger.debug("docInfoMap "+docInfoMap);
        return docInfoMap;
       
    }
	    
    private void completeWorkItem(String cabinetName, String jtsIP, String jtsPort, String smsPort, 
            String sessionID, String processInstanceID, String workitemID, String decision, 
            String remarks, String entryDateTime) throws Exception {
    	// Create attributes tag and complete the workitem
    	 String attributesTag = "<Decision>" + decision + "</Decision>" +
                 "<Remarks>" + remarks + "</Remarks>" +
                 "<INTEGRATION_ERROR_RECEIVED>" + decision + "</INTEGRATION_ERROR_RECEIVED>";
    	// Get workitem
        String getWorkItemInputXML = CommonMethods.getWorkItemInput(
                cabinetName, sessionID, processInstanceID, workitemID);
        String getWorkItemOutputXml = WFNGExecute(getWorkItemInputXML, jtsIP, jtsPort, 1);
        
        XMLParser parser = new XMLParser(getWorkItemOutputXml);
        String getWorkItemMainCode = parser.getValueOf("MainCode");
        
        if (!"0".equals(getWorkItemMainCode)) {
            DBSLog.DBSLogger.error("Error retrieving workitem " + processInstanceID);
            return;
        }
        
        // Assign attributes
        String assignAttributesInputXML = CommonMethods.assignWorkitemAttributeInput(
                cabinetName, sessionID, processInstanceID, workitemID, attributesTag);
        String assignAttributesOutputXML = WFNGExecute(assignAttributesInputXML, jtsIP, jtsPort, 1);
        
        XMLParser assignParser = new XMLParser(assignAttributesOutputXML);
        String assignMainCode = assignParser.getValueOf("MainCode");
        
        if (!"0".equals(assignMainCode)) {
            DBSLog.DBSLogger.error("Error assigning attributes to workitem " + processInstanceID);
            return;
        }
        
        // Complete workitem
        String completeWorkItemInputXML = CommonMethods.completeWorkItemInput(
                cabinetName, sessionID, processInstanceID, workitemID);
        String completeWorkItemOutputXML = WFNGExecute(completeWorkItemInputXML, jtsIP, jtsPort, 1);
        
        XMLParser completeParser = new XMLParser(completeWorkItemOutputXML);
        String completeMainCode = completeParser.getValueOf("MainCode");
        
        if ("0".equals(completeMainCode)) {
            DBSLog.DBSLogger.info("Successfully completed workitem " + processInstanceID);
            
            // Add to history
            addToHistory(cabinetName, jtsIP, jtsPort, sessionID, 
                    processInstanceID, decision, remarks, entryDateTime);
        } else {
            DBSLog.DBSLogger.error("Error completing workitem " + processInstanceID);
        }
    }
    
    private void addToHistory(String cabinetName, String jtsIP, String jtsPort, 
            String sessionID, String processInstanceID, String decision, 
            String remarks, String entryDateTime) throws Exception {
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            
            Date entryDatetimeFormat = dateFormat.parse(entryDateTime);
            String formattedEntryDatetime = dateFormat.format(entryDatetimeFormat);
            
            Date actionDateTime = new Date();
            String formattedActionDateTime = dateFormat.format(actionDateTime);
            
            String columnNames = "WINAME,ACTION_DATE_TIME,WORKSTEP,USER_NAME,DECISION,ENTRY_DATE_TIME,REMARKS";
            String columnValues = "'" + processInstanceID + "','" + formattedActionDateTime + "','" + 
                    WS_NAME + "','" + CommonConnection.getUsername() + "','" + decision + "','" + 
                    formattedEntryDatetime + "','" + remarks + "'";
            
            String insertInputXML = CommonMethods.apInsert(
                    cabinetName, sessionID, columnNames, columnValues, DBS_WIHISTORY);
            String insertOutputXML = WFNGExecute(insertInputXML, jtsIP, jtsPort, 1);
            
            XMLParser parser = new XMLParser(insertOutputXML);
            String insertMainCode = parser.getValueOf("MainCode");
            
            if ("0".equals(insertMainCode)) {
                DBSLog.DBSLogger.debug("Successfully added to history table");
            } else {
                DBSLog.DBSLogger.error("Error adding to history table");
            }
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Error in addToHistory: " + e.getMessage(), e);
        }
    }
    
    public static String WFNGExecute(String ipXML, String jtsServerIP, 
            String serverPort, int flag) throws Exception {
        try {
            if (serverPort.startsWith("33")) {
                return WFCallBroker.execute(ipXML, jtsServerIP, Integer.parseInt(serverPort), 1);
            } else {
                return ngEjbClientDBSSys.makeCall(jtsServerIP, serverPort, "WebSphere", ipXML);
            }
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Exception in WFNGExecute: " + e.getMessage(), e);
            throw e;
        }
    }
    public String DateConverter(String inputDate) {
        if (inputDate == null || inputDate.trim().isEmpty()) {
            return "";
        }
        
        try {
            String cleanedDate = inputDate.trim();
            
            // Handle different input formats
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            
            // Try parsing with timestamp first
            if (cleanedDate.contains(" ")) {
                try {
                    DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime dateTime = LocalDateTime.parse(cleanedDate, inputFormat);
                    return dateTime.toLocalDate().format(outputFormat);
                } catch (Exception e) {
                    // If timestamp parsing fails, try date-only format
                    cleanedDate = cleanedDate.split(" ")[0];
                }
            }
            
            // Parse as date-only format
            LocalDate date = LocalDate.parse(cleanedDate, outputFormat);
            return date.format(outputFormat);
            
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Error converting date: " + inputDate + " - " + e.getMessage());
            return ""; // Return empty string for invalid dates
        }
    }
}

