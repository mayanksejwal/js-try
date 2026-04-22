package com.newgen.PC;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.newgen.common.CommonConnection;
import com.newgen.common.CommonMethods;
import com.newgen.omni.jts.cmgr.NGXmlList;
import com.newgen.omni.jts.cmgr.XMLParser;

public class IntegrateDedupeBlacklistFicroRiskscore {
		
	private static String RelPartyGridTable = "USR_0_PC_RELATED_PARTY_GRID_DTLS";
	static HashMap<String, String> RelatedPartyDataMap = new HashMap<String, String>(); // not using
	
	static ResponseBean objRespBean=new ResponseBean();

	public ResponseBean IntegratewithMW(String processInstanceID, String WorkstepName, int integrationWaitTime, int socketConnectionTimeOut, HashMap<String, String> socketDetailsMap) throws IOException, Exception 
	{
		try{
		
			String integrationStatus = "";
			
			objRespBean.setWorkitemNumber(processInstanceID);
			objRespBean.setWorkStep(WorkstepName);
			objRespBean.setDedupeReturnCode("");
			objRespBean.setBlackListReturnCode("");
			objRespBean.setFircosoftReturnCode("");
			objRespBean.setFircoHit("");
			objRespBean.setBlacklistIntError("");
			objRespBean.setDedupeIntError("");
			objRespBean.setFircoIntError("");
			objRespBean.setMsgID("");
		
	    	HashMap<String, String> RelPartyDataMap = new HashMap<String,String>();
			String dedupeIntError="";
			String blacklistIntError="";
			String fircoIntError="";
			boolean IntegrationFlag = false;
			
			String DBQuery = "SELECT CIF_ID, MOBILE_NO,EXISTING_CUST," +
					" FIRST_NAME, MIDDLE_NAME, LAST_NAME, isnull(convert(date,DOB,120),'') as DATEOFBIRTH, NATIONALITY, VISA_NO," +
					" PASSPORT_NO, CIF_TYPE, EMIRATES_ID, GENDER, COUNTRY_INCORP, RELATIONSHIP_TYPE, TL_NO, COMPANY_NAME,"+
					" COUNTRY_RESIDENCE, isnull(convert(date,DATE_INCORP,120),'') as DATEOFINCORPORATION, insertionOrderId,"+
					"DEDUPE_STATUS, BLACKLIST_STATUS, FIRCO_STATUS, INPUT_PARAMS,CONDUCTED_ON,RP_IDENTIFIER"+
					" FROM "+RelPartyGridTable+" WITH(NOLOCK) WHERE " +
					"WI_NAME = '"+objRespBean.getWorkitemNumber()+"'";
			
			String extTabDataIPXML = CommonMethods.apSelectWithColumnNames(DBQuery,CommonConnection.getCabinetName(), CommonConnection.getSessionID(PCLog.PCLogger,false));
			PCLog.PCLogger.debug("RelPartyGridTable IPXML: "+ extTabDataIPXML);
			String extTabDataOPXML = PC.WFNGExecute(extTabDataIPXML,CommonConnection.getJTSIP(),CommonConnection.getJTSPort(),1);
			PCLog.PCLogger.debug("RelPartyGridTable OPXML: "+ extTabDataOPXML);

			XMLParser xmlParserData= new XMLParser(extTabDataOPXML);						
			int iTotalrec = Integer.parseInt(xmlParserData.getValueOf("TotalRetrieved"));
			
			if(xmlParserData.getValueOf("MainCode").equalsIgnoreCase("0")&& iTotalrec>0)
			{

				objRespBean.setFinalDecision("Success"); // tentative
				
				NGXmlList objWorkList = xmlParserData.createList("Records", "Record");										
				for (; objWorkList.hasMoreElements(true); objWorkList.skip(true))
				{	
					boolean DedupeFlag = false;
					boolean BlacklistFlag = false;
					boolean FircoFlag = false;
					boolean FircoHit = false;
					
					RelPartyDataMap.put("CIF",objWorkList.getVal("CIF_ID"));
					RelPartyDataMap.put("EXISTING_CUST",objWorkList.getVal("EXISTING_CUST"));
					RelPartyDataMap.put("FIRSTNAME",objWorkList.getVal("FIRST_NAME"));
					RelPartyDataMap.put("MIDDLENAME",objWorkList.getVal("MIDDLE_NAME"));
					RelPartyDataMap.put("LASTNAME",objWorkList.getVal("LAST_NAME"));
					RelPartyDataMap.put("DATEOFBIRTH",((objWorkList.getVal("DATEOFBIRTH")).equals("1900-01-01") ? "" : objWorkList.getVal("DATEOFBIRTH")));
					RelPartyDataMap.put("NATIONALITY",objWorkList.getVal("NATIONALITY"));
					RelPartyDataMap.put("VISANUMBER",objWorkList.getVal("VISA_NO"));
					RelPartyDataMap.put("PASSPORTNUMBER",objWorkList.getVal("PASSPORT_NO"));
					RelPartyDataMap.put("EMIRATESID",objWorkList.getVal("EMIRATES_ID"));
					RelPartyDataMap.put("GENDER",objWorkList.getVal("GENDER"));
					RelPartyDataMap.put("COUNTRY",objWorkList.getVal("COUNTRY_INCORP"));
					RelPartyDataMap.put("RELATIONSHIPTYPE",objWorkList.getVal("RELATIONSHIP_TYPE"));
					
					RelPartyDataMap.put("RELMOBILENUMBER",objWorkList.getVal("MOBILE_NO"));
					RelPartyDataMap.put("COMPANY_NAME",objWorkList.getVal("COMPANY_NAME"));
					RelPartyDataMap.put("TL_NUMBER",objWorkList.getVal("TL_NO"));
					RelPartyDataMap.put("COUNTRY_RESIDENCE",objWorkList.getVal("COUNTRY_RESIDENCE"));
					RelPartyDataMap.put("DATEOFINCORPORATION",((objWorkList.getVal("DATEOFINCORPORATION")).equals("1900-01-01") ? "" : objWorkList.getVal("DATEOFINCORPORATION")));
					RelPartyDataMap.put("insertionOrderId",objWorkList.getVal("insertionOrderId"));
					RelPartyDataMap.put("DEDUPE_STATUS",objWorkList.getVal("DEDUPE_STATUS"));
					RelPartyDataMap.put("BLACKLIST_STATUS",objWorkList.getVal("BLACKLIST_STATUS"));
					RelPartyDataMap.put("FIRCO_STATUS",objWorkList.getVal("FIRCO_STATUS"));
					RelPartyDataMap.put("INPUT_PARAMS",objWorkList.getVal("INPUT_PARAMS")); 
					RelPartyDataMap.put("COMPANYFLAG",objWorkList.getVal("CIF_TYPE").equals("Individual") ? "R" : "C");
					RelPartyDataMap.put("CONDUCTED_ON",objWorkList.getVal("CONDUCTED_ON")); 
					RelPartyDataMap.put("RP_IDENTIFIER",objWorkList.getVal("RP_IDENTIFIER"));
				
					
					// dedupe call
					if(!RelPartyDataMap.get("DEDUPE_STATUS").equalsIgnoreCase("Success")) //To check CIF ID validation
					{
						PCLog.PCLogger.debug("WINAME : "+objRespBean.getWorkitemNumber()+", WSNAME: "+objRespBean.getWorkStep()+", inside onclick function for Dedupe check call ,  CIFID : "+RelPartyDataMap.get("CIF"));
						
						integrationStatus = PCIntegration.DedupeCall(CommonConnection.getCabinetName(),CommonConnection.getUsername(),CommonConnection.getSessionID(PCLog.PCLogger,false), CommonConnection.getJTSIP(),
								CommonConnection.getJTSPort(),objRespBean.getWorkitemNumber(),objRespBean.getWorkStep(),integrationWaitTime,socketConnectionTimeOut,  socketDetailsMap, RelPartyDataMap);
						
											
						
						PCLog.PCLogger.debug("DedupeCall integrationStatus: " +integrationStatus);
						
						//setting Status of integration in Bean
					    if(integrationStatus.contains("Failure") && !DedupeFlag)
					    {
							DedupeFlag=true;
							String []dedupeDec;
							dedupeDec=integrationStatus.split("~");
							if(dedupeDec.length>0)
							{
								objRespBean.setDedupeReturnCode(dedupeDec[0]);
								objRespBean.setFinalDecision("Failure");
								//objRespBean.setDedupeIntError(dedupeDec[1]+"-"+dedupeDec[2]);
								//dedupeIntError+=objRespBean.getDedupeIntError();
								dedupeIntError+=dedupeDec[1]+":"+dedupeDec[2]+"-"+dedupeDec[3]+"\n";
							
							}	
						}
						else if(!DedupeFlag)
						{
							if(integrationStatus.contains("Failure")){
								objRespBean.setFinalDecision("Failure");
							}
							objRespBean.setDedupeReturnCode(integrationStatus);
						}

					    IntegrationFlag = true;
					    RelPartyDataMap.put("DEDUPE_STATUS",objRespBean.getDedupeReturnCode());
					   /* commented for now
					    if(objRespBean.getDedupeReturnCode().equals("Failure")){					    	
					        continue;
					    }
					    */
					    
					}
					else if(RelPartyDataMap.get("DEDUPE_STATUS").equalsIgnoreCase("Success"))
					{
						PCLog.PCLogger.debug("DedupeCall Status is success already and Integration call is not performed for :"+RelPartyDataMap.get("CIF_ID"));
						if (!"Failure".equalsIgnoreCase(objRespBean.getDedupeReturnCode())) // ??
							objRespBean.setDedupeReturnCode("Success");
					}
					
					
					// blacklist call
					if(!RelPartyDataMap.get("BLACKLIST_STATUS").equalsIgnoreCase("Success"))
					{						
						integrationStatus = PCIntegration.BlacklistCall(CommonConnection.getCabinetName(),CommonConnection.getUsername(),CommonConnection.getSessionID(PCLog.PCLogger,false), CommonConnection.getJTSIP(),
								CommonConnection.getJTSPort(),objRespBean.getWorkitemNumber(),objRespBean.getWorkStep(), socketConnectionTimeOut, integrationWaitTime,  socketDetailsMap, RelPartyDataMap);
						PCLog.PCLogger.debug("BlacklistCall integrationStatus: " +integrationStatus);
							
						//setting Status of integration in Bean
					    if(integrationStatus.contains("Failure") && !BlacklistFlag)
					    {
							BlacklistFlag=true;
							String []BlaklistDec;
							BlaklistDec=integrationStatus.split("~");
							if(BlaklistDec.length>0)
							{
								objRespBean.setBlackListReturnCode(BlaklistDec[0]);
								objRespBean.setFinalDecision("Failure");
								//objRespBean.setBlacklistIntError(BlaklistDec[1]+"-"+BlaklistDec[2]);
								//blacklistIntError+=objRespBean.getBlacklistIntError();
								blacklistIntError+=BlaklistDec[1]+":"+BlaklistDec[2]+"-"+BlaklistDec[3]+"\n";
							}	
						}
						else if(!BlacklistFlag)
						{
							if(integrationStatus.contains("Failure")){
								objRespBean.setFinalDecision("Failure");
							}
							objRespBean.setBlackListReturnCode(integrationStatus);
						}

					    IntegrationFlag = true;
					    RelPartyDataMap.put("BLACKLIST_STATUS",objRespBean.getBlackListReturnCode());
					    /* commented for now
					    if(objRespBean.getBlackListReturnCode().equals("Failure")){					    	
					        continue;
					    }
					    */
					}
					else if(RelPartyDataMap.get("BLACKLIST_STATUS").equalsIgnoreCase("Success"))
					{
						PCLog.PCLogger.debug("BlacklistCall Status is success already and Integration call is not performed for :"+RelPartyDataMap.get("CIF_ID"));
						if (!"Failure".equalsIgnoreCase(objRespBean.getBlackListReturnCode()))
							objRespBean.setBlackListReturnCode("Success");
					}
					
					
					// FIRCO calls
					if( (RelPartyDataMap.get("FIRCO_STATUS").equalsIgnoreCase("") 
							|| RelPartyDataMap.get("FIRCO_STATUS").equalsIgnoreCase("Not Checked")) )
					{
							
						integrationStatus= PCIntegration.FircosoftCall(CommonConnection.getCabinetName(),CommonConnection.getUsername(),CommonConnection.getSessionID(PCLog.PCLogger,false), CommonConnection.getJTSIP(),
								CommonConnection.getJTSPort(),objRespBean.getWorkitemNumber(),objRespBean.getWorkStep(),integrationWaitTime,socketConnectionTimeOut,  socketDetailsMap, RelPartyDataMap);
												
						
						PCLog.PCLogger.debug("FircosoftCall integrationStatus: " +integrationStatus);
						
						if(integrationStatus.contains("Not Checked") && !FircoFlag)
						{
							FircoFlag=true;
							objRespBean.setFinalDecision("Failure");
							String []fircoDec;
							fircoDec=integrationStatus.split("~");
							if(fircoDec.length>0)
							{
								objRespBean.setFircosoftReturnCode(fircoDec[0]);
								objRespBean.setFinalDecision("Failure");
								//objRespBean.setBlacklistIntError(BlaklistDec[1]+"-"+BlaklistDec[2]);
								//blacklistIntError+=objRespBean.getBlacklistIntError();
								fircoIntError+=fircoDec[1]+":"+fircoDec[2]+"-"+fircoDec[3]+"\n";
							}
						}
						
						/* commented for now				
						if(objRespBean.getFircosoftReturnCode().equals("Failure")) {
					        continue;
						}
						*/
						
						if(integrationStatus.equalsIgnoreCase("Record Found") || integrationStatus.equalsIgnoreCase("Record Not Found"))
						{
							objRespBean.setFircosoftReturnCode("Success");
						}
						
						//FircoHit
						if(integrationStatus.equalsIgnoreCase("Record Found") && !FircoHit)
						{
							FircoHit = true;
						}
						RelPartyDataMap.put("FIRCO_STATUS", objRespBean.getFircosoftReturnCode());
						IntegrationFlag = true;
																								
					}
					else if(RelPartyDataMap.get("FIRCO_STATUS").equalsIgnoreCase("Record Found") 
							|| RelPartyDataMap.get("FIRCO_STATUS").equalsIgnoreCase("Record Not Found"))
					{
						PCLog.PCLogger.debug("FircosoftCall Status is success already and Integration call is not performed for :"+RelPartyDataMap.get("CIF"));
						if (!"Failure".equalsIgnoreCase(objRespBean.getFircosoftReturnCode()))
							objRespBean.setFircosoftReturnCode("Success");
					}
					
					
					if(IntegrationFlag)
					{
						// Columns need to updated in CheckGrid table
						
						Calendar cal = Calendar.getInstance();
					    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			   
					    String CurrDateTime = sdf.format(cal.getTime());
					    
					    String StrIntegrationInputParamsRel = "";
					    StrIntegrationInputParamsRel = "CIF~#~"+RelPartyDataMap.get("CIF")+"|#|"+
								"COMPANYFLAG~#~"+ (RelPartyDataMap.get("COMPANYFLAG").equals("R") ? "Individual" : "Non-Individual") +"|#|"+					    
								"EXISTING_CUST~#~"+RelPartyDataMap.get("EXISTING_CUST")+"|#|"+
								"FIRSTNAME~#~"+RelPartyDataMap.get("FIRSTNAME")+"|#|"+
								"MIDDLENAME~#~"+RelPartyDataMap.get("MIDDLENAME")+"|#|"+ 
								"LASTNAME~#~"+RelPartyDataMap.get("LASTNAME")+"|#|"+
								"DATEOFBIRTH~#~"+RelPartyDataMap.get("DATEOFBIRTH")+"|#|"+
								"NATIONALITY~#~"+RelPartyDataMap.get("NATIONALITY")+"|#|"+
								"VISANUMBER~#~"+RelPartyDataMap.get("VISANUMBER")+"|#|"+
								"PASSPORTNUMBER~#~"+RelPartyDataMap.get("PASSPORTNUMBER")+"|#|"+
								"EMIRATESID~#~"+RelPartyDataMap.get("EMIRATESID")+"|#|"+
								"GENDER~#~"+RelPartyDataMap.get("GENDER")+"|#|"+
								"COUNTRY~#~"+RelPartyDataMap.get("COUNTRY")+"|#|"+
								"COMPANY_NAME~#~"+RelPartyDataMap.get("COMPANY_NAME")+"|#|"+
								"TL_NUMBER~#~"+RelPartyDataMap.get("TL_NUMBER")+"|#|"+
								"COUNTRY_RESIDENCE~#~"+RelPartyDataMap.get("COUNTRY_RESIDENCE")+"|#|"+
								"DATEOFINCORPORATION~#~"+RelPartyDataMap.get("DATEOFINCORPORATION")+"|#|"+
								"RELMOBILENUMBERCOUNTRYCODE~#~"+RelPartyDataMap.get("RELMOBILENUMBERCOUNTRYCODE")+"|#|"+
								"RELMOBILENUMBER~#~"+RelPartyDataMap.get("RELMOBILENUMBER")+"|#|"+
								"ISSUINGEMIRATE~#~"+RelPartyDataMap.get("ISSUINGEMIRATE");
					    
					    RelPartyDataMap.put("INPUT_PARAMS", StrIntegrationInputParamsRel);					    
						
					    if(RelPartyDataMap.getOrDefault("CONDUCTED_ON", "").equalsIgnoreCase(""))
					    {
					    	RelPartyDataMap.put("CONDUCTED_ON", CurrDateTime);
							RelPartyDataMap.put("DATE_MODIFIED_ON", "");
					    }
					    else
					    {
					    	RelPartyDataMap.put("DATE_MODIFIED_ON", CurrDateTime);
					    }	
					    
						RelPartyDataMap.put("CONDUCTED_BY", "System");
						
					    
					 // Update in RelatedParty table
					    String TableName = "USR_0_PC_RELATED_PARTY_GRID_DTLS"; 
					    String columnnames = "DEDUPE_STATUS,BLACKLIST_STATUS,FIRCO_STATUS,INPUT_PARAMS,CONDUCTED_ON,DATE_MODIFIED_ON";
					    String columnvalues = "'"+ RelPartyDataMap.get("DEDUPE_STATUS")+"','"+ RelPartyDataMap.get("BLACKLIST_STATUS") +"','"+ RelPartyDataMap.get("FIRCO_STATUS") +"','"+ RelPartyDataMap.get("INPUT_PARAMS") +"','"+ RelPartyDataMap.get("CONDUCTED_ON") +"','"+ RelPartyDataMap.get("DATE_MODIFIED_ON") +"'";
					    String sWhereClause = "WI_NAME='" + objRespBean.getWorkitemNumber() + "' AND insertionOrderId='" + RelPartyDataMap.get("insertionOrderId")+"' ";
			        	
					    String retCode = updateColumns( columnnames, columnvalues, TableName, sWhereClause);
				        
				        PCLog.PCLogger.debug("ReturnCode for updating the columns is : " + retCode);
				        
					}
						
					// all return codes here
					PCLog.PCLogger.debug("getDedupeReturnCode for WI : "+objRespBean.getDedupeReturnCode());
					PCLog.PCLogger.debug("getBlackListReturnCode for WI : "+objRespBean.getBlackListReturnCode());
					PCLog.PCLogger.debug("getFircosoftReturnCode for WI : "+objRespBean.getFircosoftReturnCode());							
						
					PCLog.PCLogger.debug("Now no record in checksgrid table for WI : "+objRespBean.getWorkitemNumber());
									
					// Checking HITs to set fircohit flag 
					if(FircoHit)
						objRespBean.setFircoHit("Y");
					else
					{
						// toDo remove ?? FIRCO Count
						objRespBean.setFircoHit("N");
					}
				}
				objRespBean.setDedupeIntError(dedupeIntError);
				objRespBean.setBlacklistIntError(blacklistIntError);
				objRespBean.setFircoIntError(fircoIntError);
			}			
		}
		catch(Exception e)
		{
			PCLog.PCLogger.debug("Exception occured in IntegrateMW Fn for WI :"+objRespBean.getWorkitemNumber());
			objRespBean.setWorkItemMainCode("");
			objRespBean.setDedupeReturnCode("");
			objRespBean.setBlackListReturnCode("");
			objRespBean.setFircosoftReturnCode("");
			objRespBean.setRiskScoreReturnCode("");
		}
		
		
		return objRespBean;
	}
	
	public static String updateColumns(String cols, String values, String tableName, String where) throws IOException, Exception{
					
	        String inputXML = CommonMethods.apUpdateInput(CommonConnection.getCabinetName(), CommonConnection.getSessionID(PCLog.PCLogger,false), tableName,
	          cols, values, where);
	        PCLog.PCLogger.debug("Input XML for apUpdateInput " + tableName + " Table : " + inputXML);
	        String outputXml = PC.WFNGExecute(inputXML, CommonConnection.getJTSIP(), CommonConnection.getJTSPort(), 1);
	        PCLog.PCLogger.debug("Output XML for apUpdateInput " + tableName + " Table : " + outputXml);
	        XMLParser sXMLParserChild = new XMLParser(outputXml);
	        String StrMainCode = sXMLParserChild.getValueOf("MainCode");
	        String RetStatus = "";
	        if (StrMainCode.equals("0"))	
	        {
	            PCLog.PCLogger.debug("Successful in apUpdateInput the record in : " + tableName);
	            RetStatus = "Success in apUpdateInput the record";
	        }
	        else
	        {
	            PCLog.PCLogger.debug("Error in Executing apUpdateInput sOutputXML : " + outputXml);
	            RetStatus = "Error in Executing apUpdateInput";
	        }
		
		return RetStatus;
	}
	
	private String resolveColumns(String key){ // mapping from 'map key' -> 'db column'
		
		if(key.equals("CIF")) return "CIF_ID";
		if(key.equals("FIRSTNAME")) return "FIRST_NAME";
		if(key.equals("MIDDLENAME")) return "MIDDLE_NAME";
		if(key.equals("LASTNAME")) return "LAST_NAME";
		if(key.equals("DATEOFBIRTH")) return "DOB";
		if(key.equals("NATIONALITY")) return "NATIONALITY";
		if(key.equals("VISANUMBER")) return "VISA_NO";
		if(key.equals("PASSPORTNUMBER")) return "PASSPORT_NO";
		if(key.equals("EMIRATESID")) return "EMIRATES_ID";
		if(key.equals("GENDER")) return "GENDER";
		if(key.equals("COUNTRY")) return "COUNTRY_INCORP";
		if(key.equals("RELATIONSHIPTYPE")) return "RELATIONSHIP_TYPE";
		if(key.equals("RELMOBILENUMBER")) return "MOBILE_NO";
		if(key.equals("COMPANY_NAME")) return "COMPANY_NAME";
		if(key.equals("TL_NUMBER")) return "TL_NO";
		if(key.equals("COUNTRY_RESIDENCE")) return "COUNTRY_RESIDENCE";
		if(key.equals("DATEOFINCORPORATION")) return "DATEOFINCORPORATION";
		if(key.equals("DEDUPE_STATUS")) return "DEDUPE_STATUS";
		if(key.equals("BLACKLIST_STATUS")) return "BLACKLIST_STATUS";
		if(key.equals("FIRCO_STATUS")) return "FIRCO_STATUS";
		if(key.equals("INPUT_PARAMS")) return "INPUT_PARAMS"; 
		if(key.equals("COMPANYFLAG")) return "CIF_TYPE";
		
		return "";
	}
}
