package com.newgen.iforms.user;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;

import com.newgen.iforms.custom.IFormReference;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;
public class DBS_InfobipIntegration extends DBSCommon {

	public String commsTrigger(IFormReference iformObj,String controlId,String paramInfo) throws IOException {
		DBS.mLogger.debug("Inside MailTriggering Method");
		String Mailquery=""; 
		String SMSquery="";
		String MailID=""; 
		String SMS_MOBNO="";
		int ProccessDefID=0;
		String TemplateID="";
		String Stage="";
		String from_mailid="";
		String mail_subject="";
		String mail_template="";
		String sms_template="";
	//	String sms_placeholder="";
		String mail_placeholder="";
		String customer_name ="";
		String defaultMailCC="";
		String dbReferenceNo="";
		String awbNo="";
		String branch="";
		String emirates="";
		String infoBipAlert="";
		String rCIF="";
		String cCIF="";
		String dynamicValues="";
		String infobipPlaceholder="";
		String ext_ref_no="";
		String ref_no="";
		// Removed unused variables that caused strict compilation failures

		String paramArray[]=paramInfo.split("~");
		if(paramArray.length>0)
		{
			Stage=paramArray[0];
			TemplateID=paramArray[1];
		}
		else
		{
			DBS.mLogger.debug("Data-"+paramInfo);
			return "false";
		}
		DBS.mLogger.debug("Data-"+paramInfo);
		try 
		{                
			List<?> processDef = iformObj
				.getDataFromDB("select ProcessDefId from PROCESSDEFTABLE with(nolock) where ProcessName='DBS'");                
			for (Object row : processDef)
			{
				List<?> arr1 = (List<?>) row;
				if (!arr1.isEmpty() && arr1.get(0) != null)
				{
					ProccessDefID = Integer.parseInt(String.valueOf(arr1.get(0)));
				}
			}
		}
		catch (Exception e) 
		{
			DBS.mLogger.debug(" WSNAME: "+iformObj.getActivityName()+", Exception in ProcessDef Query " + e.getMessage());
			return "false";
		}
		try 
		{
		   String whereCondition="";
		   if(iformObj.getValue("SERVICE_REQ_CODE").equals("DBS003")){
			   whereCondition="b.CommStage='"+Stage+"' and b.TemplateId='"+TemplateID+"'";
		   }
		   else if(iformObj.getValue("SERVICE_REQ_CODE").equals("DBS004")){
			   whereCondition="b.CommStage='"+Stage+"' and b.TemplateType='"+TemplateID+"' and b.TemplateId='DBS004_"+iformObj.getValue("DOC_TYPE")+"'";
		   }
		   else if(iformObj.getValue("SERVICE_REQ_CODE").equals("DBS005")){
			   if("Approve".equalsIgnoreCase((String)iformObj.getValue("ID_DECISION"))){
				   whereCondition="b.CommStage='"+Stage+"' and b.TemplateType='"+TemplateID+"' and b.TemplateId='DBS005_"+iformObj.getValue("TRANSFER_CHANNEL")+"'"; 
			   }
			   else{
				   whereCondition="b.CommStage='"+Stage+"' and b.TemplateType='"+TemplateID+"' and b.TemplateId='DBS005_Reject'"; 
			   }
		   }  
		   
		   
		   String qry="select a.CUSTOMERNAME,a.DBREFERENCENO,b.MailTemplate,b.SMSEnglishTemplate,b.FromMail,b.DefaultCCMail,b.MailPlaceHolders,"
		   		+ "b.mailSubject,b.mailSubPlaceHolder,a.MOBNO,a.MAILID,a.AWBNO,a.BRANCH,b.INFOBIP_ALERTID,a.RCIF,a.CIFID,b.infobipPlaceHolder,ISNULL(a.FINACLE_RFR_NO,'')as FINACLE_RFR_NO ,ISNULL(a.FTS_REF_NO,'') as FTS_REF_NO from RB_DBS_EXTTABLE a ,USR_0_DBS_Communication_Templates b where a.WINAME='"+getWorkitemName(iformObj)+"' and "+whereCondition+"";
		   DBS.mLogger.debug(" WSNAME: "+iformObj.getActivityName()+", Query " +qry); 
		   List<?> templates = iformObj.getDataFromDB(qry);
		   for (Object row : templates)
		   {
			   List<?> arr1 = (List<?>) row; 
			   customer_name = (String) arr1.get(0);
			   dbReferenceNo = (String) arr1.get(1);
			   mail_template = (String) arr1.get(2);
               sms_template = (String) arr1.get(3);
			   from_mailid = (String) arr1.get(4);
			   defaultMailCC = (String) arr1.get(5);
			   mail_placeholder = (String) arr1.get(6);
			   mail_subject = (String) arr1.get(7);
			  // sms_placeholder=(String) arr1.get(8);
			   SMS_MOBNO = (String) arr1.get(9);
			   MailID = (String) arr1.get(10);
			   awbNo = (String) arr1.get(11);
			   Object be = arr1.get(12);
			   if (be != null)
			   {
				   String beStr = be.toString();
				   if (!"".equalsIgnoreCase(beStr)){
				   String[] branch_Emirate = beStr.split(",");
				   if (branch_Emirate.length == 2){
        			   branch = branch_Emirate[0];
					   emirates = branch_Emirate[1];
			           }
			       }
			   }
			   infoBipAlert = (String) arr1.get(13);
			   rCIF = (String) arr1.get(14);
			   cCIF=(String) arr1.get(15);
			   infobipPlaceholder=(String) arr1.get(16);
			   ref_no=(String) arr1.get(17);
			   ext_ref_no=(String) arr1.get(18);
		    }
		   DBS.mLogger.debug("qry:"+qry);
		   DBS.mLogger.debug("qry:"+mail_template);
		   /*mail_template=mail_template.replaceAll("'", "''");
		   sms_template=sms_template.replaceAll("'", "''");
		   mail_subject=mail_subject.replaceAll("'", "''");
		   */
		   mail_template=escapeSingleQuote(mail_template);
		   sms_template=escapeSingleQuote(sms_template);
		   mail_subject=escapeSingleQuote(mail_subject);
		   
		   
		   String pHArr[]=mail_placeholder.split(",");
			for(String s:pHArr)
			{
				DBS.mLogger.debug("PlaceHolderName: "+s);
				switch(s)
				{
					case "$SRNUM$":
					{
						mail_template = mail_template.replace(s,dbReferenceNo);
						sms_template=sms_template.replace(s,dbReferenceNo);
						mail_subject=mail_subject.replace(s,dbReferenceNo);
						//dynamicValues=dbReferenceNo;
						dynamicValues=("".equalsIgnoreCase(dynamicValues))?dbReferenceNo:dynamicValues+"~"+dbReferenceNo;
						break;
					}
					case "$CUSTOMER_NAME$":
					{
						mail_template = mail_template.replace(s, customer_name);
						sms_template=sms_template.replace(s,customer_name);
						break;
					}
					case "$LOGO_PATH$":
					{
						//mail_template = mail_template.replace(s, "data:image/png;base64,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");
						mail_template = mail_template.replace(s,DBS.properties.getProperty("base64ImageOfLogo"));
						break;
					}
					case "$AWBNO$":
					{
						mail_template = mail_template.replace(s,awbNo);
						sms_template=sms_template.replace(s,awbNo);
						//dynamicValues=awbNo;
						dynamicValues=("".equalsIgnoreCase(dynamicValues))?awbNo:dynamicValues+"~"+awbNo;
						break;
					}
					case "$Branch$":
					{
						mail_template = mail_template.replace(s,branch);
						//mail_subject=mail_subject.replace(s,awbNo);
						break;
					}
					case "$Emirate$":
					{
						/*if(!"".equalsIgnoreCase(branch)){
							String qry1="select top 1 Emirates from USR_0_DBS_BRANCH_EMIRATES_MASTER with(nolock) where Branch='"+branch+"' and ISACTIVE='Y'";
						    List emiratesList = iformObj.getDataFromDB(qry1);
						    for(int i=0;i<emiratesList.size();i++)
						    {
							   List<String> arr1=(List)emiratesList.get(i); 
							   emirates=arr1.get(0);
						    }
						}*/
					    mail_template = mail_template.replace(s,emirates);
						//mail_subject=mail_subject.replace(s,awbNo);
						break;
					}
					case "$REJECT_REASON$":
					{
						String rejectReasonHtml = formatRejectReasonsAsHtml(iformObj);
						DBS.mLogger.debug("WSNAME: " + iformObj.getActivityName() + ", Replaced $REJECT_REASON$ with: " + rejectReasonHtml);
						mail_template = mail_template.replace(s, rejectReasonHtml);
						sms_template = sms_template.replace(s, rejectReasonHtml.replaceAll("<[^>]*>", "")); // Strip HTML for SMS
						break;
					}
					case "$REF_NO$":
					{
						mail_template = mail_template.replace(s, ref_no);
						//sms_template = sms_template.replace(s, rejectReasonHtml.replaceAll("<[^>]*>", "")); // Strip HTML for SMS
						break;
					}
					case "$EXT_REF_NO$":
					{
						mail_template = mail_template.replace(s,ext_ref_no );
					 //	sms_template = sms_template.replace(s, rejectReasonHtml.replaceAll("<[^>]*>", "")); // Strip HTML for SMS
						break;
					}
				}
				
			}
		   
		   // Additional check for $REJECT_REASON$ placeholder even if not in mail_placeholder list
		   if (mail_template.contains("$REJECT_REASON$") || sms_template.contains("$REJECT_REASON$")) {
			   DBS.mLogger.debug("WSNAME: " + iformObj.getActivityName() + ", Found $REJECT_REASON$ placeholder in template, processing...");
			   String rejectReasonHtml = formatRejectReasonsAsHtml(iformObj);
			   mail_template = mail_template.replace("$REJECT_REASON$", rejectReasonHtml);
			   sms_template = sms_template.replace("$REJECT_REASON$", rejectReasonHtml.replaceAll("<[^>]*>", "")); // Strip HTML for SMS
			   DBS.mLogger.debug("WSNAME: " + iformObj.getActivityName() + ", Replaced $REJECT_REASON$ with: " + rejectReasonHtml);
		   }
		   
		   DBS.mLogger.debug("Email:"+mail_template);
		   DBS.mLogger.debug("SMS:"+sms_template);
			try
			{
				if(!"".equalsIgnoreCase(MailID) && !"".equalsIgnoreCase(mail_template) )
				{
					if(iformObj.getCabinetName().equalsIgnoreCase("rakcas")){
						//MailID="testuser10@rakbanktst.ae";
					}
					//Comment in prod env.
					if("Swift".equalsIgnoreCase((String)iformObj.getValue("TRANSFER_CHANNEL")) && "Approve".equalsIgnoreCase((String)iformObj.getValue("ID_DECISION"))){
						
						String attachmentData=attachmentDetails(iformObj,"SWIFT_Copy");
						String []data=attachmentData.split("~");
						String attachmentISINDEX=data[0];
						String attachmentNames=data[1];
						String attachmentExts=data[2];
						Mailquery="INSERT INTO WFMAILQUEUETABLE(mailFrom,mailTo,mailCC,mailBCC,mailSubject,mailMessage,mailContentType,attachmentISINDEX,attachmentNames,attachmentExts,mailPriority,mailStatus,statusComments,lockedBy,successTime,LastLockTime,insertedBy,mailActionType,insertedTime,processDefId,processInstanceId,workitemId,activityId,noOfTrials,zipFlag,zipName,maxZipSize,alternateMessage)values ('"+from_mailid+"','"+MailID+"','"+defaultMailCC+"',NULL,'"+mail_subject+"',N'"+mail_template+"','text/html;charset=UTF-8','"+attachmentISINDEX+"','"+attachmentNames+"','"+attachmentExts+"',1,'N',NULL,NULL,NULL,NULL,'CUSTOM','TRIGGER',getDate(),"+ProccessDefID+",'"+getWorkitemName(iformObj)+"',1,1,1,NULL,NULL,NULL,NULL)";
						
					}
					else{
						Mailquery="INSERT INTO WFMAILQUEUETABLE(mailFrom,mailTo,mailCC,mailBCC,mailSubject,mailMessage,mailContentType,attachmentISINDEX,attachmentNames,attachmentExts,mailPriority,mailStatus,statusComments,lockedBy,successTime,LastLockTime,insertedBy,mailActionType,insertedTime,processDefId,processInstanceId,workitemId,activityId,noOfTrials,zipFlag,zipName,maxZipSize,alternateMessage)values ('"+from_mailid+"','"+MailID+"','"+defaultMailCC+"',NULL,'"+mail_subject+"',N'"+mail_template+"','text/html;charset=UTF-8',NULL,NULL,NULL,1,'N',NULL,NULL,NULL,NULL,'CUSTOM','TRIGGER',getDate(),"+ProccessDefID+",'"+getWorkitemName(iformObj)+"',1,1,1,NULL,NULL,NULL,NULL)";
					}
					int saveDataInDB = iformObj.saveDataInDB(Mailquery);
					DBS.mLogger.debug("MailQuery:"+Mailquery+saveDataInDB);
				}
				if(iformObj.getValue("SERVICE_REQ_CODE").equals("DBS004") || iformObj.getValue("SERVICE_REQ_CODE").equals("DBS005")){
					
					 if(!("sRNUM".contains(infobipPlaceholder))){
						 dynamicValues="";
					 }
					 SMSquery="Insert into USR_0_INFOBIP_SMS_QUEUETABLE (Processname,WI_NAME,AlertID,InsertedDateTime,CIF,Dynamic_Tags,Dynamic_Values,Alert_Status,MobileNumber,SMS_Content) values ('DBS','" + getWorkitemName(iformObj) + "','"+ infoBipAlert + "',CONVERT(NVARCHAR(50),GETDATE(),120),'"+rCIF+"','"+infobipPlaceholder+"','"+dynamicValues+"','P','"+SMS_MOBNO+"','"+sms_template+"')";
					 int saveDataInDB1 = iformObj.saveDataInDB(SMSquery);
					 DBS.mLogger.debug("Infobip Query:"+SMSquery+saveDataInDB1);	
				}
				if(iformObj.getValue("SERVICE_REQ_CODE").equals("DBS003")){
					 
					 SMSquery="Insert into USR_0_INFOBIP_SMS_QUEUETABLE (Processname,WI_NAME,AlertID,InsertedDateTime,CIF,Dynamic_Tags,Dynamic_Values,Alert_Status,MobileNumber,SMS_Content) values ('DBS','" + getWorkitemName(iformObj) + "','"+ infoBipAlert + "',CONVERT(NVARCHAR(50),GETDATE(),120),'"+cCIF+"','"+infobipPlaceholder+"','"+dynamicValues+"','P','"+SMS_MOBNO+"','"+sms_template+"')";
					 int saveDataInDB1 = iformObj.saveDataInDB(SMSquery);
					 DBS.mLogger.debug("Infobip Query:"+SMSquery+saveDataInDB1);		
				}
			/*	//end
				else if(!"".equalsIgnoreCase(SMS_MOBNO) && !"".equalsIgnoreCase(sms_template))
				{
					SMSquery="Insert into NG_RLOS_SMSQUEUETABLE (Alert_Name,Alert_Code,ALert_Status,Mobile_No,Alert_Text,WI_Name,Workstep_Name,inserted_Date_time) values ('DBS SMS','DBS','P','"+SMS_MOBNO+"','"+sms_template+"', '"+getWorkitemName(iformObj)+"','"+iformObj.getActivityName()+"',getdate())";
					int saveDataInDB1 = iformObj.saveDataInDB(SMSquery);
					DBS.mLogger.debug("Query:"+SMSquery+saveDataInDB1);	
					
				}*/
				
			}
			catch (Exception e) 
			{
				DBS.mLogger.debug(" WSNAME: "+iformObj.getActivityName()+", Exception in Inserting Mail " + e.getMessage());
			}
			
			DBS.mLogger.debug("Inserted in WFMAILQUEUEMAILTABLE:"+mail_template);
		}
		catch (Exception e) 
		{
			DBS.mLogger.debug(" WSNAME: "+iformObj.getActivityName()+", Exception in Mail Trigger " + e.getMessage());
			return "false";
		}
	
		return "true";
		
		}
	private String attachmentDetails(IFormReference iformObj,String docs) {
		String DBQuery = "SELECT TOP 1 " +
	               "pd.ImageIndex, " +
	               "pd.Name, " +
	               "pd.VolumeId, " +
	               "LTRIM(RTRIM(pd.AppName)) AS AppName, " +
	               "pd.CreatedDateTime, " +
	               "pd.comment " +
	               "FROM PDBFolder pf WITH(NOLOCK) " +
	               "INNER JOIN PDBDocumentContent pdc WITH(NOLOCK) " +
	               "ON pf.FolderIndex = pdc.ParentFolderIndex " +
	               "INNER JOIN PDBDocument pd WITH(NOLOCK) " +
	               "ON pd.DocumentIndex = pdc.DocumentIndex " +
	               "WHERE pf.Name = '" +getWorkitemName(iformObj)+ "' " +
	               "AND pd.Name = '" + docs + "' " +
	               "ORDER BY pd.CreatedDateTime DESC";
		DBS.mLogger.debug("Attachment DBQuery: " + DBQuery);
		List extTabDataIPXML = iformObj.getDataFromDB(DBQuery);
		DBS.mLogger.debug("extTabDataIPXML: " + extTabDataIPXML);

		DBS.mLogger.debug("extTabDataOPXML: " + extTabDataIPXML);
		 String attachmentISINDEX="";
		 String name="";
		 String extn="";
		 String imageIndex="";
		 String VolumeId="";
		 for (Object row : extTabDataIPXML)
		 {
		   List<?> arr1 = (List<?>) row; 
		   imageIndex = (String) arr1.get(0);
		   name = (String) arr1.get(1);
		   VolumeId = (String) arr1.get(2);
		   extn = (String) arr1.get(3);
		   attachmentISINDEX = imageIndex + "#" + VolumeId +"#";
		 }
		 return attachmentISINDEX+"~"+name+"~"+extn;
	}
	public static String dateTimeToDateFn(String dateTime)
	{
	   String date="";
	   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy");
	   Date inputDateTime = new Date();
	   try 
	   {
		   inputDateTime = sdf.parse(dateTime);
	   } 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
	   date=sdf1.format(inputDateTime);
	   return date;
	}
	public static String escapeSingleQuote(String text){
		// Pattern to match single quotes that are not already escaped
        Pattern pattern = Pattern.compile("(?<!')'(?!')");
        Matcher matcher = pattern.matcher(text);

        // Replace unescaped single quotes with escaped single quotes
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(result, "''");
        }
        matcher.appendTail(result);
		return result.toString();
	}

	/**
	 * Formats multiple reject reasons as HTML bullet points
	 * Uses the same logic as DBS_IntroDone.rejectReason() method
	 * @param iformObj IFormReference object
	 * @return HTML formatted reject reasons or default message
	 */
	public String formatRejectReasonsAsHtml(IFormReference iformObj) {
		try {
			String rejectReasons = "";
			
			// Try to get reject reasons from grid using the same approach as DBS_IntroDone
			try {
				JSONArray gridData = iformObj.getDataFromGrid("REJECT_REASON_GRID");
				if (gridData != null && gridData.size() > 0) {
					String gridSize = String.valueOf(gridData.size());
					DBS.mLogger.debug("WSNAME: " + iformObj.getActivityName() + ", Reject Reason Grid Size: " + gridSize);
					
					// Use the same logic as DBS_IntroDone.rejectReason() method
					rejectReasons = extractRejectReasonsFromGrid(iformObj, gridSize);
				}
			} catch (Exception e) {
				DBS.mLogger.debug("Error getting reject reasons from grid: " + e.getMessage());
			}
			
			// If grid is empty or no reasons found, use generic message
			if (rejectReasons == null || rejectReasons.trim().isEmpty()) {
				rejectReasons = "Unable to process the request due to validation errors.";
				DBS.mLogger.debug("WSNAME: " + iformObj.getActivityName() + ", No reject reasons found in grid, using generic message");
			}
			
			// Format as HTML
			return formatReasonsAsHtmlList(rejectReasons);
			
		} catch (Exception e) {
			DBS.mLogger.debug("Error formatting reject reasons: " + e.getMessage());
			return "Unable to process the request due to validation errors.";
		}
	}
	
	/**
	 * Extracts reject reasons from grid using the same logic as DBS_IntroDone.rejectReason()
	 * @param iformObj IFormReference object
	 * @param rejectReasonSize Grid size as string
	 * @return Reject reasons separated by #
	 */
	private String extractRejectReasonsFromGrid(IFormReference iformObj, String rejectReasonSize) {
		DBS.mLogger.debug("Reject Reasons Grid Length is " + rejectReasonSize);
		String strRejectReasons = "";
		
		for (int p = 0; p < Integer.parseInt(rejectReasonSize); p++) {
			String completeReason = null;
			completeReason = iformObj.getTableCellValue("REJECT_REASON_GRID", p, 0);
			DBS.mLogger.debug("WINAME: " + getWorkitemName(iformObj) + ", WSNAME: " + iformObj.getActivityName() + ", Complete Reject Reasons: " + completeReason);
			
			if (completeReason != null && !completeReason.trim().isEmpty()) {
				String reasonText = "";
				
				// Extract reason text (same logic as DBS_IntroDone)
				if (completeReason.indexOf("-") > -1) {
					// Format: (code) - reason
					reasonText = completeReason.substring(completeReason.indexOf("-") + 1).trim();
					DBS.mLogger.debug("WINAME: " + getWorkitemName(iformObj) + ", WSNAME: " + iformObj.getActivityName() + ", Extracted Reject Reason: " + reasonText);
				} else {
					// No code format, use complete reason
					reasonText = completeReason.trim();
					DBS.mLogger.debug("WINAME: " + getWorkitemName(iformObj) + ", WSNAME: " + iformObj.getActivityName() + ", Reject Reason (no code): " + reasonText);
				}
				
				// Build the reasons string
				if (strRejectReasons.isEmpty()) {
					strRejectReasons = reasonText;
				} else {
					strRejectReasons = strRejectReasons + "#" + reasonText;
				}
				
				DBS.mLogger.debug("WINAME: " + getWorkitemName(iformObj) + ", WSNAME: " + iformObj.getActivityName() + ", Current Reject Reasons String: " + strRejectReasons);
			}
		}
		
		DBS.mLogger.debug("Final reject reasons are: " + strRejectReasons);
		return strRejectReasons;
	}
	
	/**
	 * Converts delimited reject reasons into HTML bullet list
	 * @param reasons String with reasons separated by # or other delimiters
	 * @return HTML formatted list
	 */
	private String formatReasonsAsHtmlList(String reasons) {
		if (reasons == null || reasons.trim().isEmpty()) {
			return "Unable to process the request due to validation errors.";
		}
		
		// Split by various possible delimiters
		String[] reasonArray = reasons.split("[#|;]");
		
		if (reasonArray.length == 1) {
			// Single reason - return as simple text
			return reasonArray[0].trim();
		}
		
		// Multiple reasons - format as HTML list
		StringBuilder htmlList = new StringBuilder();
		htmlList.append("<ul style=\"margin: 10px 0; padding-left: 20px; line-height: 140%;\">");
		
		for (String reason : reasonArray) {
			String trimmedReason = reason.trim();
			if (!trimmedReason.isEmpty()) {
				htmlList.append("<li style=\"margin: 5px 0; color: #000000;\">")
						.append(trimmedReason)
						.append("</li>");
			}
		}
		
		htmlList.append("</ul>");
		return htmlList.toString();
	}

}