
var PC_onLoad = document.createElement('script');
	PC_onLoad.src = '/PC/PC/CustomJS/PC_onLoad.js';
	document.head.appendChild(PC_onLoad);

function setCustomControlsValue()
{
	var ServiceRequestSelected;
	try
	{
		ServiceRequestSelected= window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestSelected");
	}
	catch(ex)
	{
		ServiceRequestSelected= window.frames['iframe2'].document.getElementById("ServiceRequestSelected");
	}
	var ServiceRequestValue= "";
	var opt=[] ,tempStr="";
	var len=ServiceRequestSelected.options.length;
	if(len>0)
	{
		for(var i=0;i<len; i++)
		{
			opt = ServiceRequestSelected.options[i];
			ServiceRequestValue=ServiceRequestValue+opt.value+"|";
			
		}
		ServiceRequestValue=ServiceRequestValue.substring(0,ServiceRequestValue.length-1);
		setControlValue("SERVICE_REQUEST_SELECTED",ServiceRequestValue);
	}
	
	var ProductIdentifierSelected;
	try
	{
		ProductIdentifierSelected= window.frames['iframe4'].contentWindow.document.getElementById("ProductSelected");
	}
	catch(ex)
	{
		ProductIdentifierSelected= window.frames['iframe4'].document.getElementById("ProductSelected");
	}
	var ProductIdentifierValue= "";
	opt1=[] ,tempStr1="";
	len=ProductIdentifierSelected.options.length;
	if(len>0)
	{
		for(var j=0;j<len; j++)
		{
			opt1 = ProductIdentifierSelected.options[j];
			ProductIdentifierValue=ProductIdentifierValue+opt1.value+"|";
		}
		
		ProductIdentifierValue=ProductIdentifierValue.substring(0,ProductIdentifierValue.length-1);
		//alert("Product identifier final value "+ProductIdentifierValue);
		setControlValue("PRODUCT_HELD",ProductIdentifierValue);
	}
	
	var RelatedCIFSelected;
	try
	{
		RelatedCIFSelected= window.frames['iframe3'].contentWindow.document.getElementById("RelatedCIFSelected");
	}
	catch(ex)
	{
		RelatedCIFSelected= window.frames['iframe3'].document.getElementById("RelatedCIFSelected");
	}
	var RelatedCifValue= "";
	var opt2=[] ,tempStr2="";
	var len=RelatedCIFSelected.options.length;
	if(len>0)
	{
		for(var i=0;i<len;i++)
		{
			opt2 = RelatedCIFSelected.options[i];
			RelatedCifValue=RelatedCifValue+opt2.value+"|";
			
		}
		RelatedCifValue=RelatedCifValue.substring(0,RelatedCifValue.length-1);
		setControlValue("SELECTED_RELATED_CIF",RelatedCifValue);
	}
	
	
	var RelatedCIF;
	try
	{
		RelatedCIF= window.frames['iframe3'].contentWindow.document.getElementById("RelatedCIFdropdown");
	}
	catch(ex)
	{
		RelatedCIF= window.frames['iframe3'].document.getElementById("RelatedCIFdropdown");
	}
	RelatedCifValue= "";
	opt2=[];
	tempStr2="";
	len=RelatedCIF.options.length;
	if(len>0)
	{
		for(var i=0;i<len;i++)
		{
			opt2 = RelatedCIF.options[i];
			RelatedCifValue=RelatedCifValue+opt2.value+"|";
			
		}
		RelatedCifValue=RelatedCifValue.substring(0,RelatedCifValue.length-1);
		setControlValue("RELATED_CIF_SEARCH",RelatedCifValue);
	}
	
	DocTypeAttached();
}

function DocTypeAttached()
{
	var docInterface = window.parent.getInterfaceData('D');
	var docListSize= docInterface.length;
	var attachedFlag =  false;   
	/*for(var docCounter=0;docCounter<docListSize;docCounter++)
	{
		var docName = docInterface[docCounter].name;
				   
		if(docName=="Security Cheque or Cheques")
		{
			attachedFlag = true;
			setControlValue("ISSECURITYCHKDOCTYPE","Attached");
			break;
		}
	}*/
	if(attachedFlag==false)
	{
		setControlValue("ISSECURITYCHKDOCTYPE","No");
	}
}

//added by Nikita on 27052019 for the condition at Obtain Original and Additional Doc queue
function DocTypeAttachedObtainOrgDoc(ActivityName)
{
	var isDocAttached="";
	if(ActivityName=="Obtain_Org_Add_Documents")
	{
		isDocAttached=executeServerEvent("isDocAttached","INTRODUCEDONE","",true);
		if(isSTLAttachedDocType('Salary Transfer Letter'))
		{
			setControlValue('isDoc_SalaryTransferLetter_Attached','Attached');
			
		}
	}
}

//Function added By Sajan as part of CRs 21082019
function setUserToWorkOnThisWI(username)
{
	//alert("The user is "+username);
	if(ActivityName=="Card_Maintenance_Maker" && getValue("CardMaintMakerSubBy")=="")
	{
		setControlValue("CardMaintMakerSubBy",username);
	}
	else if(ActivityName=="Card_Maintenance_Checker" && getValue("CardMaintCheckerSubBy")=="")
	{
		setControlValue("CardMaintCheckerSubBy",username);
	}
	else if(ActivityName=="Card_Settlement_Maker" && getValue("CardSettleMakerSubBy")=="")
	{
		setControlValue("CardSettleMakerSubBy",username);
	}
	else if(ActivityName=="Card_Settlement_Checker" && getValue("CardSettleCheckerSubBy")=="")
	{
		setControlValue("CardSettleCheckerSubBy",username);
	}
	else if(ActivityName=="Loan_Disbursal_Maker" && getValue("LoanDisbMakerSubBy")=="")
	{
		setControlValue("LoanDisbMakerSubBy",username);
	}
	else if(ActivityName=="Loan_Disbursal_Checker" && getValue("LoanDisbCheckerSubBy")=="")
	{
		setControlValue("LoanDisbCheckerSubBy",username);
	}
	else if(ActivityName=="Mortgage_OPS_Maker" && getValue("MortgageOPSMakerSubBy")=="")
	{
		setControlValue("MortgageOPSMakerSubBy",username);
	}
	else if(ActivityName=="Mortgage_OPS_Checker" && getValue("MortgageOPSCheckerSubBy")=="")
	{
		setControlValue("MortgageOPSCheckerSubBy",username);
	}
	else if(ActivityName=="PL_Service_Maker" && getValue("PLServMakerSubBy")=="")
	{
		setControlValue("PLServMakerSubBy",username);
	}
	else if(ActivityName=="PL_Service_Checker" && getValue("PLServCheckerSubBy")=="")
	{
		setControlValue("PLServCheckerSubBy",username);
	}
	else if(ActivityName=="IOPS_Post_Disbursal_Maker" && getValue("IOPSPostDMakerSubBy")=="")
	{
		setControlValue("IOPSPostDMakerSubBy",username);
	}
	else if(ActivityName=="IOPS_Post_Disbursal_Checker" && getValue("IOPSPostDCheckerSubBy")=="")
	{
		setControlValue("IOPSPostDCheckerSubBy",username);
	}
	else if(ActivityName=="Collections_Maker" && getValue("CollMakerSubBy")=="")
	{
		setControlValue("CollMakerSubBy",username);
	}
	else if(ActivityName=="Collections_Checker" && getValue("CollCheckerSubBy")=="")
	{
		setControlValue("CollCheckerSubBy",username);
	}
	else if(ActivityName=="Investment_OPS_Maker" && getValue("InvOPSMakerSubBy")=="")
	{
		setControlValue("InvOPSMakerSubBy",username);
	}
	else if(ActivityName=="Investment_OPS_Checker" && getValue("InvOPSCheckerSubBy")=="")
	{
		setControlValue("InvOPSCheckerSubBy",username);
	}
	else if(ActivityName=="Standing_Instruction_Maker" && getValue("StandInstMakerSubBy")=="")
	{
		setControlValue("StandInstMakerSubBy",username);
	}
	else if(ActivityName=="Standing_Instruction_Checker" && getValue("StandInstCheckerSubBy")=="")
	{
		setControlValue("StandInstCheckerSubBy",username);
	}
	else if(ActivityName=="OPS_Data_Entry_Maker" && getValue("OPSDataEntryMakerSubBy")=="")
	{
		setControlValue("OPSDataEntryMakerSubBy",username);
	}
	else if(ActivityName=="OPS_Data_Entry_Checker" && getValue("OPSDataEntryCheckerSubBy")=="")
	{
		setControlValue("OPSDataEntryCheckerSubBy",username);
	}
}


function setMemopadRequired(ActivityName)
{
	if(ActivityName=='Introduction' || ActivityName=='OPS_Document_Checker' || ActivityName=='OPS_Bil_Document_Checker')
	{
		var selectedServices=getValue("SERVICE_REQUEST_SELECTED");
		var isMemoPad="";
		if(selectedServices!="" || selectedServices!=null || selectedServices!='')
		{
			isMemoPad=executeServerEvent("checkMemoPad","INTRODUCEDONE","",true);
		}	
		return isMemoPad;
	}
}


function setSystemCheckRequired(ActivityName)
{
	if(ActivityName=='Introduction')
	{
		var productHeld=getValue("PRODUCT_HELD");
		var isSystemCheck="";
		if(productHeld!='' && productHeld!="" && productHeld!=null)
		{
			isSystemCheck=executeServerEvent("isSystemCheckBorrowing","INTRODUCEDONE","",true);
		}
		else
		{
			isSystemCheck=executeServerEvent("isSystemCheckNonBorrowing","INTRODUCEDONE","",true);
		}
	}
}

/*function setAccountFreezeRequired()
{
	var AccountFreezeRequired = getValue("ACC_FREEZE_REQD");
	var isAccFreeze = "";
	if(AccountFreezeRequired!='' || AccountFreezeRequired!=null)
	{
		isAccFreeze=executeServerEvent("AccFreezeRequired","INTRODUCEDONE","",true);
		//alert("isAccFreeze--"+isAccFreeze);
	}
}
*/
function insertIntoHistoryTable()
{
	var rejectReasonsGridLength=getGridRowCount('REJECT_REASON_GRID');
	//alert("Reject Reason grid length is "+rejectReasonsGridLength);
	var historyTableInsert=executeServerEvent("InsertIntoHistory","INTRODUCEDONE",rejectReasonsGridLength,true).trim();
	return historyTableInsert;
}
function insertIntoErrorHandlingTable(ActivityName)
{
	if(ActivityName=="Introduction")
	{
		var ErrorHandlingTableInsert = executeServerEvent("InsertIntoErrorHandling","INTRODUCEDONE","",true);
		if(ErrorHandlingTableInsert == 'true')
			setControlValue("MP_INTG_REQ_INITIATION", "Required");
	}
	else if(ActivityName=='OPS_Bil_Document_Checker' || ActivityName=='OPS_Document_Checker')
	{
		var errorGridSize=getGridRowCount("Q_USR_0_PC_ERR_HANDLING");
		var srNos="";
		var callName;
		for(var i=0;i<errorGridSize;i++)
		{
			callName=getValueFromTableCell("Q_USR_0_PC_ERR_HANDLING",i,1);
			if(callName=="Memopad_Maintenance_Req")
			{
				srNos=srNos+getValueFromTableCell("Q_USR_0_PC_ERR_HANDLING",i,8)+",";
			}
		}
		srNos=srNos.substring(0,srNos.length-1);
		//if(srNos!="")
		//{
			var ErrorHandlingTableInsert = executeServerEvent("InsertIntoErrorHandling","INTRODUCEDONE",srNos,true);
			if(ErrorHandlingTableInsert == 'true')
				setControlValue("MP_INTG_REQ_OPS_REJECT", "Required");	
		//}	
	}
}

function handleException(rowIndex,colIndex,ref,controlId)
{
	if(ref.id.indexOf('checkbox')!=-1)
	{
		var exceptionRaise=executeServerEvent("raiseClearException","formload",rowIndex,true).trim();
		loadExceptions();
		
	}
}

function autoRaiseException(rowIndex,colIndex,ref,controlId)
{
	if(ref.indexOf('checkbox')!=-1)
	{
		var exceptionRaise=executeServerEvent("raiseClearException","formload",rowIndex,true).trim();
		loadExceptions();
	}
}

function enableDisableRejectReasons()
{
	var ActivityName =getWorkItemData("ActivityName");
	if((getValue("qDecision").indexOf("Reject")!=-1 || getValue("qDecision").indexOf("Additional Information Required")!=-1 ) && 
	(
	ActivityName!='ABF_Services_Doc_Checker'
	&& ActivityName!='Card_Maintenance_Doc_Maker'
	&& ActivityName!='Card_Maintenance_Doc_Checker'
	&& ActivityName!='Card_Settlement_Doc_Maker'
	&& ActivityName!='Card_Settlement_Doc_Checker'
	&& ActivityName!='CBD_CROPS_Doc_Checker'
	&& ActivityName!='Investment_OPS_Doc_Checker'
	&& ActivityName!='Investment_Product_Doc_Checker'
	&& ActivityName!='Mortgage_OPS_Doc_Checker'
	&& ActivityName!='PL_Services_Doc_Checker'
	&& ActivityName!='SME_CROPS_Doc_Checker')
	)
	{
		setStyle("REJECT_REASON_GRID","visible","true");
	}
	else
	{
		setStyle("REJECT_REASON_GRID","visible","false");
		clearTable("REJECT_REASON_GRID",true);
	}
	
	//added By Nikita for Enabling the reject reason at compliance ws on 28102019

	if(ActivityName=="Compliance")
	{
		if(getValue("qDecision").indexOf("Decline")!=-1 || getValue("qDecision").indexOf("Reject")!=-1)
		{
			setStyle("REJECT_REASON_GRID","visible","true");
		}
		else
		{
			setStyle("REJECT_REASON_GRID","visible","false");
			clearTable("REJECT_REASON_GRID",true);
		}
	}
}

//Function added by Sajan for cases put on hold CRs 21082019

function enableDisableHoldTillDate()
{
	if(ActivityName=="Card_Maintenance_Checker" || ActivityName=="Card_Settlement_Checker" || ActivityName=="Loan_Disbursal_Checker" || ActivityName=="Mortgage_OPS_Checker" || ActivityName=="PL_Service_Checker" || ActivityName=="IOPS_Post_Disbursal_Checker" || ActivityName=="Collections_Checker" || ActivityName=="Investment_OPS_Checker" || ActivityName=="Standing_Instruction_Checker" || ActivityName=="OPS_Data_Entry_Checker" || ActivityName=="Card_Maintenance_Maker" || ActivityName=="Card_Settlement_Maker" || ActivityName=="Loan_Disbursal_Maker" || ActivityName=="Mortgage_OPS_Maker" || ActivityName=="PL_Service_Maker" || ActivityName=="IOPS_Post_Disbursal_Maker" || ActivityName=="Collections_Maker" || ActivityName=="Investment_OPS_Maker" || ActivityName=="Standing_Instruction_Maker" || ActivityName=="OPS_Data_Entry_Maker")
	{
		if(getValue("qDecision")=="Send To COPS Hold")
		{
			//alert("Hello");
			setStyle("HOLD_TILL_DATE_COPS","visible","true");
		}
		else
		{
			setControlValue("HOLD_TILL_DATE_COPS", "");
			setStyle("HOLD_TILL_DATE_COPS","visible","false");
		}
	}
}

function unableDisableProductType(fieldName,fieldValue,eventType)
{
	if(getValue("REQUEST_TYPE")=='Non-Borrowing')
	{
		
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById("ProductSelected").options.length=0;
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById("ProductSelected").options.length=0;
		}
		
		//Disabling product add/remove controls
		
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('ProductListdropdown').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('ProductListdropdown').disabled=true;
		}
		
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('ProductSelected').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('ProductSelected').disabled=true;
		}
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('addButtonProduct').disabled=true;
			window.frames['iframe4'].contentWindow.document.getElementById('addButtonProduct').style.color="#2d2c2c7a";
			window.frames['iframe4'].contentWindow.document.getElementById('addButtonProduct').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('addButtonProduct').disabled=true;
			window.frames['iframe4'].document.getElementById('addButtonProduct').style.color="#2d2c2c7a";
			window.frames['iframe4'].document.getElementById('addButtonProduct').style.backgroundColor="#e4e4e4";
		}
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('removeButtonProduct').disabled=true;
			window.frames['iframe4'].contentWindow.document.getElementById('removeButtonProduct').style.color="#2d2c2c7a";
			window.frames['iframe4'].contentWindow.document.getElementById('removeButtonProduct').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('removeButtonProduct').disabled=true;
			window.frames['iframe4'].document.getElementById('removeButtonProduct').style.color="#2d2c2c7a";
			window.frames['iframe4'].document.getElementById('removeButtonProduct').style.backgroundColor="#e4e4e4";
		}
	}
	else
	{
		
		
		//Disabling product add/remove controls
		
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('ProductListdropdown').disabled=false;
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('ProductListdropdown').disabled=false;
		}
		
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('ProductSelected').disabled=false;
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('ProductSelected').disabled=false;
		}
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('addButtonProduct').disabled=false;
			window.frames['iframe4'].contentWindow.document.getElementById('addButtonProduct').style.color="#ffffff";
			window.frames['iframe4'].contentWindow.document.getElementById('addButtonProduct').style.backgroundColor="#990033";
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('addButtonProduct').disabled=false;
			window.frames['iframe4'].document.getElementById('addButtonProduct').style.color="#ffffff";
			window.frames['iframe4'].document.getElementById('addButtonProduct').style.backgroundColor="#990033";
		}
		try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('removeButtonProduct').disabled=false;
			window.frames['iframe4'].contentWindow.document.getElementById('removeButtonProduct').style.color="#ffffff";
			window.frames['iframe4'].contentWindow.document.getElementById('removeButtonProduct').style.backgroundColor="#990033";
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('removeButtonProduct').disabled=false;
			window.frames['iframe4'].document.getElementById('removeButtonProduct').style.color="#ffffff";
			window.frames['iframe4'].document.getElementById('removeButtonProduct').style.backgroundColor="#990033";
		}
	}
}
function setIsSystemCheckApproval(ActivityName)
{
	if(ActivityName=="Introduction" || ActivityName=="OPS_Maker" || ActivityName=="OPS_Document_Checker" || ActivityName=="OPS_Bil_Document_Checker" || ActivityName=="Error_Hand_Data_Entry_Checker" || ActivityName=="Control_Maker" || ActivityName=="Control_Checker" || ActivityName=="Compliance" || ActivityName=="Compliance_WC" || ActivityName=="Compliance_Manager")
	{
		var isExceptionRaised=ifExceptionRaised();
		if(isExceptionRaised==true)
		{
			setControlValue("ISSYSTEMCHECKSAPPROVAL","Required");
		} 
		else
		{
			setControlValue("ISSYSTEMCHECKSAPPROVAL","No");
		}
	}
}

function setOriginalAndAdditionalDocs(ActivityName)
{
	if(ActivityName=="OPS_Document_Checker" || ActivityName=="OPS_Bil_Document_Checker")
	{
		//var isExceptionRaised=ifExceptionRaised();
		if(getValue("ORIGINAL_HELD")=="No")
		{
			setControlValue("ISORGADDDOCOBT","Required");
		}
		/*else if(isExceptionRaised==true)
		{
			setControlValue("ISORGADDDOCOBT","Required");
		}*/
		else if(getValue("SECURITY_CHEQUE_REQD")=="Yes")
		{
			setControlValue("ISORGADDDOCOBT","Required");
		}
		else 
		{
			setControlValue("ISORGADDDOCOBT","No");
		}
	}
	if(ActivityName=="Credit_Manager" && getValue("qDecision")=="Approve Subject to")
	{
		setControlValue("ISORGADDDOCOBT","Required");
	}
}

function ifExceptionRaised()
{
	var exceptionGridCount=getGridRowCount("Q_USR_0_PC_EXCEPTION_HISTORY");
		var exceptionName;
		for(var i=0;i<exceptionGridCount;i++)
		{
			exceptionName=getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,1);
			if(exceptionName=="UID Match" || exceptionName=="High Risk Score" || exceptionName=="Blacklist" || exceptionName=="UBO Approval")
			{
				if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==true)
				{
					//alert("Exception is raised");
					return true;
				}
			}
		}
		return false;
}
function ifHighRiskExceptionRaised()
{
	if(ActivityName=='OPS_Maker' || ActivityName=='OPS_Document_Checker' || ActivityName=='OPS_Bil_Document_Checker' || ActivityName=='Control_Maker' || ActivityName=='Control_Checker' || ActivityName=='Compliance_WC' || ActivityName=='Compliance' || ActivityName=='Compliance_Manager')
	{
		var exceptionGridCount=getGridRowCount("Q_USR_0_PC_EXCEPTION_HISTORY");
		var exceptionName;
		var highRiskFlag="";
		var otherException="";
		for(var i=0;i<exceptionGridCount;i++)
		{
			exceptionName=getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,1);
			if(exceptionName=="UID Match" || exceptionName=="Blacklist" || exceptionName=="UBO Approval")
			{
				if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==true)
				{
					otherException='Y';
				}
			}
			if(exceptionName=="High Risk Score")
			{
				if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==true)
				{
					highRiskFlag='Y';
				}
			}
		}
		
		if(highRiskFlag=='Y')
		{
			setControlValue("ISHIGHRISKRAISED","Yes");
		}
		else
		{
			setControlValue("ISHIGHRISKRAISED","No");
		}
		
		if(otherException=='Y')
		{
			setControlValue("ISCOUNTEXCEPTION","2");
		}
		else
		{
			setControlValue("ISCOUNTEXCEPTION","0");
		}
	}
}


function setArchivalPath(ActivityName)
{
		if(ActivityName=="Introduction")
		{
			setValues({"ARCHIVALPATHSUCCESS":"Omnidocs\\CentralOperations\\&<CIF_ID>&\\Profile Change\\&<WI_NAME>&"},true);
			setValues({"ARCHIVALPATHREJECT":"Omnidocs\\CentralOperations\\&<CIF_ID>&\\Rejected\\Profile Change\\&<WI_NAME>&"},true);
		}
	
}

//Added By Sajan on 28/06/2019 for CRs
function setDeferralHeld()
{
	var DeferralGridCount=getGridRowCount("Q_USR_0_PC_DEFERRAL_GRID");
	if(DeferralGridCount>0)
	{
		setControlValue("ISDEFERRALHELD","Yes");
	}
	else
	{
		setControlValue("ISDEFERRALHELD","No");
	}
}

function autoRaiseHighRiskException()
{
	var RiskScore = getValue('RISK_RATING');
	var exceptionGridCount=getGridRowCount("Q_USR_0_PC_EXCEPTION_HISTORY");
	var exceptionName;
	for(var i=0;i<exceptionGridCount;i++)
	{
		exceptionName=getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,1);
		if(exceptionName=="High Risk Score")
		{
			if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==false || getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)== "")
			{
				// if workitme came from Obtain_Org_Add_Documents, and entered RiskScore and Existing RiskScore at OBT queue is same then exception will not be raised automatically
				var EXISTING_RISKSCORE_ATOBTQUEUE = getValue("EXISTING_RISKSCORE_ATOBTQUEUE");
				if(RiskScore != '' && RiskScore !='0' && EXISTING_RISKSCORE_ATOBTQUEUE !='' && EXISTING_RISKSCORE_ATOBTQUEUE !='0')
				{
					if (RiskScore == EXISTING_RISKSCORE_ATOBTQUEUE)
					{
						setControlValue("ISHIGHRISKRAISED","No");
						continue;
					}	
				}
				//***************************************************************
				if(parseFloat(RiskScore) >= 4.05)
				{
					setTableCellData("Q_USR_0_PC_EXCEPTION_HISTORY",i,0,true,true);
					autoRaiseException(i,0,"checkbox","");
					setControlValue("ISHIGHRISKRAISED","Yes");
				}
				else
				{
					setControlValue("ISHIGHRISKRAISED","No");
				}
			} 
			else
			{
				setControlValue("ISHIGHRISKRAISED","Yes");
			}
			break;
		}
	}
}

function autoRaiseUIDException()
{
	var exceptionGridCount=getGridRowCount("Q_USR_0_PC_EXCEPTION_HISTORY");
	var exceptionName;
	for(var i=0;i<exceptionGridCount;i++)
	{
		exceptionName=getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,1);
		if(exceptionName=="UID Match")
		{
			if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==false || getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)== "")
			{
				var UIDGridCount=getGridRowCount("Q_USR_0_PC_UID_DTLS");
				// if workitme came from Obtain_Org_Add_Documents, and entered UID Count and Existing UID Count at OBT queue is same then exception will not be raised automatically
				var EXISTING_UIDCOUNT_ATOBTQUEUE = getValue("EXISTING_UIDCOUNT_ATOBTQUEUE");
				if(UIDGridCount != '' && UIDGridCount !='0' && EXISTING_UIDCOUNT_ATOBTQUEUE !='' && EXISTING_UIDCOUNT_ATOBTQUEUE !='0')
				{
					if (UIDGridCount == EXISTING_UIDCOUNT_ATOBTQUEUE)
					{
						setControlValue("ISUIDEXCEPTIONRAISED","No");
						continue;
					}	
				}
				//***************************************************************
				
				//if (UIDGridCount > 0)
				if(uid_match_status_exception())
				{
					setTableCellData("Q_USR_0_PC_EXCEPTION_HISTORY",i,0,true,true);
					autoRaiseException(i,0,"checkbox","");
					setControlValue("ISUIDEXCEPTIONRAISED","Yes");
				} 
				else
				{
					setControlValue("ISUIDEXCEPTIONRAISED","No");
				}
			}
			else
			{
				setControlValue("ISUIDEXCEPTIONRAISED","Yes");
			}
			break;
		}
	}
}
function riskRatingValidation()
{
	if(ActivityName=='CBWC_Maker' && getValue("qDecision")=='Submit')
	{
		riskRatingFieldMandate('true', ActivityName);
	}
	else
	{
		riskRatingFieldMandate('false', ActivityName);
	}
	
}
function isSTLAttachedDocType(sDocTypeNames)
{
	var arrAvailableDocList = window.parent.getInterfaceData('D');
	
	var arrSearchDocList = sDocTypeNames.split(",");
	var bResult=false;
	for(var iSearchCounter=0;iSearchCounter<arrSearchDocList.length;iSearchCounter++)
	{
		bResult=false;
		for(var iDocCounter=0;iDocCounter<arrAvailableDocList.length;iDocCounter++)
		{
			if (arrAvailableDocList[iDocCounter].name == arrSearchDocList[iSearchCounter]) {
				bResult = true;
				break;
			}
		}
		if(!bResult){
		return false;
		}
	}            
	return true;
}

function uid_match_status_exception(){
	
	var getUIDTablerowCount = getGridRowCount('Q_USR_0_PC_UID_DTLS');
	if(getUIDTablerowCount == 0){
		return false;
	}
	for(var j=0;j<getUIDTablerowCount;j++)
	{
		var matchStatus = getValueFromTableCell("Q_USR_0_PC_UID_DTLS",j,3);
			if(!matchStatus || matchStatus.trim().toLowerCase() ==="true"){
				return true;
			}
	}
	return false;
}