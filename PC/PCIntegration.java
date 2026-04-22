/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: RAK BPM
Application				: RAK BPM Utility
Module					: PC
File Name				: PCIntegration.java
Author 					: Sakshi Grover
Date (DD/MM/YYYY)		: 30/04/2019

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
Change Request			01/01/2024			Varun/Suraj			Added integration calls as part of CR.
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.PC;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.XMLParser;


public class PCIntegration
{
	
	String customIntegration(String cabinetName, String sessionID,String sJtsIp, String iJtsPort , String wi_name,
		String ws_name, int socket_connection_timeout,int integrationWaitTime,
		HashMap<String, String> socketDetailsMap)
	{
		int call_status_fail_count=0;
		String DesStatus="Success";
		try
		{
			String QueryString = "SELECT UPPER(ACC_NUMBER) AS ACC_NUMBER, UPPER(INTEGRATION_FIELD) AS INTEGRATION_FIELD, " +
			"INSERTIONORDERID FROM USR_0_PC_ERR_HANDLING WITH (nolock) WHERE WI_NAME ='"+wi_name+"' AND " +
			"(CALL_STATUS ='New' OR CALL_STATUS='Failure' OR CALL_STATUS='' OR CALL_STATUS IS NULL)";

			String sInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionID);

			PCLog.PCLogger.debug("APSelect Inputxml: "+sInputXML);

			String sOutputXML=PC.WFNGExecute(sInputXML,sJtsIp,iJtsPort,1);
			PCLog.PCLogger.debug("APSelect OutputXML: "+sOutputXML);

		    XMLParser sXMLParser= new XMLParser(sOutputXML);
		    String sMainCode = sXMLParser.getValueOf("MainCode");
		    PCLog.PCLogger.debug("SMainCode: "+sMainCode);

		    int sTotalRecords = Integer.parseInt(sXMLParser.getValueOf("TotalRetrieved"));
		    PCLog.PCLogger.debug("STotalRecords: "+sTotalRecords);

			if (sMainCode.equals("0") && sTotalRecords > 0)
			{
				for(int i=0;i<sTotalRecords;i++)
				{

					String sXMLData=sXMLParser.getNextValueOf("Record");
					sXMLData =sXMLData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

	        		XMLParser subXMLParser = new XMLParser(sXMLData);
	        		String insertionorderid=subXMLParser.getValueOf("insertionorderid");
	        		PCLog.PCLogger.debug("Insertionorderid: "+insertionorderid);

	        		String accountNumber=subXMLParser.getValueOf("ACC_NUMBER");
	        		PCLog.PCLogger.debug("AccountNumber: "+accountNumber);

	        		String integrationField=subXMLParser.getValueOf("INTEGRATION_FIELD");
	        		PCLog.PCLogger.debug("IntegrationField: "+integrationField);


					String responseXML=socketConnection(cabinetName,sessionID,sJtsIp,iJtsPort,wi_name,
							ws_name,accountNumber,integrationField,socket_connection_timeout,integrationWaitTime,
							socketDetailsMap);

					XMLParser xmlParserResponse = new XMLParser(responseXML);
					String call_status=xmlParserResponse.getValueOf("Status");
					PCLog.PCLogger.debug("Call_status: "+call_status);

					String msg_ID=xmlParserResponse.getValueOf("InputMessageId");
					PCLog.PCLogger.debug("Msg_ID: "+msg_ID);

					String return_code=xmlParserResponse.getValueOf("ReturnCode");
					PCLog.PCLogger.debug("Return_code: "+return_code);

					String return_desc=xmlParserResponse.getValueOf("ReturnDesc");
					PCLog.PCLogger.debug("Return_desc: "+return_desc);

					String mq_output_ref=xmlParserResponse.getValueOf("MemoPadSrlNum");
					PCLog.PCLogger.debug("Mq_output_ref: "+mq_output_ref);

					String integrationStatus = "Failure";
					if(return_code.equalsIgnoreCase("0000"))
					{
						integrationStatus  = "Success";
						PCLog.PCLogger.debug("IntegrationStatus: "+integrationStatus);
					}
					String columnName="MSG_ID, RETURN_CODE, RETURN_DESC, MQ_OUTPUT_REF, CALL_STATUS,DATE_TIME";
					String columnValues="'"+msg_ID+"','"+return_code+"','"+return_desc+"','"+mq_output_ref+"','"+integrationStatus+"','"+CommonMethods.getdateCurrentDateInSQLFormat()+"'";


					String whereClause="WI_NAME='"+wi_name+"' AND insertionorderid="+insertionorderid;

					String apUpdateInputXML=CommonMethods.apUpdateInput(cabinetName, sessionID, "USR_0_PC_ERR_HANDLING",
							columnName,columnValues, whereClause);
					PCLog.PCLogger.debug("APUpdateInputXML: "+apUpdateInputXML);

					String apUpdateOutputXML=PC.WFNGExecute(apUpdateInputXML,sJtsIp,iJtsPort,1);
					PCLog.PCLogger.debug("APUpdateOutputXML: "+apUpdateOutputXML);

				    XMLParser apUpdateXMLParser= new XMLParser(apUpdateOutputXML);
				    String apUpdateMainCode = apUpdateXMLParser.getValueOf("MainCode");
				    PCLog.PCLogger.debug("APUpdateMainCode: "+apUpdateMainCode);
				    if(apUpdateMainCode.equalsIgnoreCase("0"))
				    {
				    	PCLog.PCLogger.debug("APUpdate Successful for USR_0_PC_ERR_HANDLING: "+apUpdateMainCode);
				    }
					if(!integrationStatus.equalsIgnoreCase("Success"))
					{
						call_status_fail_count+=1;

					}
				}

				PCLog.PCLogger.debug("Call_status_fail_count: "+call_status_fail_count);
				if(call_status_fail_count > 0)
					DesStatus="Failure";
				else
					DesStatus="Success";

			}
			
			return DesStatus;
		}
		catch(Exception e)
		{
			return "";
		}

	}

	private String socketConnection(String cabinetName,String sessionID,
			String sJtsIp, String iJtsPort ,String wi_name, String ws_name, String acc_number,String memopad,
			int connection_timeout,int integrationWaitTime,
			HashMap<String, String> socketDetailsMap )
	{
		String socketServerIP;
		int socketServerPort;
		Socket socket = null;
		OutputStream out = null;
		InputStream socketInputStream = null;
		DataOutputStream dout = null;
		DataInputStream din = null;
		String outputResponse = null;
		String inputRequest = null;
		String inputMessageID = null;

		java.util.Date d1 = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
		String DateExtra2 = sdf1.format(d1)+"+04:00";

		StringBuilder finalXML = new StringBuilder("<EE_EAI_MESSAGE>\n"+
				"<EE_EAI_HEADER>\n"+
				"<MsgFormat>MEMOPAD_MAINTENANCE_REQ</MsgFormat>\n"+
				"<MsgVersion>0001</MsgVersion>\n"+
				"<RequestorChannelId>BPM</RequestorChannelId>\n"+
				"<RequestorUserId>RAKUSER</RequestorUserId>\n"+
				"<RequestorLanguage>E</RequestorLanguage>\n"+
				"<RequestorSecurityInfo>secure</RequestorSecurityInfo>\n"+
				"<ReturnCode>911</ReturnCode>\n"+
				"<ReturnDesc>Issuer Timed Out</ReturnDesc>\n"+
				"<MessageId>UniqueMessageId123</MessageId>\n"+
				"<Extra1>REQ||SHELL.JOHN</Extra1>\n"+
				"<Extra2>"+DateExtra2+"</Extra2>\n"+
				"</EE_EAI_HEADER>\n"+
				"<MemopadMaintenanceReq>\n"+
				"<BankId>RAK</BankId>\n"+
				"<CIFID></CIFID>\n"+
				"<ACNumber>"+acc_number+"</ACNumber>\n"+
				"<Operation>A</Operation>\n"+
				"<Topic>Profile Change</Topic>\n"+
				"<FuncCode>FT</FuncCode>\n"+
				"<Intent>F</Intent>\n"+
				"<Security>P</Security>\n"+
				"<MemoText>"+memopad.replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;")+"</MemoText>\n"+
				"<ExceptionCode></ExceptionCode>\n"+
				"<FreeField1></FreeField1>\n"+
				"<FreeField2></FreeField2>\n"+
				"<FreeField3></FreeField3>\n"+
				"</MemopadMaintenanceReq>\n"+
				"</EE_EAI_MESSAGE>\n");

		try
		{
			PCLog.PCLogger.debug("sessionID "+ sessionID);

			socketServerIP=socketDetailsMap.get("SocketServerIP");
			PCLog.PCLogger.debug("SocketServerIP "+ socketServerIP);
			socketServerPort=Integer.parseInt(socketDetailsMap.get("SocketServerPort"));
			PCLog.PCLogger.debug("SocketServerPort "+ socketServerPort);

	   		if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort==0))
	   		{

    			socket = new Socket(socketServerIP, socketServerPort);
    			socket.setSoTimeout(connection_timeout*1000);
    			out = socket.getOutputStream();
    			socketInputStream = socket.getInputStream();
    			dout = new DataOutputStream(out);
    			din = new DataInputStream(socketInputStream);
    			PCLog.PCLogger.debug("Dout " + dout);
    			PCLog.PCLogger.debug("Din " + din);

    			outputResponse = "";
    			inputRequest = getRequestXML( cabinetName,sessionID,wi_name, ws_name, CommonConnection.getUsername(), finalXML);

    			if (inputRequest != null && inputRequest.length() > 0)
    			{
    				int inputRequestLen = inputRequest.getBytes("UTF-16LE").length;
    				PCLog.PCLogger.debug("RequestLen: "+inputRequestLen + "");
    				inputRequest = inputRequestLen + "##8##;" + inputRequest;
    				PCLog.PCLogger.debug("InputRequest"+"Input Request Bytes : "+ inputRequest.getBytes("UTF-16LE"));
    				dout.write(inputRequest.getBytes("UTF-16LE"));dout.flush();
    			}
    			byte[] readBuffer = new byte[500];
    			int num = din.read(readBuffer);
    			if (num > 0)
    			{

    				byte[] arrayBytes = new byte[num];
    				System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
    				outputResponse = outputResponse+ new String(arrayBytes, "UTF-16LE");
					inputMessageID = outputResponse;
    				PCLog.PCLogger.debug("OutputResponse: "+outputResponse);

    				if(!"".equalsIgnoreCase(outputResponse))
    					outputResponse = getResponseXML(cabinetName,sJtsIp,iJtsPort,sessionID,
    							wi_name,outputResponse,integrationWaitTime );
    				if(outputResponse.contains("&lt;"))
    				{
    					outputResponse=outputResponse.replaceAll("&lt;", "<");
    					outputResponse=outputResponse.replaceAll("&gt;", ">");
    				}
    			}
    			socket.close();

				outputResponse = outputResponse.replaceAll("</MessageId>","</MessageId>/n<InputMessageId>"+inputMessageID+"</InputMessageId>");

    			return outputResponse;

    		}
    		else
    		{
    			PCLog.PCLogger.debug("SocketServerIp and SocketServerPort is not maintained "+"");
    			PCLog.PCLogger.debug("SocketServerIp is not maintained "+	socketServerIP);
    			PCLog.PCLogger.debug(" SocketServerPort is not maintained "+	socketServerPort);
    			return "Socket Details not maintained";
    		}
		}
		catch (Exception e)
		{
			PCLog.PCLogger.debug("Exception Occured Mq_connection_CC"+e.getStackTrace());
			return "";
		}
		finally
		{
			try
			{
				if(out != null)
				{
					out.close();
					out=null;
				}
				if(socketInputStream != null)
				{

					socketInputStream.close();
					socketInputStream=null;
				}
				if(dout != null)
				{

					dout.close();
					dout=null;
				}
				if(din != null)
				{

					din.close();
					din=null;
				}
				if(socket != null)
				{
					if(!socket.isClosed())
						socket.close();
					socket=null;
				}
			}catch(Exception e)
			{
				PCLog.PCLogger.debug("Final Exception Occured Mq_connection_CC"+e.getStackTrace());
				//printException(e);
			}
		}
	}

	private static String getRequestXML(String cabinetName, String sessionID,
			String wi_name, String ws_name, String userName, StringBuilder final_XML)
	{
		StringBuffer strBuff = new StringBuffer();
		strBuff.append("<APMQPUTGET_Input>");
		strBuff.append("<SessionId>" + sessionID + "</SessionId>");
		strBuff.append("<EngineName>" + cabinetName + "</EngineName>");
		strBuff.append("<XMLHISTORY_TABLENAME>NG_PC_XMLLOG_HISTORY</XMLHISTORY_TABLENAME>");
		strBuff.append("<WI_NAME>" + wi_name + "</WI_NAME>");
		strBuff.append("<WS_NAME>" + ws_name + "</WS_NAME>");
		strBuff.append("<USER_NAME>" + userName + "</USER_NAME>");
		strBuff.append("<MQ_REQUEST_XML>");
		strBuff.append(final_XML);
		strBuff.append("</MQ_REQUEST_XML>");
		strBuff.append("</APMQPUTGET_Input>");
		PCLog.PCLogger.debug("GetRequestXML: "+ strBuff.toString());
		return strBuff.toString();
	}

	private static String getResponseXML(String cabinetName,String sJtsIp,String iJtsPort, String
			sessionID, String wi_name,String message_ID, int integrationWaitTime)
	{

		String outputResponseXML="";
		try
		{
			String QueryString = "select OUTPUT_XML from NG_PC_XMLLOG_HISTORY with (nolock) where " +
					"MESSAGE_ID ='"+message_ID+"' and WI_NAME = '"+wi_name+"'";

			String responseInputXML =CommonMethods.apSelectWithColumnNames(QueryString, cabinetName, sessionID);
			PCLog.PCLogger.debug("Response APSelect InputXML: "+responseInputXML);

			int Loop_count=0;
			do
			{
				String responseOutputXML=PC.WFNGExecute(responseInputXML,sJtsIp,iJtsPort,1);
				PCLog.PCLogger.debug("Response APSelect OutputXML: "+responseOutputXML);

			    XMLParser xmlParserSocketDetails= new XMLParser(responseOutputXML);
			    String responseMainCode = xmlParserSocketDetails.getValueOf("MainCode");
			    PCLog.PCLogger.debug("ResponseMainCode: "+responseMainCode);

			    int responseTotalRecords = Integer.parseInt(xmlParserSocketDetails.getValueOf("TotalRetrieved"));
			    PCLog.PCLogger.debug("ResponseTotalRecords: "+responseTotalRecords);
			    if (responseMainCode.equals("0") && responseTotalRecords > 0)
				{
					String responseXMLData=xmlParserSocketDetails.getNextValueOf("Record");
					responseXMLData =responseXMLData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

	        		XMLParser xmlParserResponseXMLData = new XMLParser(responseXMLData);

	        		outputResponseXML=xmlParserResponseXMLData.getValueOf("OUTPUT_XML");
	        		PCLog.PCLogger.debug("OutputResponseXML: "+outputResponseXML);

	        		if("".equalsIgnoreCase(outputResponseXML)){
	        			outputResponseXML="Error";
	    			}
	        		break;
				}
			    Loop_count++;
			    Thread.sleep(1000);
			}
			while(Loop_count<integrationWaitTime);

		}
		catch(Exception e)
		{
			PCLog.PCLogger.debug("Exception occurred in outputResponseXML" + e.getMessage());
			PCLog.PCLogger.debug("Exception occurred in outputResponseXML" + e.getStackTrace());
			outputResponseXML="Error";
		}
		return outputResponseXML;
	}

	
	// byVarun (Integration calls)
	
	private static String rowVal = "";
	private static String DocName = "";
	private static String returnValue = "";
	private static String RPTableName = "USR_0_PC_RELATED_PARTY_GRID_DTLS";
	
	public static ArrayList<String> DedupeGridCIFID = new ArrayList<String>(); // no use
	public static ArrayList<String> DedupeGridCIFStatus = new ArrayList<String>();	
	public static ArrayList<String> DedupeGridFullName = new ArrayList<String>();	
	public static ArrayList<String> DedupeGridDOB = new ArrayList<String>();
	public static ArrayList<String> DedupeGridGender = new ArrayList<String>();
	public static ArrayList<String> DedupeGridEmiratesID = new ArrayList<String>();
	public static ArrayList<String> DedupeGridPassportNo = new ArrayList<String>();
	public static ArrayList<String> DedupeGridNationality = new ArrayList<String>();
	public static ArrayList<String> DedupeGridResAddress = new ArrayList<String>();
	public static ArrayList<String> DedupeGridMobNo = new ArrayList<String>();
	public static ArrayList<String> DedupeGridBlacklistedFlag = new ArrayList<String>();
	public static ArrayList<String> DedupeGridNegativelistedFlag = new ArrayList<String>();	
	public static ArrayList<String> DedupeGridIncorporationDate = new ArrayList<String>();
	public static ArrayList<String> DedupeGridIncorporationCountry = new ArrayList<String>();
	public static ArrayList<String> DedupeGridIncorporationTLNo= new ArrayList<String>();	
	
	public static ArrayList<String> BlacklistGridCIFID = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridCifStatus = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridFullName = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridEmiratesID = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridPassportNo = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridDOB = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridResAddress = new ArrayList<String>();	
	public static ArrayList<String> BlacklistGridMobNo = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridBlacklistedFlag = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridBlacklistedDate = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridNegatedFlag = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridNegatedDate = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridDateOfIncorp = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridCountryOfIncorp = new ArrayList<String>();
	public static ArrayList<String> BlacklistGridTLNo = new ArrayList<String>();
	
	public static ArrayList<String> FircoGridSRNo = new ArrayList<String>();
	public static ArrayList<String> FircoGridOFACID = new ArrayList<String>();
	public static ArrayList<String> FircoGridName = new ArrayList<String>();
	public static ArrayList<String> FircoGridMatchingText = new ArrayList<String>();
	public static ArrayList<String> FircoGridOrigin = new ArrayList<String>();
	public static ArrayList<String> FircoGridDestination = new ArrayList<String>();
	public static ArrayList<String> FircoGridDOB = new ArrayList<String>();
	public static ArrayList<String> FircoGridUserData1 = new ArrayList<String>();
	public static ArrayList<String> FircoGridNationality = new ArrayList<String>();
	public static ArrayList<String> FircoGridPassport = new ArrayList<String>();
	public static ArrayList<String> FircoGridAdditionalInfo = new ArrayList<String>();
	public static ArrayList<String> FircoGridREFERENCENO = new ArrayList<String>();
	
	public static String DedupeCall( String cabinetName,String UserName,String sessionId,String sJtsIp, String iJtsPort , String processInstanceID,
			String ws_name, int socket_connection_timeout,int integrationWaitTime,
			HashMap<String, String> socketDetailsMap, HashMap<String, String> RelPartyGridDataMap)
	{
		
		try
		{
			String DocDetXml="";
			String NATIONALITY = "";			
			String DOB = "";
			String MidName = "";
			String FullName = "";
			String MobileNumberDetails = "";
			String MobileNumber = "";
			String CIF_ID="";
			String First_Name="";
			String Last_Name="";
			String Maritalstatus="";
			String OrganizationDetails = "";
			String PersonalDetails = "";
			String CustomerType = "";
			String RetailCorpFlag = "";
			String CustDormancy = "";
		
			
			CIF_ID = RelPartyGridDataMap.get("CIF");

			String CompFlag = RelPartyGridDataMap.get("COMPANYFLAG");
						
			CustomerType = 	"<CustomerType>"+CompFlag+"</CustomerType>";
			RetailCorpFlag = "<RetailCorpFlag>"+CompFlag+"</RetailCorpFlag>";
			PCLog.PCLogger.debug("Dedupe input XML: "+RelPartyGridDataMap);
			
			if("R".equalsIgnoreCase(CompFlag))
			{				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("EMIRATESID")))
				{
					PCLog.PCLogger.debug("Dedupe input XML: "+CompFlag+"2");
					PCLog.PCLogger.debug("Dedupe input XML: "+RelPartyGridDataMap.get("EMIRATESID"));
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>EMID</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("EMIRATESID")+"</DocumentRefNumber>\n" +
						"</Document>";
					PCLog.PCLogger.debug("Dedupe input XML: "+CompFlag+"3");
				}
				if(!(RelPartyGridDataMap.get("PASSPORTNUMBER").equals("")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>PPT</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("PASSPORTNUMBER")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
				if(!"".equals(RelPartyGridDataMap.get("VISANUMBER")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>VISA</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("VISANUMBER")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
				
				PersonalDetails = "<PersonDetails>";
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("FIRSTNAME")))
					PersonalDetails = PersonalDetails+"<FirstName>"+RelPartyGridDataMap.get("FIRSTNAME")+"</FirstName>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("MIDDLENAME")))
					PersonalDetails = PersonalDetails+"<MiddleName>"+RelPartyGridDataMap.get("MIDDLENAME")+"</MiddleName>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("LASTNAME")))
					PersonalDetails = PersonalDetails+"<LastName>"+RelPartyGridDataMap.get("LASTNAME")+"</LastName>";
				
				String FullName1 = RelPartyGridDataMap.get("FIRSTNAME") +" "+RelPartyGridDataMap.get("LASTNAME");
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("MIDDLENAME")))
					FullName1 = RelPartyGridDataMap.get("FIRSTNAME") +" "+ RelPartyGridDataMap.get("MIDDLENAME") +" "+RelPartyGridDataMap.get("LASTNAME");
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("NATIONALITY")))
					PersonalDetails = PersonalDetails+"<Nationality>"+RelPartyGridDataMap.get("NATIONALITY")+"</Nationality>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("DATEOFBIRTH")))
					PersonalDetails = PersonalDetails+"<DateOfBirth>"+RelPartyGridDataMap.get("DATEOFBIRTH")+"</DateOfBirth>";
				
				PersonalDetails = PersonalDetails+"</PersonDetails>";
							    			
								
				if (!"".equalsIgnoreCase(RelPartyGridDataMap.get("RELMOBILENUMBER")))
				{
					String fullMobNo = RelPartyGridDataMap.get("RELMOBILENUMBER");
					MobileNumber=fullMobNo;
					MobileNumberDetails = "<ContactDetails>\n"+
							"<PhoneFax>\n"+
								"<PhoneType>Phone</PhoneType>\n"+
								"<PhoneValue>"+MobileNumber+"</PhoneValue>\n"+
							"</PhoneFax>\n"+
						"</ContactDetails>";
				}
			}
			if("C".equalsIgnoreCase(CompFlag))
			{								
				OrganizationDetails = "";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("COMPANY_NAME")))
					OrganizationDetails = OrganizationDetails + "<CorporateName>"+RelPartyGridDataMap.get("COMPANY_NAME")+"</CorporateName>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("COUNTRY"))) 
					OrganizationDetails = OrganizationDetails + "<CountryOfIncorporation>"+RelPartyGridDataMap.get("COUNTRY")+"</CountryOfIncorporation>";
						
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("DATEOFINCORPORATION"))) 			
					OrganizationDetails = OrganizationDetails + "<DateOfIncorporation>"+RelPartyGridDataMap.get("DATEOFINCORPORATION")+"</DateOfIncorporation>";
				
				if(!OrganizationDetails.equalsIgnoreCase(""))
					OrganizationDetails = "<OrganizationDetails>"+OrganizationDetails+"</OrganizationDetails>";
				
				if (!"".equalsIgnoreCase(RelPartyGridDataMap.get("RELMOBILENUMBER")))
				{
					String fullMobNo = RelPartyGridDataMap.get("RELMOBILENUMBER");
					MobileNumber=fullMobNo;
					MobileNumberDetails = "<ContactDetails>\n"+
							"<PhoneFax>\n"+
								"<PhoneType>Phone</PhoneType>\n"+
								"<PhoneValue>"+MobileNumber+"</PhoneValue>\n"+
							"</PhoneFax>\n"+
						"</ContactDetails>";
				}
				
				if(!(RelPartyGridDataMap.get("TL_NUMBER").equals("")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>TDLIC</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("TL_NUMBER")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
			}	
			
			java.util.Date d1 = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
			String DateExtra2 = sdf1.format(d1)+"+04:00";
			
			/*SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
			String ReqDateTime = sdf2.format(d1);*/
			
			StringBuilder sInputXML = new StringBuilder("<EE_EAI_MESSAGE>\n" +
					"<EE_EAI_HEADER>\n" +
					"<MsgFormat>DEDUP_SUMMARY</MsgFormat>\n" +
					"<MsgVersion>0001</MsgVersion>\n" +
					"<RequestorChannelId>BPM</RequestorChannelId>\n" +
					"<RequestorUserId>RAKUSER</RequestorUserId>\n" +
					"<RequestorLanguage>E</RequestorLanguage>\n" +
					"<RequestorSecurityInfo>secure</RequestorSecurityInfo>\n" +
					"<ReturnCode>0000</ReturnCode>\n" +
					"<ReturnDesc>REQ</ReturnDesc>\n" +
					"<MessageId>BPM_MARY_02</MessageId>\n" +
					"<Extra1>REQ||BPM.123</Extra1>\n" +
					"<Extra2>"+DateExtra2+"</Extra2>\n" +
					"</EE_EAI_HEADER>\n"+
					"<CustomerDuplicationListRequest>" +
						"<BankId>RAK</BankId><CIFID>"+CIF_ID+"</CIFID>"+CustomerType+" " +
						" "+RetailCorpFlag+"<EntityType>All</EntityType>" +
						" "+PersonalDetails+" "+
						OrganizationDetails+MobileNumberDetails+DocDetXml+"</CustomerDuplicationListRequest>\n"+
					"</EE_EAI_MESSAGE>");
			
			PCLog.PCLogger.debug("Dedupe input XML: "+sInputXML);
			
			String responseXML =socketConnection(cabinetName, CommonConnection.getUsername(), sessionId,sJtsIp,
					 iJtsPort,  processInstanceID,  ws_name, integrationWaitTime, socket_connection_timeout,
					  socketDetailsMap, sInputXML);
			
			PCLog.PCLogger.debug("socketConnection responseXML: "+responseXML);

			XMLParser xmlParserDetails= new XMLParser(responseXML);
		    String return_code = xmlParserDetails.getValueOf("ReturnCode");
		    PCLog.PCLogger.debug("Return Code: "+return_code);

		    String MsgFormat = xmlParserDetails.getValueOf("MsgFormat");
		    String return_desc = xmlParserDetails.getValueOf("ReturnDesc");
			
			if (return_desc.trim().equalsIgnoreCase(""))
				return_desc = xmlParserDetails.getValueOf("Description").replace("'", "");
			
			String MsgId = "";
			if (responseXML.contains("<MessageId>"))
				MsgId = xmlParserDetails.getValueOf("MessageId");
			
		    PCLog.PCLogger.debug("Return Desc: "+return_desc);
		    
		    String CallStatus = "";
		    if(return_code.equals("0000"))
		    	CallStatus="Success";
		    else
		    	CallStatus="Failure~"+"Dedup~"+MsgId+"~"+return_desc;
		    
		    if(return_code.equals("0000")) 
			{		    	
				while(responseXML.contains("<Customer>"))
				{
					String colNames="";
			    	String colValues="";
					String MainCifId = "";
					String MainCustomerName = "";
					String MainDateOfBirth = "";
					String MainNationality = "";
					String MainEmiratesID = "";
					String MainPassportNo="";
					String MainMobileNo="";
					String MainGender="";
					String MainResAddress="";
					String MainIncorporationDate="";
					String MainIncorporationCountry="";
					String MainTL="";
						
					rowVal = responseXML.substring(responseXML.indexOf("<Customer>"),responseXML.indexOf("</Customer>")+"</Customer>".length());
					
					if(rowVal.equalsIgnoreCase("<Customer></Customer>"))
						return "No result";
					
					MainCifId = (rowVal.contains("<CIFID>")) ? rowVal.substring(rowVal.indexOf("<CIFID>")+"</CIFID>".length()-1,rowVal.indexOf("</CIFID>")):"";
					MainCustomerName = (rowVal.contains("<FullName>")) ? rowVal.substring(rowVal.indexOf("<FullName>")+"</FullName>".length()-1,rowVal.indexOf("</FullName>")):"";
					String DateOfBirth = (rowVal.contains("<DateOfBirth>")) ? rowVal.substring(rowVal.indexOf("<DateOfBirth>")+"</DateOfBirth>".length()-1,rowVal.indexOf("</DateOfBirth>")):"";
					
					MainNationality = (rowVal.contains("<Nationality>")) ? rowVal.substring(rowVal.indexOf("<Nationality>")+"</Nationality>".length()-1,rowVal.indexOf("</Nationality>")):"";
					MainIncorporationCountry= (rowVal.contains("<CountryOfIncorporation>")) ? rowVal.substring(rowVal.indexOf("<CountryOfIncorporation>")+"</CountryOfIncorporation>".length()-1,rowVal.indexOf("</CountryOfIncorporation>")):"";
					MainIncorporationDate= (rowVal.contains("<DateOfIncorporation>")) ? rowVal.substring(rowVal.indexOf("<DateOfIncorporation>")+"</DateOfIncorporation>".length()-1,rowVal.indexOf("</DateOfIncorporation>")):"";
					RetailCorpFlag = (rowVal.contains("<RetailCorpFlag>")) ? rowVal.substring(rowVal.indexOf("<RetailCorpFlag>")+"</RetailCorpFlag>".length()-1,rowVal.indexOf("</RetailCorpFlag>")):"";
					
					if("".equalsIgnoreCase(MainCustomerName.trim()))
						MainCustomerName = (rowVal.contains("<CorporateName>")) ? rowVal.substring(rowVal.indexOf("<CorporateName>")+"</CorporateName>".length()-1,rowVal.indexOf("</CorporateName>")):"";
					
					MainDateOfBirth = "";
					if(!(DateOfBirth==null || DateOfBirth.equalsIgnoreCase(""))) 
					{
						MainDateOfBirth = DateOfBirth;
					}
					
					int countwhilchk = 0;
					while(rowVal.contains("<Document>"))
					{							
						String rowData = rowVal.substring(rowVal.indexOf("<Document>"),rowVal.indexOf("</Document>")+"</Document>".length());
						String DocumentType = (rowData.contains("<DocumentType>")) ? rowData.substring(rowData.indexOf("<DocumentType>")+"</DocumentType>".length()-1,rowData.indexOf("</DocumentType>")):"";
						if (DocumentType.equalsIgnoreCase("EMID"))
						{
							MainEmiratesID = rowData.substring(rowData.indexOf("<DocumentRefNumber>")+"<DocumentRefNumber>".length(),rowData.indexOf("</DocumentRefNumber>"));
						}							
						//passport number
						if (DocumentType.equalsIgnoreCase("PPT"))
						{
							MainPassportNo = rowData.substring(rowData.indexOf("<DocumentRefNumber>")+"<DocumentRefNumber>".length(),rowData.indexOf("</DocumentRefNumber>"));
							
						}
						//TL number
						if (DocumentType.equalsIgnoreCase("TDLIC"))
						{
							MainTL = rowData.substring(rowData.indexOf("<DocumentRefNumber>")+"<DocumentRefNumber>".length(),rowData.indexOf("</DocumentRefNumber>"));
					
						}	
						
						rowVal = rowVal.substring(0,rowVal.indexOf(rowData))+ rowVal.substring(rowVal.indexOf(rowData)+rowData.length());
						
						countwhilchk++;
						if(countwhilchk == 50)
						{
							countwhilchk = 0;
							break;
						}
					
					 }
					
					countwhilchk = 0;
					while(rowVal.contains("<PhoneFax>"))
					{							
						String rowData = rowVal.substring(rowVal.indexOf("<PhoneFax>"),rowVal.indexOf("</PhoneFax>")+"</PhoneFax>".length());
						String PhoneType = (rowData.contains("<PhoneType>")) ? rowData.substring(rowData.indexOf("<PhoneType>")+"</PhoneType>".length()-1,rowData.indexOf("</PhoneType>")):"";
						//PCLog.PCLogger.debug("WINAME : "+objRespBean.getWorkitemNumber()+", WSNAME: "+objRespBean.getWorkStep()+", PhoneType "+PhoneType);
						
						if (PhoneType.equalsIgnoreCase("CELLPH1"))
						{
							MainMobileNo = (rowData.contains("<PhoneValue>")) ? rowData.substring(rowData.indexOf("<PhoneValue>")+"</PhoneValue>".length()-1,rowData.indexOf("</PhoneValue>")):"";
						}							
						
						rowVal = rowVal.substring(0,rowVal.indexOf(rowData))+ rowVal.substring(rowVal.indexOf(rowData)+rowData.length());
							
						countwhilchk++;
						if(countwhilchk == 50)
						{
							countwhilchk = 0;
							break;
						}
					
					 }
					
					countwhilchk = 0;
					String BlacklistedStatusFlag = "";
					String NegativelistedStatusFlag = "";
					while(rowVal.contains("<StatusInfo>"))
					{							
						String rowData = rowVal.substring(rowVal.indexOf("<StatusInfo>"),rowVal.indexOf("</StatusInfo>")+"</StatusInfo>".length());
						String StatusType = (rowData.contains("<StatusType>")) ? rowData.substring(rowData.indexOf("<StatusType>")+"</StatusType>".length()-1,rowData.indexOf("</StatusType>")):"";
						//PCLog.PCLogger.debug("WINAME : "+objRespBean.getWorkitemNumber()+", WSNAME: "+objRespBean.getWorkStep()+", PhoneType "+PhoneType);
						
						if (StatusType.equalsIgnoreCase("Blacklisted"))
						{
							BlacklistedStatusFlag = (rowData.contains("<StatusFlag>")) ? rowData.substring(rowData.indexOf("<StatusFlag>")+"</StatusFlag>".length()-1,rowData.indexOf("</StatusFlag>")):"";
						}	
						else if (StatusType.equalsIgnoreCase("Negativelisted"))
						{
							NegativelistedStatusFlag = (rowData.contains("<StatusFlag>")) ? rowData.substring(rowData.indexOf("<StatusFlag>")+"</StatusFlag>".length()-1,rowData.indexOf("</StatusFlag>")):"";
						}
						
						rowVal = rowVal.substring(0,rowVal.indexOf(rowData))+ rowVal.substring(rowVal.indexOf(rowData)+rowData.length());
							
						countwhilchk++;
						if(countwhilchk == 50)
						{
							countwhilchk = 0;
							break;
						}
					
					 }
					
					// Addition of Cust Status and Dormancy in Dedupe window added on 18/10/2020
					String CustStatus = (rowVal.contains("<CustStatus>")) ? rowVal.substring(rowVal.indexOf("<CustStatus>")+"</CustStatus>".length()-1,rowVal.indexOf("</CustStatus>")):"--";
					CustDormancy = (rowVal.contains("<CustDormancy>")) ? rowVal.substring(rowVal.indexOf("<CustDormancy>")+"</CustDormancy>".length()-1,rowVal.indexOf("</CustDormancy>")):"";
					if("Y".equalsIgnoreCase(CustDormancy.trim()))
						CustDormancy = "dormant";
					
					//HashMap<String,String> obj1= new HashMap<String,String>();
					//obj1.put("WI_NAME",processInstanceID);
					
					//obj1.put("CIF_ID", MainCifId);
					DedupeGridCIFID.add(MainCifId);
					//obj1.put("CustomerFULL_NAME", MainCustomerName);
					DedupeGridFullName.add(MainCustomerName);
					//obj1.put("DOB", MainDateOfBirth);
					DedupeGridDOB.add(MainDateOfBirth);
					//obj1.put("GENDER", MainGender);
					DedupeGridGender.add(MainGender);
					//obj1.put("EMIRATES_ID", MainEmiratesID);
					DedupeGridEmiratesID.add(MainEmiratesID);
					//obj1.put("PASSPORT_Number", MainPassportNo);
					DedupeGridPassportNo.add(MainPassportNo);
					//obj1.put("NATIONALITY", MainNationality);
					DedupeGridNationality.add(MainNationality);
					//obj1.put("RESIDENTIAL_ADDRESS", MainResAddress);
					DedupeGridResAddress.add(MainResAddress);
					//obj1.put("MOBILE_Number", MainMobileNo);
					DedupeGridMobNo.add(MainMobileNo);
					//obj1.put("IsBlackListed", BlacklistedStatusFlag);
					DedupeGridBlacklistedFlag.add(BlacklistedStatusFlag);
					//obj1.put("IsNegativeListed", NegativelistedStatusFlag);
					DedupeGridNegativelistedFlag.add(NegativelistedStatusFlag);
					//obj1.put("RetailCorpFlag", RetailCorpFlag);
					DedupeGridCIFStatus.add(CustStatus);//showing in pdf only
					DedupeGridIncorporationDate.add(MainIncorporationDate);//showing in pdf only
					DedupeGridIncorporationCountry.add(MainIncorporationCountry);//showing in pdf only
					DedupeGridIncorporationTLNo.add(MainTL);//showing in pdf only
					
					responseXML = responseXML.substring(0,responseXML.indexOf("<Customer>"))+ responseXML.substring(responseXML.indexOf("</Customer>")+"</Customer>".length());
				}
				
				PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", @@@@@@@@@@ : after add of dedupe details");
				
				DocName=(CompFlag.equalsIgnoreCase("C"))?"Dedupe_ForCompany":"Dedupe_ForIndividual";
				
				// generating PDF
				returnValue = GeneratePDF.PDFTemplate(processInstanceID, ws_name, MsgFormat, DocName, RelPartyGridDataMap);
				
				//Clearing data in Arraylist
				DedupeGridCIFID.clear();
				DedupeGridFullName.clear();
				DedupeGridDOB.clear();
				DedupeGridGender.clear();
				DedupeGridEmiratesID.clear();
				DedupeGridPassportNo.clear();
				DedupeGridNationality.clear();
				DedupeGridResAddress.clear();
				DedupeGridMobNo.clear();
				DedupeGridBlacklistedFlag.clear();
				DedupeGridNegativelistedFlag.clear();
				DedupeGridCIFStatus.clear();
				DedupeGridIncorporationDate.clear();
				DedupeGridIncorporationCountry.clear();
				DedupeGridIncorporationTLNo.clear();
				
			    PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Response of attach doc in dedupe call"+returnValue);

			    return "Success";
			}
			
			else
			{
				PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Error in Response of dedupe call"+return_code);
				return CallStatus;
			}
		
		}
		catch(Exception e)
		{
			PCLog.PCLogger.debug("Exception in DedupeCall Fn for WI: "+processInstanceID+", exception msg: "+e.getMessage()+", print exc: "+ e);
			return "Failure";
		}
	}
			
	public static String BlacklistCall(String cabinetName,String UserName,String sessionId,String sJtsIp, String iJtsPort , String processInstanceID,
			String ws_name, int socket_connection_timeout,int integrationWaitTime,
			HashMap<String, String> socketDetailsMap, HashMap<String, String> RelPartyGridDataMap)
	{	
		try
		{

			String DocDetXml="";
			String NATIONALITY = "";			
			String DOB = "";
			String MidName = "";
			String FullName = "";
			String MobileNumberDetails = "";
			String MobileNumber = "";
			String CIF_ID="";
			String First_Name="";
			String Last_Name="";
			String Maritalstatus="";
			String OrganizationDetails = "";
			String PersonalDetails = "";
			String CustomerType = "";
			String RetailCorpFlag = "";
	
			
			CIF_ID = RelPartyGridDataMap.get("CIF");

			String CompFlag = RelPartyGridDataMap.get("COMPANYFLAG");
						
			CustomerType = 	"<CustomerType>"+CompFlag+"</CustomerType>";
			RetailCorpFlag = "<RetailCorpFlag>"+CompFlag+"</RetailCorpFlag>";
			
			if("R".equalsIgnoreCase(CompFlag))
			{
				if(!(RelPartyGridDataMap.get("EMIRATESID").equals("")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>EMID</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("EMIRATESID")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
				if(!(RelPartyGridDataMap.get("PASSPORTNUMBER").equals("")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>PPT</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("PASSPORTNUMBER")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
				if(!(RelPartyGridDataMap.get("VISANUMBER").equals("")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>VISA</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("VISANUMBER")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
				
				PersonalDetails = "<PersonDetails>";
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("FIRSTNAME")))
					PersonalDetails = PersonalDetails+"<FirstName>"+RelPartyGridDataMap.get("FIRSTNAME")+"</FirstName>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("MIDDLENAME")))
					PersonalDetails = PersonalDetails+"<MiddleName>"+RelPartyGridDataMap.get("MIDDLENAME")+"</MiddleName>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("LASTNAME")))
					PersonalDetails = PersonalDetails+"<LastName>"+RelPartyGridDataMap.get("LASTNAME")+"</LastName>";
				
				String FullName1 = RelPartyGridDataMap.get("FIRSTNAME") +" "+RelPartyGridDataMap.get("LASTNAME");
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("MIDDLENAME")))
					FullName1 = RelPartyGridDataMap.get("FIRSTNAME") +" "+ RelPartyGridDataMap.get("MIDDLENAME") +" "+RelPartyGridDataMap.get("LASTNAME");
				
				if(!"".equalsIgnoreCase(FullName1.trim()))
					PersonalDetails = PersonalDetails+"<FullName>"+FullName1+"</FullName>";

				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("NATIONALITY")))
					PersonalDetails = PersonalDetails+"<Nationality>"+RelPartyGridDataMap.get("NATIONALITY")+"</Nationality>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("DATEOFBIRTH")))
					PersonalDetails = PersonalDetails+"<DateOfBirth>"+RelPartyGridDataMap.get("DATEOFBIRTH")+"</DateOfBirth>";
				
				PersonalDetails = PersonalDetails+"</PersonDetails>";
				
			}
			else if("C".equalsIgnoreCase(CompFlag))
			{
				if(!(RelPartyGridDataMap.get("TL_NUMBER").equals("")))
				{
					DocDetXml = DocDetXml+"<Document>\n" +
						"<DocumentType>TDLIC</DocumentType>\n" +
						"<DocumentRefNumber>"+RelPartyGridDataMap.get("TL_NUMBER")+"</DocumentRefNumber>\n" +
					"</Document>";
				}
				
				OrganizationDetails = "";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("COMPANY_NAME")))
					OrganizationDetails = OrganizationDetails + "<CorporateName>"+RelPartyGridDataMap.get("COMPANY_NAME")+"</CorporateName>";
				
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("COUNTRY"))) 
					OrganizationDetails = OrganizationDetails + "<CountryOfIncorporation>"+RelPartyGridDataMap.get("COUNTRY")+"</CountryOfIncorporation>";
						
				if(!"".equalsIgnoreCase(RelPartyGridDataMap.get("DATEOFINCORPORATION"))) 			
					OrganizationDetails = OrganizationDetails + "<DateOfIncorporation>"+RelPartyGridDataMap.get("DATEOFINCORPORATION")+"</DateOfIncorporation>";
			
				if(!OrganizationDetails.equalsIgnoreCase(""))
					OrganizationDetails = "<OrganizationDetails>"+OrganizationDetails+"</OrganizationDetails>";
			
			}						
			
			if (!"".equalsIgnoreCase(RelPartyGridDataMap.get("RELMOBILENUMBER")))
			{
				String fullMobNo = RelPartyGridDataMap.get("RELMOBILENUMBER");
				MobileNumber=fullMobNo;
				MobileNumberDetails = "<ContactDetails>\n"+
						"<PhoneFax>\n"+
							"<PhoneType>Phone</PhoneType>\n"+
							"<PhoneValue>"+MobileNumber+"</PhoneValue>\n"+
						"</PhoneFax>\n"+
					"</ContactDetails>";
			}
		
						
			java.util.Date d1 = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
			String DateExtra2 = sdf1.format(d1)+"+04:00";
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
			String ReqDateTime = sdf2.format(d1);
			
			StringBuilder sInputXML = new StringBuilder("<EE_EAI_MESSAGE>\n" +
					"<EE_EAI_HEADER>\n" +
					"<MsgFormat>BLACKLIST_DETAILS</MsgFormat>\n" +
					"<MsgVersion>0001</MsgVersion>\n" +
					"<RequestorChannelId>BPM</RequestorChannelId>\n" +
					"<RequestorUserId>RAKUSER</RequestorUserId>\n" +
					"<RequestorLanguage>E</RequestorLanguage>\n" +
					"<RequestorSecurityInfo>secure</RequestorSecurityInfo>\n" +
					"<ReturnCode>0000</ReturnCode>\n" +
					"<ReturnDesc>REQ</ReturnDesc>\n" +
					"<MessageId>BPM_BLACK_LIST_009</MessageId>\n" +
					"<Extra1>REQ||BPM.123</Extra1>\n" +
					"<Extra2>"+DateExtra2+"</Extra2>\n" +
					"</EE_EAI_HEADER>" +
						"<CustomerBlackListRequest>"+
						"<BankId>RAK</BankId><CIFID>"+CIF_ID+"</CIFID>"+CustomerType+" " +
						" "+RetailCorpFlag+"<EntityType>All</EntityType>" +
						" "+PersonalDetails+" "+
						OrganizationDetails+MobileNumberDetails+DocDetXml+"</CustomerBlackListRequest>\n" +
					"</EE_EAI_MESSAGE>");
			
			PCLog.PCLogger.debug("Blacklist input XML: "+sInputXML);

	
			String responseXML =socketConnection(cabinetName, CommonConnection.getUsername(), sessionId,sJtsIp,
					 iJtsPort,  processInstanceID,  ws_name, integrationWaitTime, socket_connection_timeout,
					  socketDetailsMap, sInputXML);
			
			PCLog.PCLogger.debug("responseXML: "+responseXML);
	
			XMLParser xmlParserDetails= new XMLParser(responseXML);
		    String return_code = xmlParserDetails.getValueOf("ReturnCode");
		    PCLog.PCLogger.debug("Return Code: "+return_code);
	
		    String return_desc = xmlParserDetails.getValueOf("ReturnDesc");
			
			if (return_desc.trim().equalsIgnoreCase(""))
				return_desc = xmlParserDetails.getValueOf("Description").replace("'", "");
			
		    String MsgFormat = xmlParserDetails.getValueOf("MsgFormat");
			String MsgId = "";
			if (responseXML.contains("<MessageId>"))
				MsgId = xmlParserDetails.getValueOf("MessageId");
			
		    PCLog.PCLogger.debug("Return Desc: "+return_desc);
		    
		    String CallStatus = "";
		    if(return_code.equals("0000") || return_code.equals("CINF0516"))
		    	CallStatus="Success";
		    else
		    	CallStatus="Failure~"+"Blacklist Call~"+MsgId+"~"+return_desc;
		    
		    
		    if(return_code.equals("0000"))
			{
				while(responseXML.contains("<Customer>"))
				{
					String colNames="";
				    String colValues="";
					String MainCifId = "";
					String MainCifStatus = "";
					String MainEmiratesID = "";
					String MainPassportNo="";
					String MainMobileNo="";
					String MainCustomerName="";
					String MainBlacklistFlag="";
					String MainNegatedFlag="";
					String MainDOB="";
					String MainTLNo="";
					String MainDateOfIncorp="";
					String MainCountryOfIncorp="";
					String MainBlacklistDate="";
					String MainNegatedDate="";
					
					rowVal = responseXML.substring(responseXML.indexOf("<Customer>"),responseXML.indexOf("</Customer>")+"</Customer>".length());
					//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", rowVal : "+rowVal);
					if(rowVal.equalsIgnoreCase("<Customer></Customer>"))
						return "No result";
					MainCifId = (rowVal.contains("<CIFID>")) ? rowVal.substring(rowVal.indexOf("<CIFID>")+"</CIFID>".length()-1,rowVal.indexOf("</CIFID>")):"";
					MainCifStatus = (rowVal.contains("<CustomerStatus>")) ? rowVal.substring(rowVal.indexOf("<CustomerStatus>")+"</CustomerStatus>".length()-1,rowVal.indexOf("</CustomerStatus>")):"";
                    
					// customer full name
                    String FirstName = (rowVal.contains("<FirstName>")) ? rowVal.substring(rowVal.indexOf("<FirstName>")+"</FirstName>".length()-1,rowVal.indexOf("</FirstName>")):"";
                    String MiddleName = (rowVal.contains("<MiddleName>")) ? rowVal.substring(rowVal.indexOf("<MiddleName>")+"</MiddleName>".length()-1,rowVal.indexOf("</MiddleName>")):"";
                    String LastName = (rowVal.contains("<LastName>")) ? rowVal.substring(rowVal.indexOf("<LastName>")+"</LastName>".length()-1,rowVal.indexOf("</LastName>")):"";
                    String fullName=FirstName+" "+MiddleName+" "+LastName;
					MainCustomerName = (rowVal.contains("<fullName>")) ? rowVal.substring(rowVal.indexOf("<fullName>")+"</fullName>".length()-1,rowVal.indexOf("</fullName>")):"";
					
					if("".equalsIgnoreCase(fullName.trim()))
						fullName = (rowVal.contains("<CorporateName>")) ? rowVal.substring(rowVal.indexOf("<CorporateName>")+"</CorporateName>".length()-1,rowVal.indexOf("</CorporateName>")):"";
					
					MainDOB = (rowVal.contains("<DateOfBirth>")) ? rowVal.substring(rowVal.indexOf("<DateOfBirth>")+"</DateOfBirth>".length()-1,rowVal.indexOf("</DateOfBirth>")):"";
					MainCountryOfIncorp = (rowVal.contains("<CountryOfIncorporation>")) ? rowVal.substring(rowVal.indexOf("<CountryOfIncorporation>")+"</CountryOfIncorporation>".length()-1,rowVal.indexOf("</CountryOfIncorporation>")):"";
					MainDateOfIncorp = (rowVal.contains("<DateOfIncorporation>")) ? rowVal.substring(rowVal.indexOf("<DateOfIncorporation>")+"</DateOfIncorporation>".length()-1,rowVal.indexOf("</DateOfIncorporation>")):"";
					int countwhilchk = 0;
					while(rowVal.contains("<Document>"))
					{							
						String rowData = rowVal.substring(rowVal.indexOf("<Document>"),rowVal.indexOf("</Document>")+"</Document>".length());
						String DocumentType = (rowData.contains("<DocumentType>")) ? rowData.substring(rowData.indexOf("<DocumentType>")+"</DocumentType>".length()-1,rowData.indexOf("</DocumentType>")):"";
						//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", DocumentType "+DocumentType);
						//Emirates ID
						if (DocumentType.equalsIgnoreCase("EMID"))
						{
							MainEmiratesID = rowData.substring(rowData.indexOf("<DocumentRefNumber>")+"<DocumentRefNumber>".length(),rowData.indexOf("</DocumentRefNumber>"));
						}							
						//passport number
						if (DocumentType.equalsIgnoreCase("PPT"))
						{
							MainPassportNo = rowData.substring(rowData.indexOf("<DocumentRefNumber>")+"<DocumentRefNumber>".length(),rowData.indexOf("</DocumentRefNumber>"));
						}
						//passport number
						if (DocumentType.equalsIgnoreCase("TDLIC"))
						{
							MainTLNo = rowData.substring(rowData.indexOf("<DocumentRefNumber>")+"<DocumentRefNumber>".length(),rowData.indexOf("</DocumentRefNumber>"));
						}
							rowVal = rowVal.substring(0,rowVal.indexOf(rowData))+ rowVal.substring(rowVal.indexOf(rowData)+rowData.length());
							
							countwhilchk++;
							if(countwhilchk == 50)
							{
								countwhilchk = 0;
								break;
							}
					
					 }
					//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", MainEmiratesID "+MainEmiratesID);
					//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", MainPassportNo "+MainPassportNo);
					
					countwhilchk = 0;
					while(rowVal.contains("<StatusInfo>"))
					{
						String rowData = rowVal.substring(rowVal.indexOf("<StatusInfo>"),rowVal.indexOf("</StatusInfo>")+"</StatusInfo>".length());
						
						String rowDataMain = rowVal.substring(rowVal.indexOf("<StatusInfo>"),rowVal.indexOf("</StatusInfo>")+"</StatusInfo>".length());
											
						String StatusType = (rowData.contains("<StatusType>")) ? rowData.substring(rowData.indexOf("<StatusType>")+"</StatusType>".length()-1,rowData.indexOf("</StatusType>")):"";
						//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", StatusType "+StatusType);
						// Blacklist Flag
						if (StatusType.equalsIgnoreCase("Black List"))
						{
							MainBlacklistFlag = rowData.substring(rowData.indexOf("<StatusFlag>")+"<StatusFlag>".length(),rowData.indexOf("</StatusFlag>"));
							if(MainBlacklistFlag.equalsIgnoreCase("Y"))
							{
								int countwhilchk1=0;
								MainBlacklistDate="";
								while(rowData.contains("<StatusDetails>"))
								{
									String rowData1 = rowData.substring(rowData.indexOf("<StatusDetails>"),rowData.indexOf("</StatusDetails>")+"</StatusDetails>".length());
									if("".equalsIgnoreCase(MainBlacklistDate))
									{
										MainBlacklistDate = (rowData1.contains("<CreationDate>")) ? rowData1.substring(rowData1.indexOf("<CreationDate>")+"</CreationDate>".length()-1,rowData1.indexOf("</CreationDate>")):"";
									}
									else
									{
										String temp=(rowData1.contains("<CreationDate>")) ? rowData1.substring(rowData1.indexOf("<CreationDate>")+"</CreationDate>".length()-1,rowData1.indexOf("</CreationDate>")):"";
										MainBlacklistDate=MainBlacklistDate+"\n"+temp;
									}
									rowData = rowData.substring(0,rowData.indexOf(rowData1))+ rowData.substring(rowData.indexOf(rowData1)+rowData1.length());
									PCLog.PCLogger.debug("Creation Date:"+MainBlacklistDate);
									countwhilchk1++;
									if(countwhilchk1 == 50)
									{
										countwhilchk1 = 0;
										break;
									}
								}
							}
						}
						
						// Negated Flag
						if (StatusType.equalsIgnoreCase("Negative List"))
						{
							MainNegatedFlag =rowData.substring(rowData.indexOf("<StatusFlag>")+"<StatusFlag>".length(),rowData.indexOf("</StatusFlag>"));
							if(MainNegatedFlag.equalsIgnoreCase("Y"))
							{
								int countwhilchk1=0;
								MainNegatedDate="";
								while(rowData.contains("<StatusDetails>"))
								{
									String rowData1 = rowData.substring(rowData.indexOf("<StatusDetails>"),rowData.indexOf("</StatusDetails>")+"</StatusDetails>".length());
									if("".equalsIgnoreCase(MainNegatedDate))
									{
										MainNegatedDate = (rowData1.contains("<CreationDate>")) ? rowData1.substring(rowData1.indexOf("<CreationDate>")+"</CreationDate>".length()-1,rowData1.indexOf("</CreationDate>")):"";
									}
									else
									{
										String temp=(rowData1.contains("<CreationDate>")) ? rowData1.substring(rowData1.indexOf("<CreationDate>")+"</CreationDate>".length()-1,rowData1.indexOf("</CreationDate>")):"";
										MainNegatedDate=MainNegatedDate+"\n"+temp;
									}
									PCLog.PCLogger.debug("Creation Date:"+MainBlacklistDate);
									rowData = rowData.substring(0,rowData.indexOf(rowData1))+ rowData.substring(rowData.indexOf(rowData1)+rowData1.length());
									
									countwhilchk1++;
									if(countwhilchk1 == 50)
									{
										countwhilchk1 = 0;
										break;
									}
								}
							}
						}
						rowVal = rowVal.substring(0,rowVal.indexOf(rowDataMain))+ rowVal.substring(rowVal.indexOf(rowDataMain)+rowDataMain.length());
					
						countwhilchk++;
						if(countwhilchk == 50)
						{
							countwhilchk = 0;
							break;
						}
					}	
					
					countwhilchk = 0;
					while(rowVal.contains("<PhoneFax>"))
					{							
						String rowData = rowVal.substring(rowVal.indexOf("<PhoneFax>"),rowVal.indexOf("</PhoneFax>")+"</PhoneFax>".length());
						String PhoneType = (rowData.contains("<PhoneType>")) ? rowData.substring(rowData.indexOf("<PhoneType>")+"</PhoneType>".length()-1,rowData.indexOf("</PhoneType>")):"";
						//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", PhoneType "+PhoneType);
						
						if (PhoneType.equalsIgnoreCase("CELLPH1"))
						{
							MainMobileNo = (rowData.contains("<PhoneValue>")) ? rowData.substring(rowData.indexOf("<PhoneValue>")+"</PhoneValue>".length()-1,rowData.indexOf("</PhoneValue>")):"";
						}							
						
						rowVal = rowVal.substring(0,rowVal.indexOf(rowData))+ rowVal.substring(rowVal.indexOf(rowData)+rowData.length());
							
						countwhilchk++;
						if(countwhilchk == 50)
						{
							countwhilchk = 0;
							break;
						}					
					 }
					
					
					String MatchStatus = "false";
					if("Y".equalsIgnoreCase(MainBlacklistFlag.trim()) || "Y".equalsIgnoreCase(MainNegatedFlag.trim()))
						MatchStatus = "true";
					
					BlacklistGridCIFID.add(MainCifId);
					BlacklistGridCifStatus.add(MainCifStatus);
					BlacklistGridFullName.add(fullName);
					BlacklistGridEmiratesID.add(MainEmiratesID);
					BlacklistGridPassportNo.add(MainPassportNo);
					//BlacklistGridResAddress.add("");
					BlacklistGridDOB.add(MainDOB);
					BlacklistGridMobNo.add(MainMobileNo);
					BlacklistGridBlacklistedFlag.add(MainBlacklistFlag);
					BlacklistGridNegatedFlag.add(MainNegatedFlag);
					BlacklistGridNegatedDate.add(MainNegatedDate);
					BlacklistGridBlacklistedDate.add(MainBlacklistDate);
					BlacklistGridDateOfIncorp.add(MainDateOfIncorp);
					BlacklistGridCountryOfIncorp.add(MainCountryOfIncorp);
					BlacklistGridTLNo.add(MainTLNo);
										
				    // moving forward for next RelatedParty
					responseXML = responseXML.substring(0,responseXML.indexOf("<Customer>"))+ responseXML.substring(responseXML.indexOf("</Customer>")+"</Customer>".length());
				}
				//PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Size After Adding Blacklist : ");
				PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", @@@@@@@@@@ : after add of Blacklist details");
				
				//CheckGridDataMap.put("BLACKLIST_STATUS", "Success");
				//****************************
			  
		    	CompFlag = RelPartyGridDataMap.get("COMPANYFLAG"); // also check in dedupe for the same
				
		    	DocName=(CompFlag.equalsIgnoreCase("C"))?"Blacklist_ForCompany":"Blacklist_ForIndividual";
				
				returnValue = GeneratePDF.PDFTemplate(processInstanceID, ws_name, MsgFormat, DocName, RelPartyGridDataMap);
			    
			    //*****************************
				
				//Clearing data in Arraylist for MainCIF
				BlacklistGridCIFID.clear();
				BlacklistGridCifStatus.clear();
				BlacklistGridFullName.clear();
				BlacklistGridEmiratesID.clear();
				BlacklistGridPassportNo.clear();
				BlacklistGridDOB.clear();
				BlacklistGridMobNo.clear();
				BlacklistGridBlacklistedFlag.clear();
				BlacklistGridBlacklistedDate.clear();  
				BlacklistGridNegatedFlag.clear(); 
				BlacklistGridNegatedDate.clear();  
				BlacklistGridDateOfIncorp.clear();  
				BlacklistGridCountryOfIncorp.clear();  
				BlacklistGridTLNo.clear(); 
				
			    PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Response of attach doc in dedupe call"+returnValue);
								
				return "Success";
			}
		    else if (return_code.equals("CINF0516"))
		    {

		    	CompFlag = RelPartyGridDataMap.get("COMPANYFLAG");
				
		    	DocName=(CompFlag.equalsIgnoreCase("C"))?"Blacklist_ForCompany":"Blacklist_ForIndividual";
				
				returnValue = GeneratePDF.PDFTemplate(processInstanceID, ws_name, MsgFormat, DocName, RelPartyGridDataMap);
				PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Response of attach doc in dedupe call: "+returnValue);
				
		    	// No records found
		    	return "Success";
		    }
			else
			{
				PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Error in Response of dedupe call"+return_code);
				return CallStatus;
			}
		    
		}
		catch(Exception e)
		{
			PCLog.PCLogger.debug("Exception in BlacklistCall Fn for WI: "+processInstanceID+", exception:"+e.getMessage()+", print:"+ e);
			return "Failure";
		}
	}

	public static	String FircosoftCall(String cabinetName,String UserName,String sessionId,String sJtsIp, String iJtsPort , String processInstanceID,
			String ws_name, int socket_connection_timeout,int integrationWaitTime,
			HashMap<String, String> socketDetailsMap, HashMap<String, String> RelPartyGridDataMap)
	{
		try
		{
			//PCLog.PCLogger.debug("inside FircosoftCall Fn");
			String RetStatus = "";
			String mqInputRequest = null;
			String DocDetXml="";
			String CIF_ID="";
			String NATIONALITY="";
			String FullName="";
			String DOB="";
			String gender="";
			String RESIDENCEADDRCOUNTRY="";
			String PASSPORT_NUMBER="";
			String ReferenceNo = getFircoReferenceNumber(processInstanceID);
			String MidName="";
			String Details_For="";
			String recordType = "";
						
		
			String CompayFlag = RelPartyGridDataMap.get("COMPANYFLAG");
			NATIONALITY = RelPartyGridDataMap.get("NATIONALITY");
			CIF_ID = RelPartyGridDataMap.get("CIF");

			PASSPORT_NUMBER = RelPartyGridDataMap.get("PASSPORTNUMBER");				
			
			if("C".equalsIgnoreCase(CompayFlag))
			{
				FullName = RelPartyGridDataMap.get("COMPANY_NAME").trim();
				DOB = RelPartyGridDataMap.get("DATEOFINCORPORATION");
				RESIDENCEADDRCOUNTRY = RelPartyGridDataMap.get("COUNTRY");
				recordType = "C";
			}
			
			if("R".equalsIgnoreCase(CompayFlag))
			{
				String FirstName = RelPartyGridDataMap.get("FIRSTNAME").trim();
				String MiddleName = RelPartyGridDataMap.get("MIDDLENAME").trim();
				String LastName = RelPartyGridDataMap.get("LASTNAME").trim();
			
				FullName = FirstName + " " + LastName;
				if(!"".equalsIgnoreCase(MiddleName))
					FullName = FirstName+" "+ MiddleName + " " + LastName;
									
				gender = RelPartyGridDataMap.get("GENDER");
				if((gender).equalsIgnoreCase("F"))
					gender = "Female";
				if((gender).equalsIgnoreCase("M"))
					gender = "Male";
				
				DOB = RelPartyGridDataMap.get("DATEOFBIRTH");
				RESIDENCEADDRCOUNTRY = RelPartyGridDataMap.get("COUNTRY_RESIDENCE");
				recordType = "I";
			}	
						
			java.util.Date d1 = new Date();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
			String DateExtra2 = sdf1.format(d1)+"+04:00";
			
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
			String ReqDateTime = sdf2.format(d1);
			String RequestingUnitName = "CENTRALOPERATIONSDUBAI"; // earlier it was CENTRALOPERATIONSDUBAI, changed as per raina's mail subject - Digital Loan - Fircotrust
			StringBuilder sInputXML = new StringBuilder("<EE_EAI_MESSAGE>\n"+
								"<EE_EAI_HEADER>\n"+
								"<MsgFormat>COMPLIANCE_CHECK</MsgFormat>\n"+
								"<MsgVersion>0001</MsgVersion>\n"+
								"<RequestorChannelId>BPM</RequestorChannelId>\n"+
								"<RequestorUserId>RAKUSER</RequestorUserId>\n"+
								"<RequestorLanguage>E</RequestorLanguage>\n"+
								"<RequestorSecurityInfo>secure</RequestorSecurityInfo>\n"+
								"<ReturnCode>911</ReturnCode>\n"+
								"<ReturnDesc>Issuer Timed Out</ReturnDesc>\n"+
								"<MessageId>Test123456</MessageId>\n"+
								"<Extra1>REQ||PERCOMER.PERCOMER</Extra1>\n"+
								"<Extra2>"+DateExtra2+"</Extra2>\n"+
								"</EE_EAI_HEADER>\n"+
								"<ComplianceCheckRequest><DisplayAlertsFlag>0</DisplayAlertsFlag>" +
								"<RetryRequiredFlag>N</RetryRequiredFlag><RequestingUnitName>"+RequestingUnitName+"</RequestingUnitName>" +
								"<RecordType>"+recordType+"</RecordType>"+
								"<ReferenceNo>"+ReferenceNo+"</ReferenceNo>" +
								"<EntityName><FullName>"+ FullName+"</FullName></EntityName>" +
								"<Gender>"+gender+"</Gender>" +
								"<DateOfBirthOrIncorporation>"+DOB+"</DateOfBirthOrIncorporation>" +
								"<Nationality>"+NATIONALITY+"</Nationality>" +
								"<CountryOfResidence>"+RESIDENCEADDRCOUNTRY+"</CountryOfResidence>" +
								"<PassportNumber>"+PASSPORT_NUMBER+"</PassportNumber>" +
								"<PreviousPassportNo>ok</PreviousPassportNo>" +
						"</ComplianceCheckRequest>"+
					"</EE_EAI_MESSAGE>");				
		
	    	
			PCLog.PCLogger.debug("firco input XML: "+sInputXML);

//			String responseXML = getRequestXML(cabinetName, sessionId, processInstanceID, ws_name, CommonConnection.getUsername(), sInputXML);

			String responseXML =socketConnection(cabinetName, CommonConnection.getUsername(), sessionId,sJtsIp,
					 iJtsPort,  processInstanceID,  ws_name, integrationWaitTime, socket_connection_timeout,
					  socketDetailsMap, sInputXML);
			
			PCLog.PCLogger.debug("Response XML is for FIRCO integration call is : "+responseXML);
			
			XMLParser xmlParserSocketDetails= new XMLParser(responseXML);
		    String return_code = xmlParserSocketDetails.getValueOf("ReturnCode");
		    PCLog.PCLogger.debug("Return Code: "+return_code);

		    String return_desc = xmlParserSocketDetails.getValueOf("ReturnDesc").replace("'", "");
			
			if (return_desc.trim().equalsIgnoreCase(""))
				return_desc = xmlParserSocketDetails.getValueOf("Description").replace("'", "");
			
			String MsgId = "";
			if (responseXML.contains("<MessageId>"))
				MsgId = xmlParserSocketDetails.getValueOf("MessageId");
			
			String MsgFormat = xmlParserSocketDetails.getValueOf("MsgFormat");
			
		    PCLog.PCLogger.debug("Return Desc: "+return_desc);
		    		    
		  // Inserting in Integration details table
		    String CallStatus = "";
		    if(return_code.equals("0000") || return_code.equals("FFF002") || return_code.equals("FFF_OK") || return_code.equals("FFFBAD") || return_code.equals("FFFPEN"))
		    	CallStatus="Success";
		    else
		    	CallStatus="Not Checked~"+"Firco Call~"+MsgId+"~"+return_desc;
		    
//		    java.util.Date d2 = new Date();
//		    String ResDateTime = sdf2.format(d2);
//		    String TableName = "USR_0_PC_UID_DTLS"; // toDoImp
//		    String columnnames = "UID_NUMBER, REMARKS, WI_NAME, insertionOrderId, NAME";
//		    String columnvalues = "'"+processInstanceID+"','"+CIF_ID+"','','COMPLIANCE_CHECK','','"+ReqDateTime+"','"+CallStatus+"','"+MsgId+"','"+ResDateTime+"','"+return_code+"','"+return_desc+"' ";
//			String InputXML = CommonMethods.apInsert(CommonConnection.getCabinetName(), CommonConnection.getSessionID(PCLog.PCLogger, false), columnnames, columnvalues, TableName);
//			PCLog.PCLogger.debug("Input XML for apInsert COMPLIANCE_CHECK "+TableName+" Table : "+InputXML);
//
//			String OutputXML=PC.WFNGExecute(InputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(),1);
//			PCLog.PCLogger.debug("Output XML for COMPLIANCE_CHECK apInsert "+TableName+" Table : "+OutputXML);
//
//			XMLParser sXMLParserChild= new XMLParser(OutputXML);
//		    String StrMainCode = sXMLParserChild.getValueOf("MainCode");
//		    if (StrMainCode.equals("0"))
//			   	PCLog.PCLogger.debug("Successful in Inserting the record in : "+TableName);	
//		    else
//		       	PCLog.PCLogger.debug("Error in Executing apInsert sOutputXML : "+OutputXML);
		    ////////////////////////////////////////
		    
		    
		    if(return_code.equals("0000") || return_code.equals("FFF002") || return_code.equals("FFF_OK") || return_code.equals("FFFBAD") || return_code.equals("FFFPEN"))
			{
				//JSONArray jsonArray1=new JSONArray();
				if(responseXML.contains("<AlertDetails>"))
				{	
						String AlertDetailsTagResponse=responseXML.substring(responseXML.indexOf("<AlertDetails>")+"</AlertDetails>".length()-1,responseXML.indexOf("</AlertDetails>"));
					   
					   rowVal = responseXML.substring(responseXML.indexOf("<AlertDetails>")+"</AlertDetails>".length()-1,responseXML.indexOf("</AlertDetails>"));
						//docdetails.put(eElement.getElementsByTagName("AlertDetails").item(0).getTextContent(), rowvalues);
						//System.out.println("values = "+rowvalues);
					   
					   String StatusBehavior = "";
					   String StatusName = "";
					   if(responseXML.contains("<StatusBehavior>"))
						   StatusBehavior = responseXML.substring(responseXML.indexOf("<StatusBehavior>")+"</StatusBehavior>".length()-1,responseXML.indexOf("</StatusBehavior>"));
					   
					   if(responseXML.contains("<StatusName>"))
						   StatusName = responseXML.substring(responseXML.indexOf("<StatusName>")+"</StatusName>".length()-1,responseXML.indexOf("</StatusName>"));
					   
					   String[] arrOfStr1 = null; 
					   if (rowVal.contains("Suspect detected #1"))
					   {						   	
							arrOfStr1 = rowVal.split("=============================");
							if(arrOfStr1.length==2)
							{
								PCLog.PCLogger.debug("No Records Found : ");
								
						    	FircoGridREFERENCENO.add(ReferenceNo);
						    	DocName = "Fircosoft_ForSignatories"; // company/signatories
								returnValue = GeneratePDF.PDFTemplate(processInstanceID,ws_name,MsgFormat,DocName,RelPartyGridDataMap);
								FircoGridREFERENCENO.clear();
							    
								return "Record Not Found";
							}
							else if(arrOfStr1.length>2)
							{
								try {
									int FIRCOSOFTGridsize = 0;
							    	String colNames = "";
							    	String colValues = "";
									
		                            for(int i=1;i<arrOfStr1.length-1;i++) // toDo
									{
		                            	String sRecords=arrOfStr1[i].replace(": \n", ":"); 
		                            	sRecords=sRecords.replace(":\n", ":");
		                            	PCLog.PCLogger.debug("Firco sRecords: "+sRecords);
										Map<String,String> Columnvalues = new HashMap<String,String>(); 
										BufferedReader bufReader = new BufferedReader(new StringReader(sRecords));
										String line=null;
										while( (line=bufReader.readLine()) != null )
										{
											String[] PDFColumns = {"OFAC ID", "NAME", "MATCHINGTEXT", "ORIGIN", "DESIGNATION", "DATE OF BIRTH", "USER DATA 1", "NATIONALITY", "PASSPORT", "ADDITIONAL INFOS"};
											for(int k=0;k<PDFColumns.length;k++)
											{
												if(line.contains(PDFColumns[k]+":"))
												{
													String colData = "";
													String [] tmp = line.split(":");
													//PCLog.PCLogger.debug("tmp.length : "+tmp.length+", line : "+line);
												
													//********below loop added for handling hardcoded Fircosoft XML in offshore dev server
													if(tmp.length == 1)
														colData="";//***************************
													else if(tmp[1].trim().equalsIgnoreCase("Synonyms") || tmp[1].trim().equalsIgnoreCase("none") || tmp[1].trim().equalsIgnoreCase(""))
														colData="";
													else
													{
														//colData=tmp[1].trim();
														for(int m=1; m<tmp.length; m++)
														{
															colData=colData+" "+tmp[m].trim();
														}
													}
													
													
													Columnvalues.put(PDFColumns[k],colData);
													PCLog.PCLogger.debug("ColName: "+PDFColumns[k]+", ColData: "+colData);
												}
											}
										}									
										
										PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Firco Response pasrsing done successfully");
													
										HashMap<String,String> obj1= new HashMap<String,String>();
										//JSONObject obj1=new JSONObject();
										
										//PCLog.PCLogger.debug("WINAME : "+getWorkitemName()+", WSNAME: "+getActivityName()+", ReferenceNo :"+ReferenceNo);
										obj1.put("WI_NAME",processInstanceID);
										
										obj1.put("CIF", RelPartyGridDataMap.get("CIF"));											
										Details_For=RelPartyGridDataMap.get("CIF");
																			
										FIRCOSOFTGridsize=FIRCOSOFTGridsize+1;
										obj1.put("SRNumber", String.valueOf(FIRCOSOFTGridsize));

										obj1.put("DETAILS_FOR", Details_For);
										obj1.put("OFAC_ID", Columnvalues.get("OFAC ID").toString().trim());
										//PCLog.PCLogger.debug("OFAC ID : "+Columnvalues.get("OFAC ID"));
										
										obj1.put("NAME", Columnvalues.get("NAME"));
										//PCLog.PCLogger.debug("NAME : "+Columnvalues.get("NAME"));
										
										obj1.put("MATCHINGTEXT", Columnvalues.get("MATCHINGTEXT"));
										//PCLog.PCLogger.debug("MATCHINGTEXT : "+Columnvalues.get("MATCHINGTEXT"));
										
										obj1.put("ORIGIN", Columnvalues.get("ORIGIN"));
										//PCLog.PCLogger.debug("ORIGIN : "+Columnvalues.get("ORIGIN"));
										
										obj1.put("DESIGNATION", Columnvalues.get("DESIGNATION"));
										//PCLog.PCLogger.debug("DESIGNATION : "+Columnvalues.get("DESIGNATION"));
										
										obj1.put("DATEOFBIRTHTEXT", Columnvalues.get("DATE OF BIRTH"));
										//PCLog.PCLogger.debug("DATE OF BIRTH : "+Columnvalues.get("DATE OF BIRTH"));
										
										obj1.put("USERDATA1", Columnvalues.get("USER DATA 1"));
										//PCLog.PCLogger.debug("User Data 1 : "+Columnvalues.get("USER DATA 1"));
										
										obj1.put("NATIONALITY", Columnvalues.get("NATIONALITY"));
										//PCLog.PCLogger.debug("NATIONALITY : "+NATIONALITY);
										
										obj1.put("PASSPORT", Columnvalues.get("PASSPORT"));
										//PCLog.PCLogger.debug("PASSPORT : "+Columnvalues.get("PASSPORT"));
										
										obj1.put("ADDITIONALINFO", Columnvalues.get("ADDITIONAL INFOS"));
										//PCLog.PCLogger.debug("ADDITIONAL : "+Columnvalues.get("ADDITIONAL INFOS"));
										
										obj1.put("MATCH_STATUS", "");
									//	obj1.put("USER_", PreviousPassportNo);
									//	obj1.put("REMARKS", Remarks);
										obj1.put("REFERENCE_NO", ReferenceNo);										
										
										FircoGridSRNo.add(Integer.toString(i));
										FircoGridOFACID.add(Columnvalues.get("OFAC ID"));
										FircoGridName.add(Columnvalues.get("NAME"));
										FircoGridMatchingText.add(Columnvalues.get("MATCHINGTEXT"));
										FircoGridOrigin.add(Columnvalues.get("ORIGIN"));
										FircoGridDestination.add(Columnvalues.get("DESIGNATION"));
										FircoGridDOB.add(Columnvalues.get("DATE OF BIRTH"));
										FircoGridUserData1.add(Columnvalues.get("User Data 1"));
										FircoGridNationality.add(Columnvalues.get("NATIONALITY"));
										FircoGridPassport.add(Columnvalues.get("PASSPORT"));
										FircoGridAdditionalInfo.add(Columnvalues.get("ADDITIONAL INFOS"));
										FircoGridREFERENCENO.add(ReferenceNo);
										
										//PCLog.PCLogger.debug("before FircoSoft for loop");
										
										DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmm");
										//Date date = new Date();
										
										//Appending Firco Output values to Firco DB Columns
										for(Map.Entry<String,String> map : obj1.entrySet())
										{
											PCLog.PCLogger.debug("iterating ... map.getKey() :"+map.getKey().toString()+", map.getValue() : "+map.getValue().toString());
											
											if(colNames.equals("") && !map.getValue().toString().equals(""))
									    	{
												//PCLog.PCLogger.debug("if ... map.getValue() : "+map.getValue().toString());
												
												colNames= map.getKey();
												
												if(map.getValue().toString().indexOf(",") != -1)
									    			colValues=map.getValue().replace(",", " ");
									    		else
													colValues=map.getValue();
									    	}
									    	else if(!colNames.equals("") && !map.getValue().toString().equals(""))
									    	{
									    		//PCLog.PCLogger.debug("else ... colNames : "+colNames);
									    		colNames= colNames+","+map.getKey();
									    		
									    		if(map.getValue().toString().indexOf(",") != -1)
									    			colValues=colValues+","+map.getValue().replace(",", " ");
									    		else
													colValues=colValues+","+map.getValue();
									    	}
										}
										
										colValues=colValues.replaceAll(",", "','");
									    PCLog.PCLogger.debug("Aftr replace colValues :"+colValues);								    
																			    
										
									    String TableName = "USR_0_PC_UID_DTLS"; 
									    String columnnames = "WI_NAME, UID_NUMBER, REMARKS, NAME";
									    String columnvalues = "'"+processInstanceID+"','"+Columnvalues.get("OFAC ID")+"','"+Columnvalues.get("ADDITIONAL INFOS")+"','"+Columnvalues.get("NAME")+"'";
										String InputXML = CommonMethods.apInsert(CommonConnection.getCabinetName(), CommonConnection.getSessionID(PCLog.PCLogger, false), columnnames, columnvalues, TableName);
										PCLog.PCLogger.debug("Input XML for apInsert COMPLIANCE_CHECK with "+TableName+" Table : "+InputXML);
							
										String OutputXML=PC.WFNGExecute(InputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(),1);
										PCLog.PCLogger.debug("Output XML for apInsert COMPLIANCE_CHECK with "+TableName+" Table : "+OutputXML);
							
										XMLParser sXMLParserChild= new XMLParser(OutputXML);
									    String StrMainCode = sXMLParserChild.getValueOf("MainCode");
									    if (StrMainCode.equals("0"))
										   	PCLog.PCLogger.debug("Successful in Inserting the record in : "+TableName);	
									    else
									       	PCLog.PCLogger.debug("Error in Executing apInsert sOutputXML : "+OutputXML);
									    
										PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", @@@@@@@@@@ for fircosoft detail call :::");
										
									}	
								}
								catch(Exception e)
								{
									PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Exception in parsing firco response: "+return_code);
									//objRespBean.setFircosoft_Details("Not Checked");									
									e.printStackTrace();
									return "Not Checked";	
								}
							    											
								PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", @@@@@@@@@@ : after add of fircosoft details 2");
																	
						    	DocName = RelPartyGridDataMap.get("COMPANYFLAG").equals("C") ? "Fircosoft_ForCompany" : "Fircosoft_ForIndividual"; // was fircosoft for company/signatories
							    
								returnValue = GeneratePDF.PDFTemplate(processInstanceID,ws_name,MsgFormat,DocName,RelPartyGridDataMap);
								FircoGridSRNo.clear();
								FircoGridOFACID.clear();
								FircoGridName.clear();
								FircoGridMatchingText.clear();
								FircoGridOrigin.clear();
								FircoGridDestination.clear();
								FircoGridDOB.clear();
								FircoGridUserData1.clear();
								FircoGridNationality.clear();
								FircoGridPassport.clear();
								FircoGridAdditionalInfo.clear();
								FircoGridREFERENCENO.clear();
								
							    PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Response of attach doc in dedupe call"+returnValue);
								
								return "Record Found";
							}
				   	} 
					else 
				   	{
				   		PCLog.PCLogger.debug("No Records Found : ");
				   		//objRespBean.setFircosoft_Details("Record Not Found");
				   		// attaching blank pdf when no record found.
				   		
				    	FircoGridREFERENCENO.add(ReferenceNo);
				    	DocName = RelPartyGridDataMap.get("COMPANYFLAG").equals("C") ? "Fircosoft_ForCompany" : "Fircosoft_ForIndividual";

						returnValue = GeneratePDF.PDFTemplate(processInstanceID,ws_name,MsgFormat,DocName,RelPartyGridDataMap);
						FircoGridREFERENCENO.clear();
				    
				   		
				   		return "Record Not Found";
				   	}
				}
				else
				{
					PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", not getting Alert Details tag in Response of Fircosoft call"+return_code);
					//objRespBean.setFircosoft_Details("Record Not Found");
					// attaching blank pdf when no record found.
			   		
			    	FircoGridREFERENCENO.add(ReferenceNo);
			    	DocName = RelPartyGridDataMap.get("COMPANYFLAG").equals("C") ? "Fircosoft_ForCompany" : "Fircosoft_ForIndividual";

					returnValue = GeneratePDF.PDFTemplate(processInstanceID,ws_name,MsgFormat,DocName,RelPartyGridDataMap);
					FircoGridREFERENCENO.clear();
			    
			   		//**************
					return "Record Not Found";
				}
				// attaching blank pdf when no record found.
		   		
			    	FircoGridREFERENCENO.add(ReferenceNo);
			    	DocName = RelPartyGridDataMap.get("COMPANYFLAG").equals("C") ? "Fircosoft_ForCompany" : "Fircosoft_ForIndividual";

					returnValue = GeneratePDF.PDFTemplate(processInstanceID,ws_name,MsgFormat,DocName,RelPartyGridDataMap);
					FircoGridREFERENCENO.clear();
			    
		   		//**************
				return "Record Not Found";
			}
			else
			{
				PCLog.PCLogger.debug("WINAME : "+processInstanceID+", WSNAME: "+ws_name+", Error in Response of Fircosoft call"+return_code);
				//objRespBean.setFircosoft_Details("Not Checked");
				return CallStatus;
			}
		}
		catch(Exception e)
		{
			PCLog.PCLogger.debug("Exception in FircosoftCall exception:"+e.getMessage()+", print:"+ e);
			return "Not Checked";
		}
	}

	public static String getFircoReferenceNumber(String workitemno)
	{
		if(!workitemno.equalsIgnoreCase(""))
		{
			workitemno = workitemno.split("-")[0]+"-"+workitemno.split("-")[1].replaceFirst("^0+(?!$)", "");
		}
		Timestamp localTimestamp = new Timestamp(System.currentTimeMillis());
		String date = Integer.toString(localTimestamp.getDate());
		if(date.length() == 1)
			date = "0"+date;
		
		int iMonth =localTimestamp.getMonth()+1;
		String month = Integer.toString(iMonth);
		if(month.length() == 1)
			month = "0"+month;
		
		int iYear = localTimestamp.getYear()+1900;
		String year = Integer.toString(iYear);

		String hour = Integer.toString(localTimestamp.getHours());
		if(hour.length() == 1)
			hour = "0"+hour;
		
		String minutes = Integer.toString(localTimestamp.getMinutes());
		if(minutes.length() == 1)
			minutes = "0"+minutes;
		
		String second = Integer.toString(localTimestamp.getSeconds());
		if(second.length() == 1)
			second = "0"+second;
		//String ReferenceNo=workitemno+"_"+System.currentTimeMillis()/1000*60;
		String ReferenceNo=workitemno+"-"+ date+month+year+hour+minutes+second;
		return ReferenceNo;	
	}
	
	static String socketConnection(String cabinetName, String username, String sessionId, String sJtsIp,
			String iJtsPort, String processInstanceID, String ws_name,
			int connection_timeout, int integrationWaitTime,HashMap<String, String> socketDetailsMap,  StringBuilder sInputXML)
	{

		String socketServerIP;
		int socketServerPort;
		Socket socket = null;
		OutputStream out = null;
		InputStream socketInputStream = null;
		DataOutputStream dout = null;
		DataInputStream din = null;
		String outputResponse = null;
		String inputRequest = null;
		String inputMessageID = null;

		try
		{

			PCLog.PCLogger.debug("userName "+ username);
			PCLog.PCLogger.debug("SessionId "+ sessionId);

			socketServerIP=socketDetailsMap.get("SocketServerIP");
			PCLog.PCLogger.debug("SocketServerIP "+ socketServerIP);
			socketServerPort=Integer.parseInt(socketDetailsMap.get("SocketServerPort"));
			PCLog.PCLogger.debug("SocketServerPort "+ socketServerPort);

	   		if (!("".equalsIgnoreCase(socketServerIP) && socketServerIP == null && socketServerPort==0))
	   		{

    			socket = new Socket(socketServerIP, socketServerPort);
    			socket.setSoTimeout(connection_timeout*1000);
    			out = socket.getOutputStream();
    			socketInputStream = socket.getInputStream();
    			dout = new DataOutputStream(out);
    			din = new DataInputStream(socketInputStream);
    			PCLog.PCLogger.debug("Dout " + dout);
    			PCLog.PCLogger.debug("Din " + din);

    			outputResponse = "";

    			inputRequest = getRequestXML( cabinetName,sessionId ,processInstanceID, ws_name, username, sInputXML);


    			if (inputRequest != null && inputRequest.length() > 0)
    			{
    				int inputRequestLen = inputRequest.getBytes("UTF-16LE").length;
    				PCLog.PCLogger.debug("RequestLen: "+inputRequestLen + "");
    				inputRequest = inputRequestLen + "##8##;" + inputRequest;
    				PCLog.PCLogger.debug("InputRequest"+"Input Request Bytes : "+ inputRequest.getBytes("UTF-16LE"));
    				dout.write(inputRequest.getBytes("UTF-16LE"));dout.flush();
    			}
    			byte[] readBuffer = new byte[500];
    			int num = din.read(readBuffer);
    			if (num > 0)
    			{

    				byte[] arrayBytes = new byte[num];
    				System.arraycopy(readBuffer, 0, arrayBytes, 0, num);
    				outputResponse = outputResponse+ new String(arrayBytes, "UTF-16LE");
					inputMessageID = outputResponse;
    				PCLog.PCLogger.debug("OutputResponse: "+outputResponse);

    				if(!"".equalsIgnoreCase(outputResponse))

    					outputResponse = getResponseXML(cabinetName,sJtsIp,iJtsPort,sessionId,
    							processInstanceID,outputResponse,integrationWaitTime );




    				if(outputResponse.contains("&lt;"))
    				{
    					outputResponse=outputResponse.replaceAll("&lt;", "<");
    					outputResponse=outputResponse.replaceAll("&gt;", ">");
    				}
    			}
    			socket.close();

				outputResponse = outputResponse.replaceAll("</MessageId>","</MessageId>/n<InputMessageId>"+inputMessageID+"</InputMessageId>");
				return outputResponse;

    	 		}

    		else
    		{
    			PCLog.PCLogger.debug("SocketServerIp and SocketServerPort is not maintained "+"");
    			PCLog.PCLogger.debug("SocketServerIp is not maintained "+	socketServerIP);
    			PCLog.PCLogger.debug(" SocketServerPort is not maintained "+	socketServerPort);
    			return "Socket Details not maintained";
    		}

		}

		catch (Exception e)
		{
			PCLog.PCLogger.debug("Exception Occured Mq_connection_CC"+e.getStackTrace());
			return "";
		}
		finally
		{
			try
			{
				if(out != null)
				{
					out.close();
					out=null;
				}
				if(socketInputStream != null)
				{

					socketInputStream.close();
					socketInputStream=null;
				}
				if(dout != null)
				{

					dout.close();
					dout=null;
				}
				if(din != null)
				{

					din.close();
					din=null;
				}
				if(socket != null)
				{
					if(!socket.isClosed())
						socket.close();
					socket=null;
				}

			}

			catch(Exception e)
			{
				PCLog.PCLogger.debug("Final Exception Occured Mq_connection_CC"+e.getStackTrace());
			}
		}


	}
	
}

