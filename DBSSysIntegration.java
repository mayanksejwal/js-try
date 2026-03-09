package com.newgen.DBS.SysIntegration;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

public class DBSSysIntegration {
    private static final String XMLLOG_HISTORY_TABLE = "NG_DBS_XMLLOG_HISTORY";

    public IntegrationBean DBSSys_CifUpdateIntegration(String cabinetName, String sessionID, 
            String jtsIP, String jtsPort, String smsPort, int socket_connection_timeout,
            int integrationWaitTime, Map<String, String> docInfoMap,
            Map<String, String> socketDetailsMap, String wiName, String activityName) {
        
        IntegrationBean responseBean = new IntegrationBean();

        // Build XML for CIF update request
        String cifUpdateInputXML = buildCifUpdateXml(docInfoMap);
        DBSLog.DBSLogger.debug("Input XML for DBS Update: " + cifUpdateInputXML);

        Socket socket = null;
        OutputStream out = null;
        InputStream socketInputStream = null;
        DataOutputStream dout = null;
        DataInputStream din = null;
        
        try {
            // Extract socket server details
            String socketServerIP = socketDetailsMap.get("SocketServerIP");
            int socketServerPort = Integer.parseInt(socketDetailsMap.get("SocketServerPort"));
            DBSLog.DBSLogger.debug("Socket server: " + socketServerIP + ":" + socketServerPort);

            if (socketServerIP == null || socketServerIP.isEmpty() || socketServerPort == 0) {
                responseBean.setFailureResponse("9001", "Invalid socket configuration");
                return responseBean;
            }

            // Establish socket connection
            socket = new Socket(socketServerIP, socketServerPort);
            socket.setSoTimeout(socket_connection_timeout * 1000);
            out = socket.getOutputStream();
            socketInputStream = socket.getInputStream();
            dout = new DataOutputStream(out);
            din = new DataInputStream(socketInputStream);

            // Prepare the request
            String inputRequest = getRequestXML(cabinetName, sessionID, wiName, activityName, 
                    CommonConnection.getUsername(), new StringBuilder(cifUpdateInputXML));
            
            // Send the request
            String outputResponse = sendSocketRequest(dout, din, inputRequest);
            String inputMessageID = outputResponse;
            
            // Get the full response XML
            if (!outputResponse.isEmpty()) {
                outputResponse = getResponseXML(cabinetName, jtsIP, jtsPort, sessionID,
                        wiName, outputResponse, integrationWaitTime);
                outputResponse = formatResponseXml(outputResponse, inputMessageID);
            }
            
            // Parse the response
            XMLParser parser = new XMLParser(outputResponse);
            String returnCode = parser.getValueOf("ReturnCode");
            String returnDesc = parser.getValueOf("ReturnDesc");
            if (returnDesc.trim().isEmpty()) {
                returnDesc = parser.getValueOf("Description");
            }
            
            // Process the return code
            if ("0000".equalsIgnoreCase(returnCode)) {
                responseBean.setSuccessResponse();
                responseBean.setMWErrorCode(returnCode);
                responseBean.setMWErrorDesc(returnDesc);
            } else {
                responseBean.setFailureResponse(returnCode, returnDesc);
            }
            
            DBSLog.DBSLogger.debug("Response XML for DBS CIF Update: " + outputResponse);
            
        } catch (Exception e) {
            DBSLog.DBSLogger.error("Exception in CIF Update: " + e.getMessage(), e);
            responseBean.setFailureResponse("9999", "Exception occurred: " + e.getMessage());
        } finally {
            closeResources(socket, dout, din, out, socketInputStream);
        }
        
        return responseBean;
    }
    
    private String sendSocketRequest(DataOutputStream dout, DataInputStream din, String inputRequest) 
            throws IOException {
        String outputResponse = "";
        
        if (inputRequest != null && !inputRequest.isEmpty()) {
            int inputRequestLen = inputRequest.getBytes("UTF-16LE").length;
            DBSLog.DBSLogger.debug("RequestLen: " + inputRequestLen);
            inputRequest = inputRequestLen + "##8##;" + inputRequest;
            dout.write(inputRequest.getBytes("UTF-16LE"));
            dout.flush();
        }
        
        byte[] readBuffer = new byte[500];
        int num = din.read(readBuffer);
        
        if (num > 0) {
            byte[] arrayBytes = new byte[num];
            System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
            outputResponse = new String(arrayBytes, "UTF-16LE");
            DBSLog.DBSLogger.debug("OutputResponse: " + outputResponse);
        }
        
        return outputResponse;
    }
    
    private String formatResponseXml(String outputResponse, String inputMessageID) {
        if (outputResponse.contains("&lt;")) {
            outputResponse = outputResponse.replaceAll("&lt;", "<");
            outputResponse = outputResponse.replaceAll("&gt;", ">");
        }
        
        return outputResponse.replaceAll("</MessageId>", 
                "</MessageId>\n<InputMessageId>" + inputMessageID + "</InputMessageId>");
    }
    
    private void closeResources(Socket socket, DataOutputStream dout, DataInputStream din,
            OutputStream out, InputStream socketInputStream) {
        try { if (dout != null) dout.close(); } catch (Exception e) {}
        try { if (din != null) din.close(); } catch (Exception e) {}
        try { if (out != null) out.close(); } catch (Exception e) {}
        try { if (socketInputStream != null) socketInputStream.close(); } catch (Exception e) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception e) {}
    }
    
    private String getRequestXML(String cabinetName, String sessionID,
            String wi_name, String ws_name, String userName, StringBuilder final_XML) {
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("<APMQPUTGET_Input>");
        strBuff.append("<SessionId>").append(sessionID).append("</SessionId>");
        strBuff.append("<EngineName>").append(cabinetName).append("</EngineName>");
        strBuff.append("<XMLHISTORY_TABLENAME>").append(XMLLOG_HISTORY_TABLE).append("</XMLHISTORY_TABLENAME>");
        strBuff.append("<WI_NAME>").append(wi_name).append("</WI_NAME>");
        strBuff.append("<WS_NAME>").append(ws_name).append("</WS_NAME>");
        strBuff.append("<USER_NAME>").append(userName).append("</USER_NAME>");
        strBuff.append("<MQ_REQUEST_XML>");
        strBuff.append(final_XML);
        strBuff.append("</MQ_REQUEST_XML>");
        strBuff.append("</APMQPUTGET_Input>");
        
        DBSLog.DBSLogger.debug("GetRequestXML: " + strBuff.toString());
        return strBuff.toString();
    }

    private String getResponseXML(String cabinetName, String sJtsIp, String iJtsPort, 
            String sessionID, String wi_name, String message_ID, int integrationWaitTime) {
        
        String outputResponseXML = "";
        
        try {
            String queryString = "SELECT OUTPUT_XML FROM " + XMLLOG_HISTORY_TABLE + 
                    " WITH (NOLOCK) WHERE MESSAGE_ID ='" + message_ID + 
                    "' AND WI_NAME = '" + wi_name + "'";

            String responseInputXML = CommonMethods.apSelectWithColumnNames(
                    queryString, cabinetName, sessionID);
            
            DBSLog.DBSLogger.debug("Response APSelect InputXML: " + responseInputXML);

            int loopCount = 0;
            do {
                String responseOutputXML = DBSSys.WFNGExecute(responseInputXML, sJtsIp, iJtsPort, 1);
                DBSLog.DBSLogger.debug("Response APSelect OutputXML: " + responseOutputXML);

                XMLParser xmlParserSocketDetails = new XMLParser(responseOutputXML);
                String responseMainCode = xmlParserSocketDetails.getValueOf("MainCode");
                int responseTotalRecords = Integer.parseInt(
                        xmlParserSocketDetails.getValueOf("TotalRetrieved"));
                
                if ("0".equals(responseMainCode) && responseTotalRecords > 0) {
                    String responseXMLData = xmlParserSocketDetails.getNextValueOf("Record")
                            .replaceAll("[ ]+>", ">").replaceAll("<[ ]+", "<");

                    XMLParser xmlParserResponseXMLData = new XMLParser(responseXMLData);
                    outputResponseXML = xmlParserResponseXMLData.getValueOf("OUTPUT_XML");
                    
                    if (outputResponseXML.isEmpty()) {
                        outputResponseXML = "Error";
                    }
                    break;
                }
                
                loopCount++;
                Thread.sleep(1000);
            } while (loopCount < integrationWaitTime);
            
            if (loopCount >= integrationWaitTime) {
                outputResponseXML = "Error: Integration wait time exceeded";
            }

        } catch (Exception e) {
            DBSLog.DBSLogger.error("Exception in getResponseXML: " + e.getMessage(), e);
            outputResponseXML = "Error";
        }
        
        return outputResponseXML;
    }

    private String buildCifUpdateXml(Map<String, String> xmlDataMap) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<EE_EAI_MESSAGE>\n");
        
        // Build EE_EAI_HEADER section
        xmlBuilder.append("\t<EE_EAI_HEADER>\n");
        xmlBuilder.append("\t\t<MsgFormat>CUSTOMER_UPDATE_REQ</MsgFormat>\n");
        xmlBuilder.append("\t\t<MsgVersion>001</MsgVersion>\n");
        xmlBuilder.append("\t\t<RequestorChannelId>BPM</RequestorChannelId>\n");
        xmlBuilder.append("\t\t<RequestorUserId>RAKUSER</RequestorUserId>\n");
        xmlBuilder.append("\t\t<RequestorLanguage>E</RequestorLanguage>\n");
        xmlBuilder.append("\t\t<RequestorSecurityInfo>secure</RequestorSecurityInfo>\n");
        xmlBuilder.append("\t\t<ReturnCode>911</ReturnCode>\n");
        xmlBuilder.append("\t\t<ReturnDesc>Issuer Timed Out</ReturnDesc>\n");
        xmlBuilder.append("\t\t<MessageId>Test_CU_0031</MessageId>\n");
        xmlBuilder.append("\t\t<Extra1>REQ||SHELL.dfgJOHN</Extra1>\n");
        xmlBuilder.append("\t\t<Extra2>YYYY-MM-DDThh:mm:ss.mmm+hh:mm</Extra2>\n");
        xmlBuilder.append("\t</EE_EAI_HEADER>\n");
        xmlBuilder.append("\t<CustomerDetailsUpdateReq>\n");
        xmlBuilder.append("\t\t<BankId>RAK</BankId>\n");
        xmlBuilder.append("\t\t<CIFId>").append(xmlDataMap.get("RCIF")).append("</CIFId>\n");
        xmlBuilder.append("\t\t<RetCorpFlag>R</RetCorpFlag>\n");
        xmlBuilder.append("\t\t<ActionRequired>U</ActionRequired>\n");
        
        // Add any additional customer details tags with prefix "Customer."
        for (Map.Entry<String, String> entry : xmlDataMap.entrySet()) {
            if (entry.getKey().startsWith("Customer.") && !isEmpty(entry.getValue())) {
                String tagName = entry.getKey().substring("Customer.".length());
                xmlBuilder.append("\t\t<").append(tagName).append(">");
                xmlBuilder.append(entry.getValue());
                xmlBuilder.append("</").append(tagName).append(">\n");
            }
        }
           
        // Build DocDet section only if there's at least one document field
        boolean hasDocData = !isEmpty(xmlDataMap.get("DOC_TYPE")) || 
                             !isEmpty(xmlDataMap.get("NEW_DOC_ID")) || 
                             !isEmpty(xmlDataMap.get("NEW_DOC_ISSUE_DATE")) || 
                             !isEmpty(xmlDataMap.get("NEW_DOC_EXP_DATE"));
        
        if (hasDocData) {
            xmlBuilder.append("\t<DocDet>\n");
            
            // Add document type if available
            if (!isEmpty(xmlDataMap.get("DOC_TYPE"))) {
                xmlBuilder.append("\t\t<DocType>").append(xmlDataMap.get("DOC_TYPE")).append("</DocType>\n");
            }
            
            // Always add verification flag
            xmlBuilder.append("\t\t<DocIsVerified>Y</DocIsVerified>\n");
            
            // Add document number if available
            if (!isEmpty(xmlDataMap.get("NEW_DOC_ID"))) {
                xmlBuilder.append("\t\t<DocNo>").append(xmlDataMap.get("NEW_DOC_ID")).append("</DocNo>\n");
            }
            
            // Add issue date if available
            if (!isEmpty(xmlDataMap.get("NEW_DOC_ISSUE_DATE"))) {
                xmlBuilder.append("\t\t<DocIssDate>").append(xmlDataMap.get("NEW_DOC_ISSUE_DATE")).append("</DocIssDate>\n");
            }
            
            // Add expiry date if available
            if (!isEmpty(xmlDataMap.get("NEW_DOC_EXP_DATE"))) {
                xmlBuilder.append("\t\t<DocExpDate>").append(xmlDataMap.get("NEW_DOC_EXP_DATE")).append("</DocExpDate>\n");
            }
             
            xmlBuilder.append("\t</DocDet>\n");
        }
        xmlBuilder.append("\t</CustomerDetailsUpdateReq>\n");
        xmlBuilder.append("</EE_EAI_MESSAGE>");
        DBSLog.DBSLogger.error("CIFUPDATE XML " +  xmlBuilder.toString());
        return xmlBuilder.toString();
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    public String errorMailTrigger(String workitemNo,String errorMsg,String cabinetName,String sessionId,String sJtsIp,
			String iJtsPort) throws IOException, Exception
	{
		String toMailId="";
		String fromMailId="";
		List<String> mailIDs=new ArrayList<String>();
		String DBQuery = "SELECT CONST_FIELD_VALUE from USR_0_BPM_CONSTANTS WITH(NOLOCK) WHERE CONST_FIELD_NAME IN ('PC_FromMailID','SupportMailId')";
					
		String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(DBQuery,CommonConnection.getCabinetName(), CommonConnection.getSessionID(DBSLog.DBSLogger,false));
		DBSLog.DBSLogger.debug("Mail fetch IPXML: "+ extTabDataIPXML);
		String extTabDataOPXML;
		try {
			extTabDataOPXML =  DBSSys.WFNGExecute(extTabDataIPXML,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
			XMLParser xmlParserData= new XMLParser(extTabDataOPXML);
			DBSLog.DBSLogger.debug("Mail fetch "+ extTabDataOPXML);
			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));

			if(xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0")&& iTotalrec>0)
			{
				NGXmlList objWorkList = xmlParserData.createList("Records", "Record");										
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{
					mailIDs.add(objWorkList.getVal("CONST_FIELD_VALUE"));
				}
			}
			toMailId=mailIDs.get(1);
			fromMailId=mailIDs.get(0);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
		Date actionDateTime= new Date();
		String formattedActionDateTime=outputDateFormat.format(actionDateTime);
		DBSLog.DBSLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);
		String columnNames="mailFrom,mailTo,mailCC,mailBCC,mailSubject,mailMessage,mailContentType,attachmentISINDEX,attachmentNames,attachmentExts,mailPriority,mailStatus,statusComments,lockedBy,successTime,LastLockTime,insertedBy,mailActionType,insertedTime,processDefId,processInstanceId,workitemId,activityId,noOfTrials,zipFlag,zipName,maxZipSize,alternateMessage";
		String columnValues="'"+fromMailId+"','"+toMailId+"',NULL,NULL,'DBS WI-"+workitemNo+" moved to error handling','"
				+ "<!DOCTYPE html>"
				+ "<html lang=\\\"en\">"
				+ "<head>"
				+ "<meta charset=\\\"UTF-8\">"
				+"<meta name=\\\"viewport\" content=\\\"width=device-width, initial-scale=1.0\">"
				+"<title>DBS WI Notifucation-System Integration failure</title>"
				+"<style>"
				+"  body {"
				+"    font-family: Arial, sans-serif;"
				+"    line-height: 1.6;"
				+"    margin: 20px;"
				+"  }"
				+"  .email-body {"
				+"    max-width: 600px;"
				+"    margin: 0 auto;"
				+"    padding: 20px;"
				+"    border: 1px solid #ccc;"
				+"    border-radius: 5px;"
				+"    background-color: #f9f9f9;"
				+"  }"
				+"  .disclaimer {"
				+"    font-size: 12px;"
				+"    color: #888;"
				+"    margin-top: 10px;"
				+"  }"
				+"</style>"
				+"</head>"
				+"<body>"
				+"<div class=\\\"email-body\">"
				+"  <p>Dear Team,</p>"
				+"  <p>DBS WI-"+workitemNo+" has been moved to the error handling queue due to the following reason:</p>"
				+"  <p><strong>"+errorMsg+"</strong></p>"
				+"  <p>Regards,<br>RAKBANK</p>"
				+"  <p class=\\\"disclaimer\">This is a system-generated email. Please do not reply to this message.</p>"
				+"</div>"
				+"</body>"
				+"</html>','text/html;charset=UTF-8',NULL,NULL,NULL,1,'N',NULL,NULL,NULL,NULL,'CUSTOM','TRIGGER','"+formattedActionDateTime+"',1,'"+workitemNo+"',1,59,0,NULL,NULL,NULL,NULL";
		String apInsertInputXML=CommonMethods.apInsert(cabinetName, sessionId, columnNames, columnValues,"WFMAILQUEUETABLE");
		DBSLog.DBSLogger.debug("APInsertInputXML: "+apInsertInputXML);

		String apInsertOutputXML =  DBSSys.WFNGExecute(apInsertInputXML,sJtsIp,iJtsPort,1);
		DBSLog.DBSLogger.debug("APInsertOutputXML: "+ apInsertOutputXML);

		XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
		String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
		DBSLog.DBSLogger.debug("Status of apInsertMaincode  "+ apInsertMaincode);
		if(apInsertMaincode.equalsIgnoreCase("0"))
		{
			DBSLog.DBSLogger.debug("ApInsert successful: "+apInsertMaincode);
			DBSLog.DBSLogger.debug("Inserted in WiHistory table successfully.");
		}
		else
		{
			DBSLog.DBSLogger.debug("ApInsert failed: "+apInsertMaincode);
		}
		return "Success";
	}
}