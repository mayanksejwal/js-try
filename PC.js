var PC_onLoad = document.createElement('script');
	PC_onLoad.src = '/PC/PC/CustomJS/PC_onLoad.js';
	document.head.appendChild(PC_onLoad);
	
var PC_mandatory = document.createElement('script');
	PC_mandatory.src = '/PC/PC/CustomJS/PC_MandatoryFieldValidations.js';
	document.head.appendChild(PC_mandatory);

var EIDA_read = document.createElement('script');
	EIDA_read.src = '/PC/PC/CustomJS/EIDA/eida_read.js';
	document.head.appendChild(EIDA_read);
	
	
var PC_onSaveDone = document.createElement('script');
PC_onSaveDone.src = '/PC/PC/CustomJS/PC_onSaveDone.js';
document.head.appendChild(PC_onSaveDone);

var PC_Common = document.createElement('script');
PC_Common.src = '/PC/PC/CustomJS/PC_Common.js';
document.head.appendChild(PC_Common);

var PC_EventHandler = document.createElement('script');
PC_EventHandler.src = '/PC/PC/CustomJS/PC_EventHandler.js';
document.head.appendChild(PC_EventHandler);

var PC_IntegrationEvents = document.createElement('script');
PC_IntegrationEvents.src = '/PC/PC/CustomJS/PC_IntegrationEvents.js';
document.head.appendChild(PC_IntegrationEvents);



function setCommonVariables()
{
	Processname = getWorkItemData("ProcessName");
	ActivityName =getWorkItemData("ActivityName");
	WorkitemNo =getWorkItemData("processinstanceid");
	cabName =getWorkItemData("cabinetname");
	user= getWorkItemData("username");	
	viewMode=window.parent.wiViewMode;
	
}

function afterFormload()
{
	setCommonVariables();
	populateDecisionDropDown();
	//checkIfSamplingRequired(); moved it on Done of Introduction only
	setControlValue("LoggedInUser",user);	
	setControlValue("qDecision","");  //setting decision drop down values as Select initially
	setControlValue("REMARKS","");  //setting remarks blank at load, so remarks entered at last queue won't be displayed
	setStyle("frame21", "visible", "false");//CLM
    setStyle("frame22","visible","false");// flag for initiator reject	
	loadServiceRequests('ServiceRequestdropdown',getValue('SERVICE_REQUEST_TYPE'),'load');
	loadProducts('ProductListdropdown');
	loadRelatedCif(); //Error is there
	

	if(ActivityName=="Introduction" && getValue("SERVICE_REQUEST_TYPE").trim() != '' && getValue("SERVICE_REQUEST_TYPE").trim() != 'Select'){
		addDataToChecklist("Q_USR_0_PC_CHECKLIST_GRID");
	}
	
	addDataToMemoPad("tblMemopad");
	loadExceptions(); //Error is there
	//getDuplicateWorkitems(); // no need to call at onload, it will be loaded on complex grid by default
	setMemopadSrNoDisable();
	enableDisableSearchControls();
	enableDisableAfterFormLoad();
	//handleErrorHandlingFieldEditableforCellChange();
	//alert("2 4");
	applyToolTiponFields();
	if(ActivityName=="Introduction")
	{
		loadSolId(user);
		var d = new Date();
		var today = formatDate(d,3);
		setControlValue("APPLICATION_DATE",today);
		waiverOfChargeValidation();
		collectChargeValidation();
		eosbValidation();
		phoneBankingLogic();
		if(getValue("CIF_TYPE")=="Non-Individual")
		{
			setValue("EMP_TYPE","");
			setStyle("EMP_TYPE","disable","true");
		}
	}
	if(ActivityName=="Initiator_Reject" && getValue("OPSDataEntryCheckerSubBy")=="")
	{
		waiverOfChargeValidation();
		collectChargeValidation();
	}
	//Added on 23_07_25 for Initaition Reject
	if(ActivityName=="Initiator_Reject"){
		var flag=getValue("ops_flag");
		if(flag=="Y"){
			removeItemFromCombo("qDecision",2);
		}
	}
	//added on 12-01-25 for CLM
	if(ActivityName == "OPS_Data_Entry_Checker")
	{
	var CheckerMail = executeServerEvent("EmailIdFromPDBUser","INTRODUCEDONE",getWorkItemData("username"),true).trim();
	setValues({"CHECKER_MAILID":CheckerMail},true);	
	}
	
	if(ActivityName == "OPS_Data_Entry_Maker" || ActivityName == "OPS_Data_Entry_Checker" || ActivityName == "OPS_Document_Checker" || ActivityName == "CSM"){
		if(getValue('ACC_FREEZE_REQD') == 'Required')
			showMessage("","This WI has come from Freeze Queue, Please ensure to Unfreeze the Account if it’s in Freeze Status.","error");
		if((ActivityName == 'OPS_Data_Entry_Checker' || ActivityName == 'OPS_Data_Entry_Maker' || ActivityName == 'CSM'  
		|| ActivityName == 'OPS_Document_Checker') && (collectChargeAccCount()))
		{
			setStyle("BAL_FETCH_BTN","disable", "false")
		}
		else
		{
			setStyle("BAL_FETCH_BTN","disable", "true")
		}
	}
	
	if((ActivityName == "CBWC_Maker" || ActivityName == "Introduction")){
		riskRatingSectionDisable('false', ActivityName);
		if(ActivityName == "CBWC_Maker")
		{
			riskRatingFieldMandate('true', ActivityName);
			if(getValue("CIF_TYPE")=="Non-Individual")
			{
				setValue("EMP_TYPE","");
				setStyle("EMP_TYPE","mandatory","false");
				setStyle("EMP_TYPE","disable","true");
			}
		}
		else{
			if(getValue("CIF_TYPE")=="Non-Individual")
			{
				setValue("EMP_TYPE","");
				setStyle("EMP_TYPE","disable","true");
			}
		}
		
	} else {
		riskRatingSectionDisable('true', ActivityName);
	}
	
	if(!(ActivityName == "OPS_Document_Checker" || ActivityName == "CBWC_Maker" || ActivityName == "Introduction" || ActivityName == "CSM")){
		disableListView('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS');
	}
	//CLM
	if (getWorkItemData("ActivityName") == "CLM_Maker" || getWorkItemData("ActivityName") == "CLM_Checker") {
		setStyle("frame21","visible","true");
		CLMOnLoad();
		
	}
	//saveWorkItem();
}

function customValidationsBeforeSaveDone(op)
{
	if(op=="S")
	{
		setCustomControlsValue();
		if(ActivityName=="Introduction")
		{
			var returnForAccountFreeze=executeServerEvent("isAccountFreezeEditNonEdit","INTRODUCEDONE","",true).trim();
			setControlValue("ISACCOUNTFREEZE",getValue("ACC_FREEZE_REQD"));
		}
		//setMemopadRequired();
		//setSystemCheckRequired();
		//setAccountFreezeRequired();
		return true;
	}
	else if (op=="I" || op=="D")
	{
		setCustomControlsValue();
		
		if(ActivityName=="Introduction")
		{
			var returnForAccountFreeze=executeServerEvent("isAccountFreezeEditNonEdit","INTRODUCEDONE","",true).trim();
			setControlValue("ISACCOUNTFREEZE",getValue("ACC_FREEZE_REQD"));
			
			var loggedInUser_Group = executeServerEvent("loggedInUser_Group","INTRODUCEDONE","",true).trim();
			/*var status = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
			if(status == "PL_Type" && getValue('SERVICE_REQUEST_SELECTED').includes("Employer Change – EOSB Release") && (getValue("EOSB_LP_DATE") == null || getValue("EOSB_LP_DATE") == ""))
			{
				showMessage("","Please select EOSB lien placement date customer account is PL type .","error");
				return false;
			}*/
		}
		if((ActivityName == 'CBWC_Maker' && getValue('qDecision') != 'Additional Information Required Initiator') || (ActivityName == 'OPS_Document_Checker' && getValue('qDecision') != 'Additional Information Required Initiator' && getValue('qDecision') != 'Bilingual Documents' && getValue('qDecision') != 'Reject to CBWC Maker' && getValue("ISSYSTEMCHECKS")=='Required')){
			var newRowFlag = CheckNewRecordInRelPartyTable();
			var CheckDataModifiedFlag = CheckDataModified();
			
			if(newRowFlag && CheckDataModifiedFlag)  
			{
				if(getValue('qDecision') != 'Re-perform checks'){
					showMessage("qDecision","User can take the decision as Re-perform checks.","error");
					return false;
				}
				
			}
			else if(newRowFlag)  
			{
				if(getValue('qDecision') != 'Perform Additional Checks')
				{
					showMessage("qDecision","User can take the decision as Perform Additional Checks.","error");
					return false;
				}
			}
			else if(CheckDataModifiedFlag)  
			{
				if(getValue('qDecision') != 'Re-perform checks'){
					showMessage("qDecision","User can take the decision as Re-perform checks.","error");
					return false;
				}
				
			}
		}
		if(mandatoryFieldValidation()==false)
		{
			return false;
		}
		
		if(ActivityName=="Introduction")
		{
			if(getGridRowCount("Q_USR_0_PC_CHECKLIST_GRID") == 0 || getGridRowCount("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID") == 0)
			{
				showMessage("","Operations Checklist are not saved, Kindly select/change Service Request Type to load Checklist.","error");
				return false;
			}
		}
		if(ActivityName=="OPS_Document_Checker")
		{
			var status = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
			if((getValue("SERVICE_REQUEST_SELECTED").indexOf("Employer Change – EOSB Release")!=-1) && status == "PL_Type" && getValue("ISCREDITAPPROVAL") != "Required")
			{
				showMessage("","Please select Credit Approval required as Yes since customer account is PL type .","error");
				return false;
			}
		}
		//CLM
		if (getWorkItemData("ActivityName") == "CLM_Maker") {
				var docInterface = window.parent.getInterfaceData('D');
				var size=getGridRowCount("table44");
				var selecteddocs=[];
				for(var i =0;i<size;i++){
                   selecteddocs[i]=getValueFromTableCell("table44",i,0);                 
                   } 
				var bResult=false;

				for(var iSearchCounter=0;iSearchCounter<selecteddocs.length;iSearchCounter++){
					bResult=false;
					for(var iDocCounter=0;iDocCounter<docInterface.length;iDocCounter++)
					{				
						if (docInterface[iDocCounter].name.toUpperCase().indexOf(selecteddocs[iSearchCounter].toUpperCase()) >= 0) {
						bResult = true;
						break;
					    }
				    }
					if(!bResult){
						alert("Please attach "+selecteddocs[iSearchCounter]+" document to proceed further");
						return false;
				    }
				} 
				var flag =getValue("viewclm_flag");
				if(flag == "" || flag == "null" || flag == null ){
					 alert("Kindly access CLM Platform");
					 return false;
				}
				//Decision select check
				var decision=getValue("qDecision");
				if(decision=='--Select--' || decision==null || decision.trim()==""){
					alert("Please select Decision");
					getValue("qDecision").focus();
					return false;
				}
				//CLM STATUS API Request
				var apiStatus = getCLMstatus();
				if(apiStatus==false){
					return false;
			  }				
			}
		if (getWorkItemData("ActivityName") == "CLM_Checker") {
				var docInterface = window.parent.getInterfaceData('D');
				var size=getGridRowCount("table44");
				var selecteddocs=[];
				for(var i =0;i<size;i++){
                   selecteddocs[i]=getValueFromTableCell("table44",i,0);                 
                   } 
				
				var bResult=false;

				for(var iSearchCounter=0;iSearchCounter<selecteddocs.length;iSearchCounter++){
					bResult=false;
					for(var iDocCounter=0;iDocCounter<docInterface.length;iDocCounter++)
					{				
						if (docInterface[iDocCounter].name.toUpperCase().indexOf(selecteddocs[iSearchCounter].toUpperCase()) >= 0) {
						bResult = true;
						break;
					    }
				    }
					if(!bResult){
						alert("Please attach "+selecteddocs[iSearchCounter]+" document to proceed further");
						return false;
				    }
				} 
				var flag =getValue("viewclm_flag");
				if(flag == "Maker_clicked" || flag == "null" || flag == null ){
					 alert("Kindly access CLM Platform");
					 return false;
				}
				//Decision select check
				var decision=getValue("qDecision");
				if(decision=='--Select--' || decision==null || decision.trim()==""){
					alert("Please select Decision");
					getValue("qDecision").focus();
					return false;
				}
				//CLM STATUS API Request
				if(getValue("qDecision") =='CLM Activity Approved')
				var apiStatus = getCLMstatus();
				
				if(apiStatus==false){
					return false;			      
			}
		}
			//Added on 31_12_25 for CLM
			if (getWorkItemData("ActivityName") == "Account_Closure") {
				if(getValue("qDecision")=="Account Retained"){
					setValues({"ACCOUNT_CLOSURE_EXPIRY":""},true);	
				}
			}
			
		if(getGridRowCount('Q_USR_0_PC_DUPLICATE_WI')>0){
		}
		else
		{
			if(ActivityName=="Introduction")
				getDuplicateWorkitems();
		}
		 //var confirmDoneResponse = showMessage("","Continue?","confirm");
		 console.log('flag check....');
		var confirmDoneResponse = confirm("You are about to submit the workitem. Do you wish to continue?");
		
		if(confirmDoneResponse ==  true)
		{
			setMemopadRequired(ActivityName);
			setUserToWorkOnThisWI(user);
			setSystemCheckRequired(ActivityName);
			DocTypeAttachedObtainOrgDoc(ActivityName);
			
			if(ActivityName=="Introduction")
			{
				if(getValue("EOSB_LP_DATE") == null || getValue("EOSB_LP_DATE") == "")
				{
					setControlValue("EOSB_LP_DATE","01/01/2031");
				}
				setControlValue("INITIATOR_MAILID",executeServerEvent("EmailIdFromPDBUser","INTRODUCEDONE",user,true).trim());
				setControlValue("RM_MAILID",executeServerEvent("EmailIdFromPDBUser","INTRODUCEDONE",getValue("ARM_CODE"),true).trim());
			}
			if(ActivityName=='CBWC_Maker' || ActivityName=='OPS_Document_Checker' || ActivityName=='OPS_Bil_Document_Checker')	
			{
				autoRaiseHighRiskException();
				autoRaiseUIDException();
			}
			if(ActivityName == 'CBWC_Maker' || ActivityName == 'OPS_Document_Checker'){
				if(getValue('qDecision') == 'Re-perform checks'){ // resetting the statuses for all rows
					
					var rpCount = getGridRowCount('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS');
					
					for(var i=0; i<rpCount; i++){
						setTableCellData('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS',i,21,'',true);
						setTableCellData('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS',i,22,'',true);
						setTableCellData('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS',i,23,'',true);
						setTableCellData('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS',i,24,'',true);
					}				
				}
			}
			
			setIsSystemCheckApproval(ActivityName);
			setOriginalAndAdditionalDocs(ActivityName);
			ifHighRiskExceptionRaised();
			setArchivalPath(ActivityName);
			checkIfSamplingRequired(); //executing it on Introduction only
			setDeferralHeld();  //Added By Sajan on 28/06/2019 for CRs
			
			try {	
				saveWorkItem();
			} catch(ex) {
				showMessage("qDecision",'Kindly click on Save button then Close the workitem, Open it again and submit with appropriate decision.',"error");
				return false;
			}
			
			if(getValue('qDecision')=="" || getValue('qDecision')=='Select')
				return false;
			
			
			
			// Send mail and sms
			if(ActivityName=="Introduction")
			{
				var status = executeServerEvent("SendMAILSMSAlert","INTRODUCEDONE",user,true).trim();
				if(getValue("loggedInUser_Group") == 'PB_Group')
				{
					var statusCustRespo = executeServerEvent("SendMAILSMSAlert_ForCustResponse","INTRODUCEDONE",user,true).trim();
				}
			}
			else if(ActivityName=="OPS_Document_Checker" || ActivityName=="OPS_Bil_Document_Checker")
			{
				if (getValue("qDecision") == 'Approve')
				{
					var status = executeServerEvent("SendMAILSMSAlert","INTRODUCEDONE",user,true).trim();
				}
					
			}
			else if(ActivityName=="OPS_Data_Entry_Checker")
			{
				if (getValue("qDecision") == 'Approve')
				{
					if(getValue("SERVICE_REQUEST_SELECTED").indexOf("Marking of Deceased Account")!=-1|| getValue("SERVICE_REQUEST_SELECTED").indexOf("Signatory / Shareholder Deceased")!=-1)
					{
						var status = executeServerEvent("SendMAILSMSAlert_ForDataEntryChecker","INTRODUCEDONE",user,true).trim();
					}
					if(collectChargeAccCount())
					{
						if(getValue("COLLECT_CHARGE_STATUS")=="Y")
						{
							alert('Seems funds gets collected, Kindly Check in Finacle');
							//setTimeOut(1000);
						}
						else
						{
							setValue("COLLECT_CHARGE_STATUS","Y");
							saveWorkItem();
							var chargeStatus=executeServerEvent("CollectionCharge","CLICK","",true).trim();
							if(chargeStatus!="Success")
							{
								setValue("COLLECT_CHARGE_STATUS","");
								saveWorkItem();
								showMessage("",'Some error occured at server end',"error");
								return false;
							}
							else if(chargeStatus=="Not Sufficient funds")
							{
								setValue("COLLECT_CHARGE_STATUS","");
								saveWorkItem();
								showMessage("",'Balance is not sufficient to collect charge for this PC request',"error");
								return false;
							}
						}
					}
					
				}
			}
			/*else if(ActivityName=="Archival")
			{
				if (getValue("qDecision") == 'Documents Archived')
					var status = executeServerEvent("SendMAILSMSAlert_ForArchival","INTRODUCEDONE",user,true).trim();
			}*/
			//validate history grid count with data saved at the backend - added on 10112021 for grid data not saving product issue.	
			var status = insertIntoHistoryTable();
			if(status != 'INSERTED')
				return false;
			insertIntoErrorHandlingTable(ActivityName);	
			var historyStatus=executeServerEvent("validateDecHitoryGridCountWithDatabase","INTRODUCEDONE","",true).trim();
			if(historyStatus == "Count Not Matched")
			{
				showMessage("DECISION",'There is some error occured. Kindly Close the workitem, Open it again and submit with appropriate decision.',"error");
				return false;
			}
			//*******************
			
			return true;
		}
		else
		{
			return false;
		}
	}
	return false;
}


function eventDispatched(controlObj,eventObj)
{
	//mandatoryFieldValidation();
	//customEventHandler();
	var controlId=controlObj.id;
	var controlEvent=eventObj.type;
	var ControlIdandEvent = controlId+'_'+controlEvent;

	switch(ControlIdandEvent)
	{
		case 'btn_CIF_Search_click':			
			mainCIFSearchClickHandling(controlId);
			break;
		
		case 'btn_CIF_Clear_click':			
			clearBtnClickHandling();
			break;
		
		case 'btn_View_Signature_click':			
			getSignature();
			break;
		
		case 'btn_EIDA_Reader_click':			
			eida_read();
			break;
			
		case 'SERVICE_REQUEST_TYPE_change' : 
			loadServiceRequests('SERVICE_REQUEST_TYPE',getValue('SERVICE_REQUEST_TYPE'),'onChange');
			clearTable("Q_USR_0_PC_CHECKLIST_GRID",true);
			executeServerEvent("Q_USR_0_PC_CHECKLIST_GRID","FormLoad","",true).trim();
			enableDisableCheckListGrid();
			clearTable("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID",true);
			executeServerEvent("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID","FormLoad","",true).trim();
			enableDisableOPSDocCheckListGrid();
			break;
		case 'REQUEST_TYPE_change' : 
			unableDisableProductType('REQUEST_TYPE',getValue('REQUEST_TYPE'),'onChange');
			break;
		case 'qDecision_change' : 
			enableDisableRejectReasons();
			enableDisableHoldTillDate();
			riskRatingValidation();
			break;
		case 'ISNOTREQUIRE_change' :
			disableothercheckbox();
			
		case 'Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_EXISTING_CUST_change':
			if(getValue(controlId) == 'ETB')
				setStyle('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_CIF_ID','mandatory','true');
			else
				setStyle('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_CIF_ID','mandatory','false');
			break;
			
		case 'Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_CIF_TYPE_change':
			relatedPartyValidations(controlId);
			break;
		
		case 'RISK_BTN_click':
			saveWorkItem();
			if(riskScoreValidation())
			{
				var resp = executeServerEvent("BtnRiskScore", "CLICK", "", true);
				saveWorkItem();
				if(resp.indexOf('Error')!=-1){ // changed
					showMessage("",'Unable to fetch Risk Score.',"error");
					setStyle('RISK_RATING','disable','false');
				}
				else
				{
					setStyle('RISK_RATING','disable','true');
				}
			   var AjxResponse;
	           var AjxResponseTxt;
	           if(resp.indexOf("~") != -1)
	           {            
	               var ajaxResult=resp.split("~");
	               //alert("ajaxResult--"+ajaxResult);
	               AjxResponse=ajaxResult[0];

	           }
	           else{
					 AjxResponse=response;
				}

	           if(AjxResponse == "0000")
	           {   
	               //window.opener.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);    
	               //showMessage(controlName,"Successful in attaching Black List Document","error");
	               //deleteTemplateFromServer(ajaxResult[4]);

	               //Raising automatic exception***********************************************************

	               //**************************************************************************************
	               window.parent.customAddDoc(ajaxResult[1],ajaxResult[2],ajaxResult[3]);
	               showMessage("","Successful in attaching risk rating file","error");
	               //deleteTemplateFromServer(ajaxResult[4]);
	           }
	           else if(AjxResponse.indexOf("0000")==-1)
	           {
	               showMessage("","Error in attaching risk rating file","error");            
	           }        
	           else if(AjxResponse.indexOf("Error")!=-1)
	           {
	               showMessage("","Problem in fetching attach  risk rating","error");        
	           }
			}
			break;
		case 'SUB_SEGMENT_change' :
			collectChargeValidation();
			waiverOfChargeValidation();
			break;
		case 'qPriority_change' :
			collectChargeValidation();
			waiverOfChargeValidation();
			break;
		case 'WAIVER_OF_CHARGES_change' :
			collectChargeValidation();
			break;
		case 'BAL_FETCH_BTN_click' :
			balFetchEnquiry();
			break;
		case 'ORIGINAL_HELD_change' :
			if(getValue("ORIGINAL_HELD")=="Yes")
			{
				setControlValue("ISSAMPLINGREQUIRED","Yes");
			}
			else
			{
				setControlValue("ISSAMPLINGREQUIRED","No");
			}
			break;
	}
}

function disableothercheckbox()
{
	setStyle("ISCARDMAINTENANCE","disable","true");
	setControlValue("ISCARDMAINTENANCE","");	
	setStyle("ISCARDSETTLEMENT","disable","true");
	setControlValue("ISCARDSETTLEMENT","");
	setStyle("ISLOANDISBURSAL","disable","true");
	setControlValue("ISLOANDISBURSAL","");
	setStyle("ISMORTGAGEOPS","disable","true");
	setControlValue("ISMORTGAGEOPS","");
	setStyle("ISPLSERVICES","disable","true");
	setControlValue("ISPLSERVICES","");
	setStyle("ISIOPSPOSTDISBURSAL","disable","true");
	setControlValue("ISIOPSPOSTDISBURSAL","");
	setStyle("ISCOLLECTIONS","disable","true");
	setControlValue("ISCOLLECTIONS","");
	setStyle("ISINVESTMENTOPS","disable","true");
	setControlValue("ISINVESTMENTOPS","");
	setStyle("ISSTANDINGINSTRUCTION","disable","true");
	setControlValue("ISSTANDINGINSTRUCTION","");

}


function onTableCellChange(rowIndex,colIndex,ref,controlId)
{
	//alert("abc");
	//alert("abc 1 "+rowIndex);
	//alert("abc 2 "+colIndex);
	//alert("abc 3 "+ref.id);
	//alert("abc 4 "+controlId);
	if(controlId=='Q_USR_0_PC_EXCEPTION_HISTORY')
	{
		handleException(rowIndex,colIndex,ref,controlId);
	}
	
	if(controlId=='Q_USR_0_PC_CIF_DETAILS')
	{
		handleonChangecif(rowIndex,colIndex,ref,controlId);
	}
	
	if(controlId=='Q_USR_0_PC_ERR_HANDLING')
	{
		setDateInErrorGrid(rowIndex,colIndex,ref,controlId);
	}
	
	if(controlId=='Q_USR_0_PC_MEMOPAD_GRID')
	{
		validateInputtedMemoPad(rowIndex,colIndex,ref,controlId);
	}
}

function applyToolTiponFields()
{
	$(document).ready(function() {
		$("input:text,select,textarea,multiple").wrap("<div class='tooltip-wrapper' style='display:inline'></div>");
		$("div.tooltip-wrapper").mouseover(function() {
			$(this).attr('title', $(this).children().val());
		});
	});
}

function customOnRowClick(controlId, action){

	if(getValue('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_EXISTING_CUST') == 'ETB')
		setStyle('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_CIF_ID','mandatory','true');
	else
		setStyle('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_CIF_ID','mandatory','false');			
	
	relatedPartyValidations('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_CIF_TYPE');
}

function CheckNewRecordInRelPartyTable() // if(ActivityName == 'ops_doc/cbwc')
{
	var CRGridSize=getGridRowCount('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS');
	for(var i=0; i<CRGridSize; i++)
	{
		var dedupe_status = getValueFromTableCell("Q_USR_0_PC_RELATED_PARTY_GRID_DTLS",i,21); 
		var blacklist_status = getValueFromTableCell("Q_USR_0_PC_RELATED_PARTY_GRID_DTLS",i,22); 
		var firco_status = getValueFromTableCell("Q_USR_0_PC_RELATED_PARTY_GRID_DTLS",i,23); 
		
		if(dedupe_status == '' || blacklist_status == '' || firco_status == '') return true;		
	}	
	
	return false;
}

function CheckDataModified(){
	
	var InputParamsColNames = ["EXISTING_CUST","CIF","COMPANYFLAG","COMPANY_NAME","FIRSTNAME","LASTNAME"];				
	var RPColNames = ["EXISTING_CUST","CIF_ID","CIF_TYPE","COMPANY_NAME","FIRST_NAME","LAST_NAME"];
	var RPColIndexes = [0,1,2,4,5,7];
	
	var RPGridSize = getGridRowCount('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS');

	for(var i=0; i<RPGridSize; i++){
		var map = new Map();
		var InputParamsInRel = getValueFromTableCell("Q_USR_0_PC_RELATED_PARTY_GRID_DTLS", i, 24);
		if(InputParamsInRel != null && InputParamsInRel != "")
		{
			var InputParamsInRelCols = InputParamsInRel.split("|#|");
			// var RPColName = RPColNames[i];
			for(var k=0; k<InputParamsInRelCols.length; k++){
				var x = InputParamsInRelCols[k];
				var key = x.split("~#~")[0];
				var val = x.split("~#~")[1];
				
				map.set(key,val);
			}
			
			for(var j=0; j<RPColIndexes.length; j++){
				var dynamicVal = getValueFromTableCell("Q_USR_0_PC_RELATED_PARTY_GRID_DTLS", i, RPColIndexes[j]);
				var staticVal = map.get(InputParamsColNames[j]);
				
				if(dynamicVal != staticVal) return true;
			}	
		}		
	}
	
	return false;
}

function CheckDataModifiedbkp() // CIF~#~0267900|#|COMPANYFLAG~#~C|#|FIRSTNAME~#~Nirav
{
	var ExtColumnNames = ["CIF_NUMBER","APPLICANT_FULL_NAME","PEP_STATUS","COMPANY_NAME","TL_NUMBER"];				
	var RelColumnNames = ["EXISTING_CUST","CIF_ID","CIF_TYPE","COMPANY_NAME","FIRST_NAME","LAST_NAME"];
	var RelColumnIndexes = [0,1,2,4,5,7];

	var INTEGRATION_INPUT_PARAMSInExt = getValue("INTEGRATION_INPUT_PARAMS");
	var ReperformFlag = false;
	
	// Ext table for Main Company
	if(INTEGRATION_INPUT_PARAMSInExt.indexOf("|#|") != -1)
	{
		var resultDataSplitted = INTEGRATION_INPUT_PARAMSInExt.split("|#|");
		for(var i=0;i<resultDataSplitted.length;i++)
		{
			var innerData = resultDataSplitted[i].split("~#~");
			for(var j=0;j<innerData.length;j++)
			{
				for (var k=0;k<ExtColumnNames.length;k++)
				{
					if(ExtColumnNames[k].indexOf(innerData[0]) != -1)
					{												
						if(myTrim(getValue(innerData[0])) != myTrim(innerData[1]))
						{
							ReperformFlag = true;
							break;
						}
					}
				}								
				if(ReperformFlag)
					break;
			}
			if(ReperformFlag)
				break;
		}
	}
	
	//RelatedParty table for Related Parties
	var CRPartysize=getGridRowCount('Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS'); 
	for(var y=0;y<CRPartysize;y++)
	{
		var INTEGRATION_INPUT_PARAMSInRel = getValueFromTableCell("Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS",y,12); //INTEGRATION_INPUT_PARAMS Column
		//alert("INTEGRATION_INPUT_PARAMSInRel : "+INTEGRATION_INPUT_PARAMSInRel);
		if(INTEGRATION_INPUT_PARAMSInRel.indexOf("|#|") != -1)
		{
			var resultDataSplitted = INTEGRATION_INPUT_PARAMSInRel.split("|#|");
			for(var i=0;i<resultDataSplitted.length;i++)
			{
				var innerData = resultDataSplitted[i].split("~#~");
				for(var j=0;j<innerData.length;j++)
				{
					for (var k=0;k<RelColumnNames.length;k++)
					{							
						if(innerData[0].indexOf(RelColumnNames[k]) != -1)
						{
							if(myTrim(getValueFromTableCell("Q_USR_0_IRBL_CONDUCT_REL_PARTY_GRID_DTLS",y,RelColumnIndexes[k])) != myTrim(innerData[1]))
							{
								ReperformFlag = true;
								break;
							}
						}
					}
					if(ReperformFlag)
					break;
				}
				if(ReperformFlag)
					break;
			}			
		}
		if(ReperformFlag)
			break;
	}
					
	return ReperformFlag;
}


// bySuraj
function collectChargeValidation()
{
	var paidServices=fetchPaidService();
	var selectedServices=selectedServiceReq();
	
	var pSArray = new Set();
	var paidFlag=false;
	if(paidServices!="" && selectedServices!="")
	{
		var paidServicesArray= JSON.parse(paidServices);
		for(var i=0;i<paidServicesArray.length;i++)
		{
			pSArray.add(paidServicesArray[i][0]);
		}
		
		var selServices = selectedServices.split("|");
		for(var i=0;i<selServices.length;i++){
			if(pSArray.has(selServices[i])){
				paidFlag=true;
				break;
			}
		}
	}
		
	if((getValue("WAIVER_OF_CHARGES")=="N") &&(((getValue("SUB_SEGMENT")=='SME') || (getValue("SUB_SEGMENT")=='PSL'))) && paidFlag){
		
				
		var AccountGridrowCount = getGridRowCount("tblAccountDetails");
			
		for(var i =0;i<AccountGridrowCount;i++)
			{
				var AccountType=getValueFromTableCell("tblAccountDetails",i,2);				
				if(AccountType=='ODA' || AccountType=='SBA')
					setCellDisabled("tblAccountDetails",i,8,"false");					
			}							
		
	}
	else
	{
		var AccountGridrowCount = getGridRowCount("tblAccountDetails");
		for(var i =0;i<AccountGridrowCount;i++)
		{
			var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
			
			if(AccountType=='ODA' || AccountType=='SBA')
			{
				setTableCellData("tblAccountDetails",i,8,false,true);
			}
		}
		//setMultiColumnDisable("tblAccountDetails",8,"true",true);
		setColumnDisable("tblAccountDetails",8,true,true);
	}
}
function waiverOfChargeValidation()
{
	var paidServices=fetchPaidService();
	var selectedServices=selectedServiceReq();
	
	var pSArray = new Set();
	var paidFlag=false;
	if(paidServices!="" && selectedServices!="")
	{
		var paidServicesArray= JSON.parse(paidServices);
		for(var i=0;i<paidServicesArray.length;i++)
		{
			pSArray.add(paidServicesArray[i][0]);
		}
		
		var selServices = selectedServices.split("|");
		for(var i=0;i<selServices.length;i++){
			if(pSArray.has(selServices[i])){
				paidFlag=true;
				break;
			}
		}
	}
	
	if(((getValue("SUB_SEGMENT")=='SME') || (getValue("SUB_SEGMENT")=='PSL')) && paidFlag){
		
		setStyle("WAIVER_OF_CHARGES", "disable", "false");
			
		
	}
	else
	{
		setValue("WAIVER_OF_CHARGES", "N");
		setStyle("WAIVER_OF_CHARGES", "disable", "true");
	}
}
function fetchPaidService()
{
	var paidService=executeServerEvent("PaidServices","INTRODUCEDONE","",true);
	return paidService;
	
}

/*function eosbAddValidation()
{
	var selectedServices=selectedServiceReq();
	var statusAdd = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
	if(selectedServices!="")
	{
		if(statusAdd == "PL_Type" && selectedServices.includes("EOSB Release"))
		{
			document.getElementById('EOSB_LP_DATE').disabled = false;
			setStyle("EOSB_LP_DATE","mandatory","true");
		}
	}
}*/

function eosbRemoveValidation()
{
	var selectedServices=selectedServiceReq();
	//var selectedService=selectedServices.substring(selectedServices.lastIndexOf('|') + 1);
	var statusRemove = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
	if(statusRemove == "PL_Type" && selectedServices.indexOf("EOSB Release")!=-1)
	{
		document.getElementById('EOSB_LP_DATE').disabled = false;
		setStyle("EOSB_LP_DATE","mandatory","true");
	}else{
		document.getElementById('EOSB_LP_DATE').disabled = true;
		document.getElementById('EOSB_LP_DATE').value = "";
		setStyle("EOSB_LP_DATE","mandatory","false");
	}
}

function selectedServiceReq()
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
	var opt=[] ;
	var len=ServiceRequestSelected.options.length;
	if(len>0)
	{
		for(var i=0;i<len; i++)
		{
			opt = ServiceRequestSelected.options[i];
			ServiceRequestValue = ServiceRequestValue + opt.value + "|";
			
		}
		ServiceRequestValue=ServiceRequestValue.substring(0,ServiceRequestValue.length-1);
	}
	return ServiceRequestValue;

}

function riskScoreValidation()
{
	if(getValue("CUST_CATEGORY")!='' && getValue("IS_POLITICALLY_EXPO")!='' && getValue("CUST_CATEGORY")!='' && getGridRowCount("Q_USR_0_PC_COUNTRY_DTLS")>0 && getGridRowCount("Q_USR_0_PC_DEMOGRAPHIC_DTLS")>0 && getGridRowCount("Q_USR_0_PC_INDUSTRY_DTLS")>0 && getGridRowCount("Q_USR_0_PC_RRC_PRODUCT_DTLS")>0)
	{
		if(getValue("EMP_TYPE")=='' && getValue("CIF_TYPE")=="Individual")
		{
			showMessage("","Please fill all the fields under Risk Rating section to calculate the Risk Score","error")
			return false;
		}
		return true;
	}
	else 
	{
		showMessage("","Please fill all the fields under Risk Rating section to calculate the Risk Score","error")
		return false;
	}
}

function eosbValidation()
{
		var status = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
		if(status == "PL_Type" && getValue("SERVICE_REQUEST_SELECTED").indexOf("Employer Change – EOSB Release")!=-1)
		{
			//setStyle("EOSB_LP_DATE", "disable", "false");
			document.getElementById('EOSB_LP_DATE').disabled = false;
			document.getElementById('EOSB_LP_DATE').classList.remove('disabledBGColor');
			setStyle("EOSB_LP_DATE", "mandatory", "true");
		}else
		{
			//setStyle("EOSB_LP_DATE", "disable", "true");
			document.getElementById('EOSB_LP_DATE').disabled = true;
			document.getElementById('EOSB_LP_DATE').value = "";
			document.getElementById('EOSB_LP_DATE').classList.add('disabledBGColor');
			setStyle("EOSB_LP_DATE", "mandatory", "false");
		}
}
function addRowPostHook(controlId)
{	
    if(controlId=="Q_USR_0_PC_RELATED_PARTY_GRID_DTLS")
	{		
		//Unique no datetimestamp
		var now = new Date();

		timestamp = now.getFullYear().toString(); // 2011
		timestamp += (now.getMonth < 9 ? '0' : '') + now.getMonth().toString(); // JS months are 0-based, so +1 and pad with 0's
		timestamp += ((now.getDate < 10) ? '0' : '') + now.getDate().toString(); // pad with a 0
		timestamp += now.getHours().toString();
		timestamp += now.getMinutes().toString();
		timestamp += now.getSeconds().toString();
		
		var RPGrid_Size=getGridRowCount('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS');
		
		setTableCellData(controlId,(RPGrid_Size-1),20,timestamp,true);  // RelatedPartyId Column
	}
}
function viewCLM() {

	setValues({"viewclm_flag":"Clicked"},true);	
	//saveWorkItem();
	var process = "CLMPC";	
	
		var CIF_ID = getValue("CIF_ID");
		var CIF_TYPE = getValue("CIF_TYPE");
		var Segment ="";	
		var Operation ="FULL";
		var workflowmode="";
	
		if(ActivityName=="CLM_Maker") workflowmode="MAKER";
		if(ActivityName=="CLM_Checker") workflowmode="CHECKER";
		
		if(CIF_TYPE=="Non-Individual") Segment="BBG";
		else if(CIF_TYPE=="Individual") Segment="PBG";
		
		var param = "processName="+process+"&userId="+user+"&workItemId="+WorkitemNo+"&cifId="+CIF_ID+"&customerSegment="+Segment+"&operation="+Operation+"&workflowMode="+workflowmode+"&role="+ActivityName;		
		console.log(param);
		var encryptData = executeServerEvent("VIEW_CLM_BTN", "Click",param, true);
		//window.open("https://clm-ui-sit.rakbankonline.ae/clm?t="+encodeURI(encryptData));
		window.open(encodeURI(encryptData));

		
}
function customListViewValidationPC(controlId,flag){
	
	if(flag=='A' || flag == 'M'){
		
		if(controlId=="table44"){
			var selectedDocumentValues=getValue("table44_CLM_DOCUMENTS");
			var arrAvailableDocList = window.parent.getInterfaceData("D");	
			var bResult=true;
			for(var iDocCounter=0;iDocCounter<arrAvailableDocList.length;iDocCounter++)	{
			    if (arrAvailableDocList[iDocCounter].name.toUpperCase()==selectedDocumentValues.toUpperCase()) {
					bResult = false;
					break;
				}
			}
				if(!bResult){
				alert(selectedDocumentValues+" uploaded already,Kindly re-upload if required");
				}
		}	
	}
	
}

function getCLMstatus(){
			var data=getValue("CIF_ID");
			var clm_res = executeServerEvent("GET_CLM_DATA_STATUS", "Click",data, true);
			if(clm_res!="CLM Integration call failed"){
				if(ActivityName == 'CLM_Maker' && clm_res == 'MAKER_FULL_SUBMITTED'){
					//alert(clm_res);
					return true;
				}
				else if(ActivityName == 'CLM_Checker' && clm_res == 'CHECKER_FULL_APPROVED'){
					//alert(clm_res);
					if(getValue("qDecision")== 'CLM Activity Approved') {
						return true;
							}
					else {
						alert("CLM Activity Approved , Please change decision to CLM Activity Approved");
	
						return false;
					}
				}
				else if(ActivityName == 'CLM_Checker' && clm_res == 'CHECKER_RETURNED'){
					//alert(clm_res);
					if(getValue("qDecision") == 'Send Back to CLM Maker') {
						return true;
							}
					else {
						alert("CLM Activity not approved/CLM Activity to be edited by the Maker, Please change decision to Send Back to CLM Maker");
						
						return false;
					}
				}
				else
				{
					alert("CLM Portal Activity is not submitted or approved yet! Kindly retry after sometime.");
					return false;
				}
			}
			else{
				alert("Error while fetching CLM status! Kindly retry after sometime.");
				return false;
			}
}




