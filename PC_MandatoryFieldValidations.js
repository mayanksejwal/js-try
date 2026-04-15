var PC_Common = document.createElement('script');
PC_Common.src = '/PC/PC/CustomJS/PC_Common.js';
document.head.appendChild(PC_Common);


function mandatoryFieldValidation()
{
	var Processname = getWorkItemData("ProcessName");
	var ActivityName =getWorkItemData("ActivityName");
	var WorkitemNo =getWorkItemData("processinstanceid");
	var cabName =getWorkItemData("cabinetname");
	var user= getWorkItemData("username");
	
	if(getValue("qDecision").indexOf("Reject")!=-1 && (
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
		var rejReason=getGridRowCount("REJECT_REASON_GRID");
		if(parseInt(rejReason)==0)
		{
			showMessage("add_REJECT_REASON_GRID",'Please provide Reject Reasons',"error");
			return false;
		}
	}
	//CLM
	if(ActivityName=="CLM_Maker" || ActivityName=="CLM_Checker"){
			return true;
	}
	
	if(ActivityName=="Compliance")
	{
		if(getValue("qDecision").indexOf("Decline")!=-1)
		{
			var rejReason=getGridRowCount("REJECT_REASON_GRID");
			if(parseInt(rejReason)==0)
			{
				showMessage("add_REJECT_REASON_GRID",'Please provide Reject Reasons',"error");
				return false;
			}
		}
	
	}
	
	if(getValue("qDecision").indexOf("Additional Information Required")!=-1)
	{
		if(getValue("REMARKS")=="")
		{
			showMessage("REMARKS",'Please enter remarks',"error");
			return false;
		}
	}
	
	if(getValue("qDecision")=="" || getValue("qDecision")=='')
	{
		showMessage('qDecision','Selecting a Decision is Mandatory to proceed further',"error");
			return false;
	}
	
	if(ActivityName=="Introduction")
	{
		if(getValue("SERVICE_REQUEST_TYPE")=="" || getValue("SERVICE_REQUEST_TYPE")=='' || getValue("SERVICE_REQUEST_TYPE")==null)
		{
			showMessage('SERVICE_REQUEST_TYPE','Please select Service Request Type',"error");
				return false;
		}
		
		if(getValue("REQUEST_TYPE")=="")
		{
			showMessage('REQUEST_TYPE','Please select Request Type as Borrowing or Non Borrowing',"error");
				return false;
		}
		
		var ProductIdentifierSelected;
		try
		{
			ProductIdentifierSelected=window.frames['iframe4'].contentWindow.document.getElementById("ProductSelected");
		}
		catch(ex)
		{
			ProductIdentifierSelected=window.frames['iframe4'].document.getElementById("ProductSelected");
		}
		var len=ProductIdentifierSelected.options.length;
		
		
		if(getValue("REQUEST_TYPE")=="Borrowing" && len==0)
		{
			showMessage('iframe4','Please select Product Type',"error");
			return false;
		}
		
		if(getValue("APPLICATION_DATE")=="" || getValue("APPLICATION_DATE")=='')
		{
			showMessage('APPLICATION_DATE','Application Date is Mandatory',"error");
			return false;
		}
		
		if(getValue("SERVICE_REQUEST_SELECTED")=="" || getValue("SERVICE_REQUEST_SELECTED")=='' || getValue("SERVICE_REQUEST_SELECTED")==null)
		{
			
			showMessage('ServiceRequestdropdown','Please select Service Request from Service Request List',"error");
			return false;
		}
		
		
		/*var status = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim(); 
		if(localStorage.flag == 'true' && status =='PL_Type' && (getValue("EOSB_LP_DATE") == null || getValue("EOSB_LP_DATE") == ""))
		{
			showMessage('EOSB_LP_DATE','Please select EOSB LP DATE as Service type is Employer Change - EOSB Release and Account type is PL.',"error");
			return false;
		}*/
		
		if(getValue("DNFBP_Status")=="" || getValue("DNFBP_Status")=='' || getValue("DNFBP_Status")==null)
		{
			showMessage('DNFBP_Status','Please select DNFBP Status',"error");
			return false;
		}
		if(getValue("qPriority")=="" || getValue("qPriority")=='' || getValue("qPriority")==null)
		{
		showMessage('qPriority','Please select Priority',"error");
		return false;
		}
		
		if(IscollectChargeAccount())
		{
			showMessage('tblAccountDetails','Please select only one account to collect the charge',"error");
			return false;
		}
		
		//
		if(getValue("ACC_FREEZE_REQD")=='Required')
		{
			var AccountGridrowCount = getGridRowCount("tblAccountDetails");
			var isODAOrSBAAccAvailable = false;
			for(var i =0;i<AccountGridrowCount;i++)
			{
				var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
				if(AccountType=='ODA' || AccountType=='SBA')
				{
					isODAOrSBAAccAvailable = true;
					break;
				}
			}
			if(isODAOrSBAAccAvailable == false)
			{
				var msgConfirm = confirm("You are about to submit the workitem with account freeze required as Yes but No active product is/are available for the selected CIF.\n Do you wish to continue?");
				if(!msgConfirm)
				return false;
			}
		}
		/*
		if(!serviceChargeCheck())
			return false;
		*/
	}
	if(getValue("MAIN_CIF_SEARCH")!="Y")
	{
		
		showMessage('btn_CIF_Search','Please Fetch Customer Details',"error");
		return false;
	}
	
	if(getValue("SELECTED_CIF_SEARCH")!="Y")
	{
		
		showMessage('Q_USR_0_PC_CIF_DETAILS','Please Select one Customer',"error");
		return false;
	}
	/*if(getValue("qDecision")=="" || getValue("qDecision")=='')
	{
		showMessage('qDecision','Selecting a Decision is Mandatory to proceed further',"error");
			return false;
	}*/
	
	if(ActivityName=="Card_Maintenance_Checker" || ActivityName=="Card_Settlement_Checker" || ActivityName=="Loan_Disbursal_Checker" || ActivityName=="Mortgage_OPS_Checker" || ActivityName=="PL_Service_Checker" || ActivityName=="IOPS_Post_Disbursal_Checker" || ActivityName=="Collections_Checker" || ActivityName=="Investment_OPS_Checker" || ActivityName=="Standing_Instruction_Checker" || ActivityName=="OPS_Data_Entry_Checker" || ActivityName=="Card_Maintenance_Maker" || ActivityName=="Card_Settlement_Maker" || ActivityName=="Loan_Disbursal_Maker" || ActivityName=="Mortgage_OPS_Maker" || ActivityName=="PL_Service_Maker" || ActivityName=="IOPS_Post_Disbursal_Maker" || ActivityName=="Collections_Maker" || ActivityName=="Investment_OPS_Maker" || ActivityName=="Standing_Instruction_Maker" || ActivityName=="OPS_Data_Entry_Maker")
	{
		if(getValue("qDecision")=="Send To COPS Hold" && getValue("HOLD_TILL_DATE_COPS")=="")
		{
			showMessage('HOLD_TILL_DATE_COPS','Please mention the date till when the case will be on Hold',"error");
			return false;
		}
	}
	
	
	if(ActivityName=='CBWC_Maker')
	{
		if (getValue("qDecision")=="Submit")
		{
			if(getValue('RISK_RATING')=="" || getValue('RISK_RATING')=='')
			{
				showMessage('RISK_RATING','Risk Rating is Mandatory',"error");
				return false;
			} 
			
			var ProductIdentifierSelected;
			try
			{
				ProductIdentifierSelected=window.frames['iframe4'].contentWindow.document.getElementById("ProductSelected");
			}
			catch(ex)
			{
				ProductIdentifierSelected=window.frames['iframe4'].document.getElementById("ProductSelected");
			}
			var len=ProductIdentifierSelected.options.length;
			
			if(getValue("CIF_TYPE") == 'Individual')
			{
				var docList = executeServerEvent("MandatoryDocListFromMaster","INTRODUCEDONE","INDIVIDUALDOCS",true)
				if (docList != '')
				{
					if(!AttachedDocType(docList))
						return false;
				}		
			}
			if(getValue("CIF_TYPE") == 'Non-Individual')
			{
				var docList = executeServerEvent("MandatoryDocListFromMaster","INTRODUCEDONE","NON_INDIVIDUALDOCS",true)
				if (docList != '')
				{
					if(!AttachedDocType(docList))
						return false;
				}		
			}
		}
		
	}
	
	if(ActivityName=='Credit_Analyst')
	{
		if(getValue("SECURITY_CHEQUE_REQD")=='' || getValue("SECURITY_CHEQUE_REQD")=="")
		{
			showMessage('SECURITY_CHEQUE_REQD','Security Check required is mandatory',"error");
			return false;
		}
	}
	if(ActivityName=='OPS_Data_Entry_Maker')
	{
				
		if(getValue("qDecision")=="Activity Completed")
		{
			if(getValue("NO_OF_APPROVAL_REQD")=='' || getValue("NO_OF_APPROVAL_REQD")=="")
			{
				showMessage('NO_OF_APPROVAL_REQD','No Of approvals Required is mandatory',"error");
				return false;
			}
			
			var checklistGridLength=getGridRowCount('Q_USR_0_PC_CHECKLIST_GRID');
			var makerOptionValue="";
			var flag='N';
			for(var j=0;j<checklistGridLength;j++)
			{
				makerOptionValue=getValueFromTableCell("Q_USR_0_PC_CHECKLIST_GRID",j,1);
				if(makerOptionValue!="")
				{
					flag='Y';
				}
			}
			if(flag=='N')
			{
				showMessage('Q_USR_0_PC_CHECKLIST_GRID','Select atleast one maker option for the Checklist items',"error");
				return false;
			}
			var len_PC_CHECKLIST_GRID = getGridRowCount("Q_USR_0_PC_CHECKLIST_GRID");
			for(var i = 0; i < len_PC_CHECKLIST_GRID; i++ ){
				var valChecklistData = getValueFromTableCell("Q_USR_0_PC_CHECKLIST_GRID", i, 0);
				var valChecklistData_Maker = getValueFromTableCell("Q_USR_0_PC_CHECKLIST_GRID", i, 1);
				if((valChecklistData == 'Conversion HCCS SOL / HACXFRSC / HCCS SOL (510)' || valChecklistData == 'FCY Rest. (CSTOPREM)' )&& (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Trade licence validation check and updation' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Trade licence validation check and updation' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && getValue('SUB_SEGMENT') == 'SME' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'LMS-Manual Advice & Receipts (charges)' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && getValue('REQUEST_TYPE') == 'Borrowing' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Charges for profile change (CGCHRG)' && getValue('SERVICE_REQUEST_TYPE') == 'Business')
				{
					if((getValue('qPriority') == 'Express' || getValue('SUB_SEGMENT') == 'SME' || getValue('SERVICE_REQUEST_SELECTED').indexOf("Company Name Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Company Status Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Activity Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Addition of Shareholder")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Deletion of Shareholder")!=-1) && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
					{
						showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
						return false;
					}
				}
				if(valChecklistData == 'PEP marking' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'KYC Review Done' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Country of resident for Signatory / shareholder' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'DNFBP' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'UBO and share percentage' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && (valChecklistData_Maker =="" || valChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
			}
		}
	}
	if(ActivityName=='OPS_Data_Entry_Checker')
	{
		if(getValue("qDecision")=="Approve")
		{
			if(getValue("ISCARDMAINTENANCE")==false && getValue("ISCARDSETTLEMENT")==false && getValue("ISLOANDISBURSAL")==false && getValue("ISMORTGAGEOPS")==false && getValue("ISPLSERVICES")==false && getValue("ISIOPSPOSTDISBURSAL")==false && getValue("ISCOLLECTIONS")==false && getValue("ISINVESTMENTOPS")==false && getValue("ISSTANDINGINSTRUCTION")==false && getValue("ISNOTREQUIRE")==false)
			{
				showMessage("ISCARDMAINTENANCE","Select atleast one team to proceed further","error");
				return false;
			}
			
			if (getValue("ISNOTREQUIRE")==true)
			{
				if(getValue("ISCARDMAINTENANCE")==true || getValue("ISCARDSETTLEMENT")==true || getValue("ISLOANDISBURSAL")==true || getValue("ISMORTGAGEOPS")==true || getValue("ISPLSERVICES")==true || getValue("ISIOPSPOSTDISBURSAL")==true || getValue("ISCOLLECTIONS")==true || getValue("ISINVESTMENTOPS")==true || getValue("ISSTANDINGINSTRUCTION")==true)
				{
					showMessage("ISCARDMAINTENANCE","Not Require option is selected, Any other team cannot be selected ","error");
					return false;
				}
			}
			
			var checklistGridLength=getGridRowCount('Q_USR_0_PC_CHECKLIST_GRID');
			var checkerOptionValue="";
			var flag='N';
			for(var j=0;j<checklistGridLength;j++)
			{
				checkerOptionValue=getValueFromTableCell("Q_USR_0_PC_CHECKLIST_GRID",j,2);
				if(checkerOptionValue!="")
				{
					flag='Y';
				}
			}
			if(flag=='N')
			{
				showMessage('Q_USR_0_PC_CHECKLIST_GRID','Select select atleast one checker option for the Checklist items',"error");
				return false;
			}
			var len_PC_CHECKLIST_GRID = getGridRowCount("Q_USR_0_PC_CHECKLIST_GRID");
			for(var i = 0; i < len_PC_CHECKLIST_GRID; i++ ){
				var valChecklistData = getValueFromTableCell("Q_USR_0_PC_CHECKLIST_GRID", i, 0);
				var valChecklistData_Checker = getValueFromTableCell("Q_USR_0_PC_CHECKLIST_GRID", i, 2);
				if((valChecklistData == 'Conversion HCCS SOL / HACXFRSC / HCCS SOL (510)' || valChecklistData == 'FCY Rest. (CSTOPREM)') && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Trade licence validation check and updation' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Trade licence validation check and updation' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && getValue('SUB_SEGMENT') == 'SME' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'LMS-Manual Advice & Receipts (charges)' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && getValue('REQUEST_TYPE') == 'Borrowing' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Charges for profile change (CGCHRG)' && getValue('SERVICE_REQUEST_TYPE') == 'Business')
				{
					if((getValue('qPriority') == 'Express' || getValue('SUB_SEGMENT') == 'SME' || getValue('SERVICE_REQUEST_SELECTED').indexOf("Company Name Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Company Status Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Activity Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Addition of Shareholder")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Deletion of Shareholder")!=-1) && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
					{
						showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
						return false;
					}
				}
				if(valChecklistData == 'PEP marking' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'KYC Review Done' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'Country of resident for Signatory / shareholder' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'DNFBP' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
				if(valChecklistData == 'UBO and share percentage' && getValue('SERVICE_REQUEST_TYPE') == 'Business' && (valChecklistData_Checker =="" || valChecklistData_Checker =='Select'))
				{
					showMessage("","Please fill Checker Option in Checklist Grid for which Checklist Description is : "+valChecklistData,"error");
					return false;
				}
			}
		}
	}
	if(ActivityName=="OPS_Document_Checker" || ActivityName=="OPS_Bil_Document_Checker")
	{
		if(getValue("qDecision")=="Approve")
		{
			if(getValue("ISCREDITAPPROVAL")=='')
			{
				showMessage('ISCREDITAPPROVAL','Please select if Credit Approval is required or not',"error");
				return false;
			}
			
			/* setControlValue("ISACCOUNTFREEZE",getValue("ACC_FREEZE_REQD"));
			var returnForAccountFreeze=executeServerEvent("isAccountFreezeEditNonEdit","INTRODUCEDONE","",true).trim();
			if(returnForAccountFreeze=='0')
			{
				if(getValue("ACC_FREEZE_REQD")!='No')
				{
					showMessage('ACC_FREEZE_REQD','Please select account freeze required as No',"error");
					return false;
				}
			}
			
			if(getValue("ACC_FREEZE_REQD")=='Required')
			{
				var AccountGridrowCount = getGridRowCount("tblAccountDetails");
				var isODAOrSBAAccAvailable = false;
				for(var i =0;i<AccountGridrowCount;i++)
				{
					var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
					if(AccountType=='ODA' || AccountType=='SBA')
					{
						isODAOrSBAAccAvailable = true;
						break;
					}
				}
				if(isODAOrSBAAccAvailable == false)
				{
					showMessage('ACC_FREEZE_REQD','Please select account freeze required as No since No active product is/are available for the selected CIF.',"error");
					return false;
				}
			} */
			
			var UIDGridCount = getGridRowCount("Q_USR_0_PC_UID_DTLS");
			var UIDflag=false;
			
			if(UIDGridCount>0)
			{
				for(var m=0;m<UIDGridCount;m++)
				{
					var UID=getValueFromTableCell("Q_USR_0_PC_UID_DTLS",m,1);
					var Name = getValueFromTableCell("Q_USR_0_PC_UID_DTLS",m,0);
					var numbers = /^[0-9]+$/; 
					/*if(!(UID.match(numbers))) 
					{
						showMessage('Q_USR_0_PC_UID_DTLS','Only Numbers are allowed in UID',"error");
						return false;
					}*/
					if(UID!="" && Name=="")
					{
						showMessage('Q_USR_0_PC_UID_DTLS','Please enter Name in UID Grid',"error");
						return false;
					}
				}
			}
			
			setControlValue("ISSYSTEMCHECKS","Completed");
	
		}
		
		if(getValue("qDecision")=="Additional Information Required Initiator")
		{
			var AccountGridrowCount = getGridRowCount("tblAccountDetails");
			var isODAOrSBAAccAvailable = false;
			for(var i =0;i<AccountGridrowCount;i++)
			{
				var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
				if(AccountType=='ODA' || AccountType=='SBA')
				{
					isODAOrSBAAccAvailable = true;
					break;
				}
			}
			if(isODAOrSBAAccAvailable == true)
			{
				var productHeld=getValue("PRODUCT_HELD");
				var checkMemoPadSelected;
				if(productHeld!="")
					checkMemoPadSelected=executeServerEvent("MANDATORY_MEMO_PAD","INTRODUCEDONE","",true);
				else
					checkMemoPadSelected=executeServerEvent("MANDATORY_MEMO_PAD","INTRODUCEDONE","NonBorrowing",true);
					
				var memoPadUserInput=checkMemoPadSelected.substring(1,checkMemoPadSelected.length-1);
				var requiredMemo="";
				if(checkMemoPadSelected!="")
				{
					checkMemoPadSelected=checkMemoPadSelected.substring(1,checkMemoPadSelected.length-1);
					checkMemoPadSelected=checkMemoPadSelected.split(',');
					for(var p=0;p<checkMemoPadSelected.length;p++)
					{
						
						if(!(getValueFromTableCell('Q_USR_0_PC_MEMOPAD_GRID',parseInt(checkMemoPadSelected[p].trim())-1,1)))
						{
							requiredMemo=requiredMemo+checkMemoPadSelected[p]+",";
						}
					}
				}
				  
				if(requiredMemo!="")
				{
					requiredMemo=requiredMemo.substring(0,requiredMemo.length-1);
					showMessage('Q_USR_0_PC_MEMOPAD_GRID',"Memo Pad Sr Number "+requiredMemo+" is/are mandatory","error");
					return false;
				}
			}	
		}
	}
	if(ActivityName=="OPS_Document_Checker")
	{
		if(getValue("qDecision")=="Approve")
		{
			var len_PC_OPSDOC_CCHECKLIST_GRID = getGridRowCount("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID");
			for(var i = 0; i < len_PC_OPSDOC_CCHECKLIST_GRID; i++ ){
				var valOPSDOCChecklistData = getValueFromTableCell("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID", i, 0);
				var valOPSDOCChecklistData_Maker = getValueFromTableCell("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID", i, 1);
				if(valOPSDOCChecklistData == 'PEP marking' && (valOPSDOCChecklistData_Maker =="" || valOPSDOCChecklistData_Maker =='Select'))
				{
					showMessage("","Please fill Maker Option in Doc Checklist Grid for which Checklist Description is : "+valOPSDOCChecklistData,"error");
					return false;
				}
			}
		}
	}
	if(ActivityName=="Initiator_Reject")
	{
		if(getValue("qDecision")=="Reperform Checks")
		{
			setControlValue("ISDECREPERFORMCHECKS","Yes");
		}
		else
		{
			setControlValue("ISDECREPERFORMCHECKS","No");
		}
		if(IscollectChargeAccount() && getValue("qDecision")=="Submit")
		{
			showMessage('tblAccountDetails','Please select only one account to collect the charge',"error");
			return false;
		}
	}
	
	// Validating Activity Verified decision at all Checkers queue.
	if(ActivityName=='OPS_Data_Entry_Checker' || ActivityName=='Card_Maintenance_Checker' || ActivityName=='Card_Settlement_Checker' || ActivityName=='Collections_Checker' || ActivityName=='Investment_OPS_Checker' || ActivityName=='IOPS_Post_Disbursal_Checker' || ActivityName=='Loan_Disbursal_Checker' || ActivityName=='Mortgage_OPS_Checker' || ActivityName=='PL_Service_Checker' || ActivityName=='Standing_Instruction_Checker')
	{
		if(getValue("qDecision")=="Approve")
		{
			if(getValue("NO_OF_APPROVAL_REQD")=='' || getValue("NO_OF_APPROVAL_REQD")=="" || getValue("NO_OF_APPROVAL_REQD")=="0")
			{
				// nothing to do
			}
			else
			{
				var noOfApproval = parseInt(getValue("NO_OF_APPROVAL_REQD"));
				var noOfActVerified = parseInt(getValue("NO_OF_ACTIVITY_VERIFIED"));
				if(noOfActVerified < noOfApproval-1)
				{
					showMessage("qDecision","Maker Activities are not complete, Approve decision cannot be taken.","error");
					return false;
				}
			}
		}
	}
	
	if(ActivityName=='OPS_Document_Checker' || ActivityName=='OPS_Bil_Document_Checker' || ActivityName=='Control_Maker' || ActivityName=='Control_Checker' || ActivityName=='Create_Credit_Case' || ActivityName=='Credit_Analyst' || ActivityName=='Credit_Manager' || ActivityName=='Credit_Sr_Manager' || ActivityName=='Credit_BB_Head' || ActivityName=='Original_Doc_Verification' || ActivityName=='OPS_Data_Entry_Maker' || ActivityName=='OPS_Data_Entry_Checker')
	{
		var makerDoneDate=getValue("MAKER_DONE_ON");
		var daysDiff=getDateDifferenceCBWCMaker(makerDoneDate);
		if(daysDiff>30 && getValue("qDecision")=="Approve")
		{
			showMessage('qDecision','Approve decision cannot be selected as its been more than 30 days since CBWC maker processed the case',"error");
			return false;
		}
	}
	
	if (ActivityName =='Obtain_Org_Add_Documents')
	{
		var UIDGridCount = getGridRowCount("Q_USR_0_PC_UID_DTLS");
		setControlValue("EXISTING_UIDCOUNT_ATOBTQUEUE",UIDGridCount);
		
		var RiskScore = getValue('RISK_RATING');
		setControlValue("EXISTING_RISKSCORE_ATOBTQUEUE",RiskScore);
		var status = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
		if((getValue("SERVICE_REQUEST_SELECTED").indexOf("Employer Change – EOSB Release")!=-1)&& status == "PL_Type")
		{
			if(!AttachedDocType("Salary Transfer Letter"))
				return false;
		}
	}
	
	//if(ActivityName=='Control_Maker' || ActivityName=='Control_Checker')	
	if(ActivityName=='Control_Maker')	
	{
		if(getValue("RISK_RATING")=='')
		{
			showMessage('RISK_RATING','Risk Rating is mandatory to enter',"error");
			return false;
		}
		autoRaiseHighRiskException();
	}
	
	//added by Nikita on 1310219 for the Change Tracker points start
	if(ActivityName=='Compliance')	
	{
		if(getValue("qDecision")=="Approve Subject to")
		{
			if(getValue("REMARKS")=="")
			{
				showMessage("REMARKS",'Please enter remarks',"error");
				return false;
			}
		}
	}
	if(ActivityName=='Control_Checker' || ActivityName=='Compliance_WC'){
		
		if(getValue("qDecision")=="Approve")
		{
			if(!uid_match_status()){
				showMessage("",'Please select match status',"error");
				return false;
			}
		}
	}
	//added by Nikita on 1310219 for the Change Tracker points end	
	
	
	if (!AttachedDocTypeCheck("Proof_Of_Address_And_Physical_Location")) {
		setControlValue("isDoc_POA_Attached","N");
	}
	else {
		setControlValue("isDoc_POA_Attached","Y");
	}
	
	if (!AttachedDocTypeCheck("Checklist_AECB_PEP_Form")) {
		setControlValue("isDoc_ChkList_AECB_PEP_Attached","N");
	}
	else {
		setControlValue("isDoc_ChkList_AECB_PEP_Attached","Y");
	}
	
	if (!AttachedDocTypeCheck("Email_HOD_Comp_Credit_ALOC_FP_Approval")) {
		setControlValue("isDoc_Email_HOD_Comp_Credit_ALOS_FP_Attached","N");
	}
	else {
		setControlValue("isDoc_Email_HOD_Comp_Credit_ALOS_FP_Attached","Y");
	}
	
	
	// this function should be called always should be at last
	if(mandatoryCheckforClearingExceptions() == false)
		return false;
	
}

function getDateDifferenceCBWCMaker(makerDoneDate)
{
	if(makerDoneDate=="")
	{
		return 0;
	}
	
	makerDoneDate = replacingMonthWithNumber(makerDoneDate);
	
	var a = makerDoneDate.split(" ");
	var d = a[0].split("/");
	var t = a[1].split(":");
	var newDate=d[1]+'/'+d[0]+'/'+d[2]+' '+t[0]+':'+t[1]+':'+t[2];
	
	makerDoneDate = new Date(newDate);
	var currentdate = new Date();
	var datetime =(currentdate.getMonth()+1)+ "/"
	+ currentdate.getDate() + "/" 
	+ currentdate.getFullYear() + " "  
	+ currentdate.getHours() + ":"  
	+ currentdate.getMinutes() + ":" 
	+ currentdate.getSeconds();
	var submitDate = new Date(datetime);
	var utc1 = Date.UTC(makerDoneDate.getFullYear(), makerDoneDate.getMonth(), makerDoneDate.getDate());
	var utc2 = Date.UTC(submitDate.getFullYear(), submitDate.getMonth(), submitDate.getDate());

	var _MS_PER_DAY = 1000 * 60 * 60 * 24;
	diffDays_CBRB=Math.floor((utc2 - utc1) / _MS_PER_DAY);
	return diffDays_CBRB;
}

function replacingMonthWithNumber(datevalue)
{
	datevalue = datevalue.split('Jan').join('01');
	datevalue = datevalue.split('Feb').join('02');
	datevalue = datevalue.split('Mar').join('03');
	datevalue = datevalue.split('Apr').join('04');
	datevalue = datevalue.split('May').join('05');
	datevalue = datevalue.split('Jun').join('06');
	datevalue = datevalue.split('Jul').join('07');
	datevalue = datevalue.split('Aug').join('08');
	datevalue = datevalue.split('Sep').join('09');
	datevalue = datevalue.split('Oct').join('10');
	datevalue = datevalue.split('Nov').join('11');
	datevalue = datevalue.split('Dec').join('12');
	return datevalue;
}

function AttachedDocType(sDocTypeNames)
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
		alert("Please attach '" + arrSearchDocList[iSearchCounter]+"' Doc to proceed further.");
		return false;
		}
	}            
	return true;
}

function mandatoryCheckforClearingExceptions()
{
	var ActivityName =getWorkItemData("ActivityName");
	if (getValue("qDecision")=="Approve" || getValue("qDecision")=="Approve Subject to")
	{
		var exceptionGridCount=getGridRowCount("Q_USR_0_PC_EXCEPTION_HISTORY");
		var exceptionName;
		for(var i=0;i<exceptionGridCount;i++)
		{
			exceptionName=getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,1);
			if(ActivityName == 'Control_Maker' || ActivityName == 'Control_Checker')
			{
				if(exceptionName=="UID Match" || exceptionName=="High Risk Score" || exceptionName=="Blacklist" || exceptionName=="UBO Approval")
				{
					if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==true)
					{
						alert("Cannot take "+getValue("qDecision")+" decision as "+exceptionName+" exception is raised. Kindly take exception/compliance approval required decision.");
						return false;
					}
				}
			}
			
			if(ActivityName == 'Compliance_WC' || ActivityName == 'Compliance_Manager')
			{
				if(exceptionName=="UID Match" || exceptionName=="Blacklist" || exceptionName=="UBO Approval")
				{
					if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==true)
					{
						alert("Kindly clear "+exceptionName+" exception to proceed further.");
						return false;
					}
				}
			}
			
			if(ActivityName == 'Compliance' || ActivityName == 'Compliance_Manager')
			{
				if(exceptionName=="UID Match" || exceptionName=="High Risk Score" || exceptionName=="Blacklist" || exceptionName=="UBO Approval")
				{
					if(getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0)==true)
					{
						alert("Kindly clear "+exceptionName+" exception to proceed further.");
						return false;
					}
				}
			}
		}
	}
}

function AttachedDocTypeCheck(sDocTypeNames)
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

function serviceChargeCheck()
{
	if(((getValue('SUB_SEGMENT') == 'SME' || getValue('SUB_SEGMENT') == 'PSL') && getValue('qPriority') == 'Express') ||(getValue('SERVICE_REQUEST_SELECTED').indexOf("Company Name Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Company Status Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Activity Change")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Addition of Shareholder")!=-1 || getValue('SERVICE_REQUEST_SELECTED').indexOf("Deletion of Shareholder")!=-1))
	{
		var AccountGridrowCount = getGridRowCount("tblAccountDetails");
		var isODAOrSBAAccAvailable = false;
		for(var i =0;i<AccountGridrowCount;i++)
		{
			var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
			if(AccountType=='ODA' || AccountType=='SBA')
			{
				setCellDisabled("tblAccountDetails",i,8,"false");
				isODAOrSBAAccAvailable = true;
			}
		}
		if(isODAOrSBAAccAvailable == false)
		{
			setStyle('combo37','disable','true');
			showMessage('','No active product is/are available for the selected CIF, hence service charge can not be applicable',"error");
			//pausecomp(5000);
			return true;
		}else
		{
			setStyle('combo37','disable','false');
		}
		return isODAOrSBAAccAvailable;
	}
	else
	{
		var AccountGridrowCount = getGridRowCount("tblAccountDetails");
		var isODAOrSBAAccAvailable = false;
		for(var i =0;i<AccountGridrowCount;i++)
		{
			var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
			if(AccountType=='ODA' || AccountType=='SBA')
			{
				
				setCellDisabled("tblAccountDetails",i,8,"true");
				isODAOrSBAAccAvailable = true;
			}
		}
	}
}

function pausecomp(millis)
{
    var date = new Date();
    var curDate = null;
    do { curDate = new Date(); }
    while(curDate-date < millis);
}

function IscollectChargeAccount()
{
	var count=0;
	var AccountGridrowCount = getGridRowCount("tblAccountDetails");
	for(var i =0;i<AccountGridrowCount;i++)
	{
		if(getValueFromTableCell("tblAccountDetails",i,8))
		{
			count++;
		}
		if(count>1)
			return true;
	}
	return false;
}
function collectChargeAccCount()
{
	var count=0;
	var AccountGridrowCount = getGridRowCount("tblAccountDetails");
	for(var i =0;i<AccountGridrowCount;i++)
	{
		if(getValueFromTableCell("tblAccountDetails",i,8))
		{
			count++;
		}
		
	}
	if(count==1)
	{
		return true;
	}	
	return false;
}	


function uid_match_status(){
	
	var getUIDTablerowCount = getGridRowCount('Q_USR_0_PC_UID_DTLS');
	if(getUIDTablerowCount == 0){
		return true;
	}
	for(var j=0;j<getUIDTablerowCount;j++)
	{
		var matchStatus = getValueFromTableCell("Q_USR_0_PC_UID_DTLS",j,3);
			if(!matchStatus  || matchStatus.trim() ===""){
				return false;
			}
	}
	return true;
}