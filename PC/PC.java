/*
---------------------------------------------------------------------------------------------------------
                  NEWGEN SOFTWARE TECHNOLOGIES LIMITED

Group                   : Application - Projects
Project/Product			: RAK BPM
Application				: RAK BPM Utility
Module					: PC
File Name				: PC.java
Author 					: Sakshi Grover
Date (DD/MM/YYYY)		: 30/04/2019

---------------------------------------------------------------------------------------------------------
                 	CHANGE HISTORY
---------------------------------------------------------------------------------------------------------

Problem No/CR No        Change Date           Changed By             Change Description
---------------------------------------------------------------------------------------------------------
---------------------------------------------------------------------------------------------------------
*/


package com.newgen.PC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;
import com.newgen.omni.wf.util.app.NGEjbClient;
import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.sun.jmx.snmp.Timestamp;


public class PC implements Runnable
{

	private static NGEjbClient ngEjbClientPC;

	static Map<String, String> pcCofigParamMap = new HashMap<String, String>();

	@Override
	public void run()
	{
		String sessionID = "";
		String cabinetName = "";
		String jtsIP = "";
		String jtsPort = "";
		String queueID = "";
		int socketConnectionTimeout=0;
		int integrationWaitTime=0;
		int sleepIntervalInMin=0;

		try
		{
			PCLog.setLogger();
			ngEjbClientPC = NGEjbClient.getSharedInstance();

			PCLog.PCLogger.debug("Connecting to Cabinet.");
			System.out.println("Connecting to Cabinet.");

			int configReadStatus = readConfig();
			PCLog.PCLogger.debug("configReadStatus "+configReadStatus);

			if(configReadStatus !=0)
			{
				PCLog.PCLogger.error("Could not Read Config Properties [PCStatus]");
				return;
			}

			cabinetName = CommonConnection.getCabinetName();
			PCLog.PCLogger.debug("Cabinet Name: " + cabinetName);


			jtsIP = CommonConnection.getJTSIP();
			PCLog.PCLogger.debug("JTSIP: " + jtsIP);

			jtsPort = CommonConnection.getJTSPort();
			PCLog.PCLogger.debug("JTSPORT: " + jtsPort);

			queueID = pcCofigParamMap.get("QueueID");
			PCLog.PCLogger.debug("QueueID: " + queueID);

			socketConnectionTimeout=Integer.parseInt(pcCofigParamMap.get("MQ_SOCKET_CONNECTION_TIMEOUT"));
			PCLog.PCLogger.debug("SocketConnectionTimeOut: "+socketConnectionTimeout);

			integrationWaitTime=Integer.parseInt(pcCofigParamMap.get("INTEGRATION_WAIT_TIME"));
			PCLog.PCLogger.debug("IntegrationWaitTime: "+integrationWaitTime);

			sleepIntervalInMin=Integer.parseInt(pcCofigParamMap.get("SleepIntervalInMin"));
			PCLog.PCLogger.debug("SleepIntervalInMin: "+sleepIntervalInMin);

			sessionID = CommonConnection.getSessionID(PCLog.PCLogger, false);

			if(sessionID.trim().equalsIgnoreCase(""))
			{
				PCLog.PCLogger.debug("Could Not Connect to Server!");
			}
			else
			{
				PCLog.PCLogger.debug("Session ID found: " + sessionID);
				HashMap<String, String> socketDetailsMap= socketConnectionDetails(cabinetName, jtsIP, jtsPort,
						sessionID);
				while(true)
				{
					PCLog.setLogger();
					startPCUtility(cabinetName, jtsIP, jtsPort,sessionID,
							queueID,socketConnectionTimeout, integrationWaitTime,socketDetailsMap);
							System.out.println("No More workitems to Process, Sleeping!");
							Thread.sleep(sleepIntervalInMin*60*1000);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			PCLog.PCLogger.error("Exception Occurred in PC Thread: "+e);
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			e.printStackTrace(printWriter);
			PCLog.PCLogger.error("Exception Occurred in PC Thread : "+result);
		}
	}
	private int readConfig()
	{
		Properties p = null;
		try {

			p = new Properties();
			p.load(new FileInputStream(new File(System.getProperty("user.dir")+ File.separator + "ConfigFiles"+ File.separator+ "PC_Config.properties")));

			Enumeration<?> names = p.propertyNames();

			while (names.hasMoreElements())
			{
			    String name = (String) names.nextElement();
			    pcCofigParamMap.put(name, p.getProperty(name));
			}
		}
		catch (Exception e)
		{
			return -1 ;
		}
		return 0;
	}
	private void startPCUtility(String cabinetName,String sJtsIp,String iJtsPort,String sessionId,
		 String queueID, int socketConnectionTimeOut, int integrationWaitTime,
			HashMap<String, String> socketDetailsMap)
	{
		final String ws_name="System_Integration";
		try
		{
			sessionId  = CommonConnection.getSessionID(PCLog.PCLogger,false);

			if(sessionId==null || sessionId.equalsIgnoreCase("") || sessionId.equalsIgnoreCase("null"))
			{
				PCLog.PCLogger.error("Could Not Get Session ID "+sessionId);
				return;
			}

			//Fetch all Work-Items on given queueID.
			PCLog.PCLogger.debug("Fetching all Workitems on System Integration queue");
			System.out.println("Fetching all Workitems on System Integration queue");
			String fetchWorkitemListInputXML=CommonMethods.fetchWorkItemsInput(cabinetName, sessionId, queueID);
			PCLog.PCLogger.debug("InputXML for fetchWorkList Call: "+fetchWorkitemListInputXML);

			String fetchWorkitemListOutputXML= WFNGExecute(fetchWorkitemListInputXML,sJtsIp,iJtsPort,1);

			PCLog.PCLogger.debug("WMFetchWorkList OutputXML: "+fetchWorkitemListOutputXML);

			XMLParser xmlParserFetchWorkItemlist = new XMLParser(fetchWorkitemListOutputXML);
			String fetchWorkItemListMainCode = xmlParserFetchWorkItemlist.getValueOf("MainCode");
			PCLog.PCLogger.debug("FetchWorkItemListMainCode: "+fetchWorkItemListMainCode);

			int fetchWorkitemListCount = Integer.parseInt(xmlParserFetchWorkItemlist.getValueOf("RetrievedCount"));
			PCLog.PCLogger.debug("RetrievedCount for WMFetchWorkList Call: "+fetchWorkitemListCount);

			PCLog.PCLogger.debug("Number of workitems retrieved on System Integration queue: "+fetchWorkitemListCount);
			System.out.println("Number of workitems retrieved on System Integration queue: "+fetchWorkitemListCount);
			if (fetchWorkItemListMainCode.trim().equals("0") && fetchWorkitemListCount > 0)
			{
				for(int i=0; i<fetchWorkitemListCount; i++)
				{
					String fetchWorkItemlistData=xmlParserFetchWorkItemlist.getNextValueOf("Instrument");
					fetchWorkItemlistData =fetchWorkItemlistData.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

					PCLog.PCLogger.debug("Parsing <Instrument> in WMFetchWorkList OutputXML: "+fetchWorkItemlistData);
					XMLParser xmlParserfetchWorkItemData = new XMLParser(fetchWorkItemlistData);

					String processInstanceID=xmlParserfetchWorkItemData.getValueOf("ProcessInstanceId");
					PCLog.PCLogger.debug("Current ProcessInstanceID: "+processInstanceID);

					PCLog.PCLogger.debug("Processing Workitem: "+processInstanceID);
					System.out.println("\nProcessing Workitem: "+processInstanceID);

					String WorkItemID=xmlParserfetchWorkItemData.getValueOf("WorkItemId");
					PCLog.PCLogger.debug("Current WorkItemID: "+WorkItemID);

					String entryDateTime=xmlParserfetchWorkItemData.getValueOf("EntryDateTime");
					PCLog.PCLogger.debug("Current EntryDateTime: "+entryDateTime);

					//Only to test one WorkItem. Uncomment if required.
					if(!processInstanceID.equals("PC-0000000296-process"))
					{
						//continue;
					}

					//PC Integration Call
					/*PCIntegration objPCIntegration= new PCIntegration();
					String decisionValue=objPCIntegration.customIntegration(cabinetName,sessionId, sJtsIp,
							iJtsPort,processInstanceID,ws_name,socketConnectionTimeOut,integrationWaitTime, socketDetailsMap);
					*/
					/*------------VARUN DEDUPE BLACKLIST FIRCO CALLS 15012024------------*/
					ResponseBean SysResponse = null;
					String errorMsg="";
					String decisionValue = "Success";
					if("Success".equalsIgnoreCase(decisionValue))
					{
						IntegrateDedupeBlacklistFicroRiskscore RPChecks = new IntegrateDedupeBlacklistFicroRiskscore();
						SysResponse = RPChecks.IntegratewithMW(processInstanceID, ws_name, integrationWaitTime, socketConnectionTimeOut, socketDetailsMap);
						
						if(SysResponse.getFinalDecision().equals("Success"))
							decisionValue = "Success";						
						else
							decisionValue = "Failure";
					}
					PCLog.PCLogger.debug("decisionValue : "+ decisionValue);
					
					if(decisionValue.equalsIgnoreCase("Failure"))
					{
						if(!"".equalsIgnoreCase(SysResponse.getDedupeIntError()))
						{
							
							errorMsg+=SysResponse.getDedupeIntError();
							errorMsg+="\n";
						}
						if(!"".equalsIgnoreCase(SysResponse.getBlacklistIntError()))
						{
							
							errorMsg+=SysResponse.getBlacklistIntError();
							errorMsg+="\n";
						}
						if(!"".equalsIgnoreCase(SysResponse.getFircoIntError()))
						{
							errorMsg+=SysResponse.getFircoIntError();
							errorMsg+="\n";
						}
					}
					//To be modified according to output of Integration Call.
					String attributesTag="<qDecision>"+decisionValue+"</qDecision>";

					String extTabDBQuery = "SELECT VAR_STR7 AS QSIPREVWS, VAR_STR8 AS QSIPREVDECISION , ISACCOUNTFREEZE, MP_INTG_REQ_INITIATION " +
							", ISSYSTEMCHECKS , ISSYSTEMCHECKSAPPROVAL , ISCREDITAPPROVAL " +
							" FROM RB_PC_EXTTABLE A WITH (NOLOCK) , WFINSTRUMENTTABLE B WITH (NOLOCK) " +
							"WHERE A.WI_NAME = B.PROCESSINSTANCEID  AND B.WORKITEMID = '1' " +
							"AND A.WI_NAME = '"+processInstanceID+"'";

					String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(extTabDBQuery,cabinetName, sessionId);
					PCLog.PCLogger.debug("extTabDataIPXML: "+ extTabDataIPXML);
					String extTabDataOPXML = WFNGExecute(extTabDataIPXML,sJtsIp,iJtsPort,1);
					PCLog.PCLogger.debug("extTabDataOPXML: "+ extTabDataOPXML);

					XMLParser xmlParserextTabData= new XMLParser(extTabDataOPXML);

					if(xmlParserextTabData.getValueOf("MainCode").equalsIgnoreCase("0")&& Integer.parseInt(xmlParserextTabData.getValueOf("TotalRetrieved"))>0)
					{
						String xmlDataExtTab=xmlParserextTabData.getNextValueOf("Record");
						xmlDataExtTab =xmlDataExtTab.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

						XMLParser xmlParserExtTabDataRecord = new XMLParser(xmlDataExtTab);

						String qPrevWS = xmlParserExtTabDataRecord.getValueOf("QSIPREVWS");
						String qPrevDec = xmlParserExtTabDataRecord.getValueOf("QSIPREVDECISION");
						String isAccountFreeze = xmlParserExtTabDataRecord.getValueOf("ISACCOUNTFREEZE");
						String mpIntgReqInitiation = xmlParserExtTabDataRecord.getValueOf("MP_INTG_REQ_INITIATION");
						String isSystemChecks = xmlParserExtTabDataRecord.getValueOf("ISSYSTEMCHECKS");
						String isSystemChecksApproval = xmlParserExtTabDataRecord.getValueOf("ISSYSTEMCHECKSAPPROVAL");
						String isCreditApproval = xmlParserExtTabDataRecord.getValueOf("ISCREDITAPPROVAL");




						/*if(qPrevWS.equalsIgnoreCase("Introduction")&&isAccountFreeze.equalsIgnoreCase("Required"))
						{
							attributesTag=attributesTag+"<ISACCOUNTFREEZE>Success</ISACCOUNTFREEZE>";
							attributesTag=attributesTag+"<ACC_FREEZE_INTG_STATUS>Success</ACC_FREEZE_INTG_STATUS>";
						}*/
						if((qPrevWS.equalsIgnoreCase("Introduction") || qPrevWS.equalsIgnoreCase("CSM")) && mpIntgReqInitiation.equalsIgnoreCase("Required"))
						{
							attributesTag=attributesTag+"<MP_INTG_STATUS_INITIATION>Success</MP_INTG_STATUS_INITIATION>";
						}
						/*if(qPrevWS.equalsIgnoreCase("Introduction")&&isSystemChecks.equalsIgnoreCase("Required"))
						{
							attributesTag=attributesTag+"<ISSYSTEMCHECKSAPPROVAL>Required</ISSYSTEMCHECKSAPPROVAL>";
						}*/


						if((qPrevWS.equalsIgnoreCase("OPS_Document_Checker") ||  qPrevWS.equalsIgnoreCase("OPS_Bil_Document_Checker"))
								&&qPrevDec.equalsIgnoreCase("Additional Information Required Initiator"))
						{
							attributesTag=attributesTag+"<MP_INTG_STATUS_OPS_REJECT>Success</MP_INTG_STATUS_OPS_REJECT>";
						}

						if((qPrevWS.equalsIgnoreCase("OPS_Document_Checker") ||  qPrevWS.equalsIgnoreCase("OPS_Bil_Document_Checker"))
								&&qPrevDec.equalsIgnoreCase("Approve"))
						{
							attributesTag=attributesTag+"<MP_INTG_STATUS_OPS_REJECT>Success</MP_INTG_STATUS_OPS_REJECT>";
						}

						if((qPrevWS.equalsIgnoreCase("OPS_Document_Checker") ||  qPrevWS.equalsIgnoreCase("OPS_Bil_Document_Checker"))
								&&qPrevDec.equalsIgnoreCase("Approve") && isSystemChecksApproval.equalsIgnoreCase("Required"))
						{
							attributesTag=attributesTag+"<MP_INTG_STATUS_SYSTEM_CHECKS>Success</MP_INTG_STATUS_SYSTEM_CHECKS>";
						}

						if((qPrevWS.equalsIgnoreCase("OPS_Document_Checker") ||  qPrevWS.equalsIgnoreCase("OPS_Bil_Document_Checker"))
								&&qPrevDec.equalsIgnoreCase("Approve") && isSystemChecksApproval.equalsIgnoreCase("Required"))
						{
							attributesTag=attributesTag+"<MP_INTG_STATUS_SYSTEM_CHECKS>Success</MP_INTG_STATUS_SYSTEM_CHECKS>";
						}

						if((qPrevWS.equalsIgnoreCase("OPS_Document_Checker") ||  qPrevWS.equalsIgnoreCase("OPS_Bil_Document_Checker"))
								&&qPrevDec.equalsIgnoreCase("Approve") && isCreditApproval.equalsIgnoreCase("Required"))
						{
							attributesTag=attributesTag+"<MP_INTG_STATUS_CREDEIT_APPROVAL>Success</MP_INTG_STATUS_CREDEIT_APPROVAL>";
						}
					}

					//Lock Workitem.
					String getWorkItemInputXML = CommonMethods.getWorkItemInput(cabinetName, sessionId, processInstanceID,WorkItemID);
					String getWorkItemOutputXml = WFNGExecute(getWorkItemInputXML,sJtsIp,iJtsPort,1);
					PCLog.PCLogger.debug("Output XML For WmgetWorkItemCall: "+ getWorkItemOutputXml);

					XMLParser xmlParserGetWorkItem = new XMLParser(getWorkItemOutputXml);
					String getWorkItemMainCode = xmlParserGetWorkItem.getValueOf("MainCode");
					PCLog.PCLogger.debug("WmgetWorkItemCall Maincode:  "+ getWorkItemMainCode);

					if (getWorkItemMainCode.trim().equals("0"))
					{
						PCLog.PCLogger.debug("WMgetWorkItemCall Successful: "+getWorkItemMainCode);

						String assignWorkitemAttributeInputXML=CommonMethods.assignWorkitemAttributeInput(cabinetName, sessionId,
								processInstanceID,WorkItemID,attributesTag);
						PCLog.PCLogger.debug("InputXML for assignWorkitemAttribute Call: "+assignWorkitemAttributeInputXML);

						String assignWorkitemAttributeOutputXML=WFNGExecute(assignWorkitemAttributeInputXML,sJtsIp,
								iJtsPort,1);

						PCLog.PCLogger.debug("OutputXML for assignWorkitemAttribute Call: "+assignWorkitemAttributeOutputXML);

						XMLParser xmlParserWorkitemAttribute = new XMLParser(assignWorkitemAttributeOutputXML);
						String assignWorkitemAttributeMainCode = xmlParserWorkitemAttribute.getValueOf("MainCode");
						PCLog.PCLogger.debug("AssignWorkitemAttribute MainCode: "+assignWorkitemAttributeMainCode);

						if(assignWorkitemAttributeMainCode.trim().equalsIgnoreCase("0"))
						{
							PCLog.PCLogger.debug("AssignWorkitemAttribute Successful: "+assignWorkitemAttributeMainCode);


							//Move Workitem to next Workstep
							String completeWorkItemInputXML = CommonMethods.completeWorkItemInput(cabinetName, sessionId,
									processInstanceID,WorkItemID);

							PCLog.PCLogger.debug("Input XML for wmcompleteWorkItem: "+ completeWorkItemInputXML);

							String completeWorkItemOutputXML = WFNGExecute(completeWorkItemInputXML,sJtsIp,iJtsPort,1);
							PCLog.PCLogger.debug("Output XML for wmcompleteWorkItem: "+ completeWorkItemOutputXML);

							XMLParser xmlParserCompleteWorkitem = new XMLParser(completeWorkItemOutputXML);
							String completeWorkitemMaincode = xmlParserCompleteWorkitem.getValueOf("MainCode");
							PCLog.PCLogger.debug("Status of wmcompleteWorkItem  "+ completeWorkitemMaincode);

							if (completeWorkitemMaincode.trim().equalsIgnoreCase("0"))
							{
								PCLog.PCLogger.debug("WmCompleteWorkItem successful: "+completeWorkitemMaincode);
								System.out.println(processInstanceID + "Complete Succesfully with status "+decisionValue);
								
								if(decisionValue.equalsIgnoreCase("Failure"))
								{
									errorMailTrigger(processInstanceID,errorMsg,cabinetName, sessionId,sJtsIp,iJtsPort);
								}
								
								PCLog.PCLogger.debug("WorkItem moved to next Workstep.");

								SimpleDateFormat inputDateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
								SimpleDateFormat outputDateFormat=new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");

								Date entryDatetimeFormat = inputDateformat.parse(entryDateTime);
								String formattedEntryDatetime=outputDateFormat.format(entryDatetimeFormat);
								PCLog.PCLogger.debug("FormattedEntryDatetime: "+formattedEntryDatetime);

								Date actionDateTime= new Date();
								String formattedActionDateTime=outputDateFormat.format(actionDateTime);
								PCLog.PCLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);

								//Insert in WIHistory Table.

								String columnNames="WI_NAME,ACTION_DATE_TIME,WS_NAME,USER_NAME,DECISION,ENTRY_DATE_TIME";
								String columnValues="'"+processInstanceID+"','"+formattedActionDateTime+"','"+ws_name+"','"
								+CommonConnection.getUsername()+"','"+decisionValue+"','"+formattedEntryDatetime+"'";
								if(decisionValue.equalsIgnoreCase("Failure"))
								{
									columnNames+=",REMARKS";
									columnValues+=",'"+errorMsg+"'";
								}

								String apInsertInputXML=CommonMethods.apInsert(cabinetName, sessionId, columnNames, columnValues,"USR_0_PC_WIHISTORY");
								PCLog.PCLogger.debug("APInsertInputXML: "+apInsertInputXML);

								String apInsertOutputXML = WFNGExecute(apInsertInputXML,sJtsIp,
										iJtsPort,1);
								PCLog.PCLogger.debug("APInsertOutputXML: "+ apInsertOutputXML);

								XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
								String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
								PCLog.PCLogger.debug("Status of apInsertMaincode  "+ apInsertMaincode);
								if(apInsertMaincode.equalsIgnoreCase("0"))
								{
									PCLog.PCLogger.debug("ApInsert successful: "+apInsertMaincode);
									PCLog.PCLogger.debug("Inserted in WiHistory table successfully.");
								}
								else
								{
									PCLog.PCLogger.debug("ApInsert failed: "+apInsertMaincode);
								}
							}
							else
							{
								completeWorkitemMaincode="";
								PCLog.PCLogger.debug("WMCompleteWorkItem failed: "+completeWorkitemMaincode);
							}
						}
						else
						{
							assignWorkitemAttributeMainCode="";
							PCLog.PCLogger.debug("AssignWorkitemAttribute failed: "+assignWorkitemAttributeMainCode);
						}
					}
					else
					{
						getWorkItemMainCode="";
						PCLog.PCLogger.debug("WmgetWorkItem failed: "+getWorkItemMainCode);
					}
				}
			}
			else
			{
				PCLog.PCLogger.debug("WMFetchWorkList failed with code: "+fetchWorkItemListMainCode);
			}
		}catch (Exception e)
		{
			PCLog.PCLogger.debug("Exception: "+e.getMessage());
		}
	}
	
	private HashMap<String,String> socketConnectionDetails(String cabinetName, String sJtsIp, String iJtsPort,
			String sessionID)
	{
		HashMap<String, String> socketDetailsMap = new HashMap<String, String>();

		try
		{
			PCLog.PCLogger.debug("Fetching Socket Connection Details.");
			System.out.println("Fetching Socket Connection Details.");

			String socketDetailsQuery = "SELECT SocketServerIP,SocketServerPort FROM NG_BPM_MQ_TABLE with (nolock) where ProcessName = 'PC' and CallingSource = 'Utility'";

			String socketDetailsInputXML =CommonMethods.apSelectWithColumnNames(socketDetailsQuery, cabinetName, sessionID);
			PCLog.PCLogger.debug("Socket Details APSelect InputXML: "+socketDetailsInputXML);

			String socketDetailsOutputXML=WFNGExecute(socketDetailsInputXML,sJtsIp,iJtsPort,1);
			PCLog.PCLogger.debug("Socket Details APSelect OutputXML: "+socketDetailsOutputXML);

			XMLParser xmlParserSocketDetails= new XMLParser(socketDetailsOutputXML);
			String socketDetailsMainCode = xmlParserSocketDetails.getValueOf("MainCode");
			PCLog.PCLogger.debug("SocketDetailsMainCode: "+socketDetailsMainCode);

			int socketDetailsTotalRecords = Integer.parseInt(xmlParserSocketDetails.getValueOf("TotalRetrieved"));
			PCLog.PCLogger.debug("SocketDetailsTotalRecords: "+socketDetailsTotalRecords);

			if(socketDetailsMainCode.equalsIgnoreCase("0")&& socketDetailsTotalRecords>0)
			{
				String xmlDataSocketDetails=xmlParserSocketDetails.getNextValueOf("Record");
				xmlDataSocketDetails =xmlDataSocketDetails.replaceAll("[ ]+>",">").replaceAll("<[ ]+", "<");

				XMLParser xmlParserSocketDetailsRecord = new XMLParser(xmlDataSocketDetails);

				String socketServerIP=xmlParserSocketDetailsRecord.getValueOf("SocketServerIP");
				PCLog.PCLogger.debug("SocketServerIP: "+socketServerIP);
				socketDetailsMap.put("SocketServerIP", socketServerIP);

				String socketServerPort=xmlParserSocketDetailsRecord.getValueOf("SocketServerPort");
				PCLog.PCLogger.debug("SocketServerPort " + socketServerPort);
				socketDetailsMap.put("SocketServerPort", socketServerPort);

				PCLog.PCLogger.debug("SocketServer Details found.");
				System.out.println("SocketServer Details found.");

			}
		}
		catch (Exception e)
		{
			PCLog.PCLogger.debug("Exception in getting Socket Connection Details: "+e.getMessage());
			System.out.println("Exception in getting Socket Connection Details: "+e.getMessage());
		}

		return socketDetailsMap;
	}

	public static String WFNGExecute(String ipXML, String jtsServerIP, String serverPort,
			int flag) throws IOException, Exception
	{
		PCLog.PCLogger.debug("In WF NG Execute with server port: " + serverPort);
		try
		{
			if (serverPort.startsWith("33"))
				return WFCallBroker.execute(ipXML, jtsServerIP,
						Integer.parseInt(serverPort), 1);
			else
				return ngEjbClientPC.makeCall(jtsServerIP, serverPort,
						"WebSphere", ipXML);
		}
		catch (Exception e)
		{
			PCLog.PCLogger.debug("Exception Occured in WF NG Execute : "
					+ e.getMessage());
			e.printStackTrace();
			return "Error";
		}
	}
	public String errorMailTrigger(String workitemNo,String errorMsg,String cabinetName,String sessionId,String sJtsIp,
			String iJtsPort) throws IOException, Exception
	{
		String toMailId="";
		String fromMailId="";
		List<String> mailIDs=new ArrayList<String>();
		String DBQuery = "SELECT CONST_FIELD_VALUE from USR_0_BPM_CONSTANTS WITH(NOLOCK) WHERE CONST_FIELD_NAME IN ('PC_FromMailID','SupportMailId')";
					
		String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(DBQuery,CommonConnection.getCabinetName(), CommonConnection.getSessionID(PCLog.PCLogger,false));
		PCLog.PCLogger.debug("Mail fetch IPXML: "+ extTabDataIPXML);
		String extTabDataOPXML;
		try {
			extTabDataOPXML = PC.WFNGExecute(extTabDataIPXML,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
			XMLParser xmlParserData= new XMLParser(extTabDataOPXML);
			PCLog.PCLogger.debug("Mail fetch "+ extTabDataOPXML);
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
		PCLog.PCLogger.debug("FormattedActionDateTime: "+formattedActionDateTime);
		String columnNames="mailFrom,mailTo,mailCC,mailBCC,mailSubject,mailMessage,mailContentType,attachmentISINDEX,attachmentNames,attachmentExts,mailPriority,mailStatus,statusComments,lockedBy,successTime,LastLockTime,insertedBy,mailActionType,insertedTime,processDefId,processInstanceId,workitemId,activityId,noOfTrials,zipFlag,zipName,maxZipSize,alternateMessage";
		String columnValues="'"+fromMailId+"','"+toMailId+"',NULL,NULL,'Profile Change WI-"+workitemNo+" moved to error handling','"
				+ "<!DOCTYPE html>"
				+ "<html lang=\\\"en\">"
				+ "<head>"
				+ "<meta charset=\\\"UTF-8\">"
				+"<meta name=\\\"viewport\" content=\\\"width=device-width, initial-scale=1.0\">"
				+"<title>Profile Change Notification</title>"
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
				+"  <p>Profile change WI-"+workitemNo+" has been moved to the error handling queue due to the following reason:</p>"
				+"  <p><strong>"+errorMsg+"</strong></p>"
				+"  <p>Regards,<br>RAKBANK</p>"
				+"  <p class=\\\"disclaimer\">This is a system-generated email. Please do not reply to this message.</p>"
				+"</div>"
				+"</body>"
				+"</html>','text/html;charset=UTF-8',NULL,NULL,NULL,1,'N',NULL,NULL,NULL,NULL,'CUSTOM','TRIGGER','"+formattedActionDateTime+"',1,'"+workitemNo+"',1,59,0,NULL,NULL,NULL,NULL";
		String apInsertInputXML=CommonMethods.apInsert(cabinetName, sessionId, columnNames, columnValues,"WFMAILQUEUETABLE");
		PCLog.PCLogger.debug("APInsertInputXML: "+apInsertInputXML);

		String apInsertOutputXML = WFNGExecute(apInsertInputXML,sJtsIp,iJtsPort,1);
		PCLog.PCLogger.debug("APInsertOutputXML: "+ apInsertOutputXML);

		XMLParser xmlParserAPInsert = new XMLParser(apInsertOutputXML);
		String apInsertMaincode = xmlParserAPInsert.getValueOf("MainCode");
		PCLog.PCLogger.debug("Status of apInsertMaincode  "+ apInsertMaincode);
		if(apInsertMaincode.equalsIgnoreCase("0"))
		{
			PCLog.PCLogger.debug("ApInsert successful: "+apInsertMaincode);
			PCLog.PCLogger.debug("Inserted in WiHistory table successfully.");
		}
		else
		{
			PCLog.PCLogger.debug("ApInsert failed: "+apInsertMaincode);
		}
		return "Success";
	}
}
