var PC_Common = document.createElement('script');
PC_Common.src = '/PC/PC/CustomJS/PC_Common.js';
document.head.appendChild(PC_Common);

//var ActivityName=getWorkItemData("ActivityName");
function enableDisableAfterFormLoad()
{
	setStyle("HIDDEN_SECTION","visible","false");
	setControlValue("qDecision","");  //setting decision drop down values as Select initially
	enableDisableCheckListGrid();
	enableDisableOPSDocCheckListGrid();
	setStyle("REJECT_REASON_GRID","visible","false");
	//setStyle("RISK_RATING","disable","true");
	setStyle("ISCREDITAPPROVAL","disable","true");
	setStyle("NO_OF_APPROVAL_REQD","disable","true");
	setStyle("ORIGINAL_HELD","disable","true");
	setStyle("DNFBP_Status","disable","true");
	setStyle("qPriority","disable","true");
	setStyle("IS_COMPANY_COMPLEX","disable","true");
	
	if(ActivityName=='OPS_Data_Entry_Checker')
	{
		setStyle("IS_COMPANY_COMPLEX","disable","false");
	}
	if(ActivityName=='OPS_Data_Entry_Checker' || ActivityName=='OPS_Bil_Document_Checker')
	{
		setStyle("PROCESSING_TEAM","disable","false");
	}
	else
	{
		setStyle("PROCESSING_TEAM","disable","true");
	}
	if(ActivityName=='OPS_Document_Checker')
	{
		setStyle("DOCS_PROCESSING_TEAM","disable","false");
	}
	else
	{
		setStyle("DOCS_PROCESSING_TEAM","disable","true");
	}
	handleUIDgridFieldEditable();
	handleErrorHandlingFieldEditable();
	
	//Error handling tab to be visible at error handling queue
	if(ActivityName=='Error_Hand_Data_Entry_Maker' || ActivityName=='Error_Hand_Data_Entry_Checker' || ActivityName=='Error_Handling')
	{
		setTabStyle("tab3",3,"visible","true");
	}
	else
	{
		setTabStyle("tab3",3,"visible","false");
	}
	if(ActivityName!='Introduction')
	{	
		setStyle('btn_CIF_Search','disable','true');
		setStyle('btn_CIF_Clear','disable','true');
		setStyle("SERVICE_REQUEST_TYPE","disable","true");
		setStyle("APPLICATION_DATE","disable","true");
		setStyle("btn_EIDA_Reader","disable","true");
		setStyle("ACC_FREEZE_REQD","disable","true");
		setStyle("REQUEST_TYPE","disable","true");
		setStyle("Q_USR_0_PC_MEMOPAD_GRID","disable","true");
		setStyle("Q_USR_0_PC_CIF_DETAILS","disable","true");
		
		// Disbale Signature Window if ODA or SBA account types are not available.
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
			setStyle("btn_View_Signature","disable","true");
		}
		//************************************************************************
		
		try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('ServiceRequestdropdown').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('ServiceRequestdropdown').disabled=true;
		}
		/*try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('ServiceRequestSelected').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('ServiceRequestSelected').disabled=true;
		}*/
		try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('addButtonSR').disabled=true;
			window.frames['iframe2'].contentWindow.document.getElementById('addButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].contentWindow.document.getElementById('addButtonSR').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('addButtonSR').disabled=true;
			window.frames['iframe2'].document.getElementById('addButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].document.getElementById('addButtonSR').style.backgroundColor="#e4e4e4";
		}
		try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('removeButtonSR').disabled=true;
			window.frames['iframe2'].contentWindow.document.getElementById('removeButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].contentWindow.document.getElementById('removeButtonSR').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('removeButtonSR').disabled=true;
			window.frames['iframe2'].document.getElementById('removeButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].document.getElementById('removeButtonSR').style.backgroundColor="#e4e4e4";
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
		
		/*try
		{
			window.frames['iframe4'].contentWindow.document.getElementById('ProductSelected').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe4'].document.getElementById('ProductSelected').disabled=true;
		}*/
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
		
		//Disabling related CIF add remove control
		
		try
		{
			window.frames['iframe3'].contentWindow.document.getElementById('RelatedCIFdropdown').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe3'].document.getElementById('RelatedCIFdropdown').disabled=true;
		}
		
		try
		{
			window.frames['iframe3'].contentWindow.document.getElementById('RelatedCIFSelected').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe3'].document.getElementById('RelatedCIFSelected').disabled=true;
		}
		
		try
		{
			window.frames['iframe3'].contentWindow.document.getElementById('addButtonCIF').disabled=true;
			window.frames['iframe3'].contentWindow.document.getElementById('addButtonCIF').style.color="#2d2c2c7a";
			window.frames['iframe3'].contentWindow.document.getElementById('addButtonCIF').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe3'].document.getElementById('addButtonCIF').disabled=true;
			window.frames['iframe3'].document.getElementById('addButtonCIF').style.color="#2d2c2c7a";
			window.frames['iframe3'].document.getElementById('addButtonCIF').style.backgroundColor="#e4e4e4";
		}
		try
		{
			window.frames['iframe3'].contentWindow.document.getElementById('removeButtonCIF').disabled=true;
			window.frames['iframe3'].contentWindow.document.getElementById('removeButtonCIF').style.color="#2d2c2c7a";
			window.frames['iframe3'].contentWindow.document.getElementById('removeButtonCIF').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe3'].document.getElementById('removeButtonCIF').disabled=true;
			window.frames['iframe3'].document.getElementById('removeButtonCIF').style.color="#2d2c2c7a";
			window.frames['iframe3'].document.getElementById('removeButtonCIF').style.backgroundColor="#e4e4e4";
		}
	}
	
	//Condition added by Sajan for make Hold Till Date Visible/invisible CRs 21082019
	
	if(ActivityName=="Card_Maintenance_Checker" || ActivityName=="Card_Settlement_Checker" || ActivityName=="Loan_Disbursal_Checker" || ActivityName=="Mortgage_OPS_Checker" || ActivityName=="PL_Service_Checker" || ActivityName=="IOPS_Post_Disbursal_Checker" || ActivityName=="Collections_Checker" || ActivityName=="Investment_OPS_Checker" || ActivityName=="Standing_Instruction_Checker" || ActivityName=="OPS_Data_Entry_Checker" || ActivityName=="Card_Maintenance_Maker" || ActivityName=="Card_Settlement_Maker" || ActivityName=="Loan_Disbursal_Maker" || ActivityName=="Mortgage_OPS_Maker" || ActivityName=="PL_Service_Maker" || ActivityName=="IOPS_Post_Disbursal_Maker" || ActivityName=="Collections_Maker" || ActivityName=="Investment_OPS_Maker" || ActivityName=="Standing_Instruction_Maker" || ActivityName=="OPS_Data_Entry_Maker")
	{
		setStyle("HOLD_TILL_DATE_COPS","visible","true");
	}
	else
	{
		setStyle("HOLD_TILL_DATE_COPS","visible","false");
	}
	
	
	if(ActivityName=="OPS_Document_Checker" || ActivityName=="OPS_Bil_Document_Checker")
	{
		//if(getValue("ISACCOUNTFREEZE")!='No')
		//{
			//setStyle("ACC_FREEZE_REQD","disable","false");--CR:3
		//}
		//setStyle("RISK_RATING","disable","false");
		setStyle("ISCREDITAPPROVAL","disable","false");
		
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
			setStyle("Q_USR_0_PC_MEMOPAD_GRID","disable","false");
			var memoPadGridLength=getGridRowCount("Q_USR_0_PC_MEMOPAD_GRID");
			var arr=[];
			for(var j=0;j<memoPadGridLength;j++)
			{
				arr[0]=j;
				if(getValueFromTableCell("Q_USR_0_PC_MEMOPAD_GRID",j,1)==true)
				{
					setRowsDisabled("Q_USR_0_PC_MEMOPAD_GRID",arr,true);
				}else
					setCellDisabled("Q_USR_0_PC_MEMOPAD_GRID", j,2,"true");
			}
		}
	}
	
	if(ActivityName=="Credit_Manager" || ActivityName=="Credit_Analyst" || ActivityName=="Credit_Sr_Manager")
	{
		setStyle("SECURITY_CHEQUE_REQD","disable","false");
		setStyle("CREDIT_APPROVING_AUTHORITY","disable","false");
	}
	else if(ActivityName=='Introduction')
	{
		setStyle("SECURITY_CHEQUE_REQD","disable","false");
		setStyle("ACC_FREEZE_REQD","disable","true");
		setStyle("DNFBP_Status","disable","false");
		setStyle("qPriority","disable","false");
		unableDisableProductType('REQUEST_TYPE',getValue('REQUEST_TYPE'),'onChange');
	}
	else
	{
		setStyle("SECURITY_CHEQUE_REQD","disable","true");
		setStyle("CREDIT_APPROVING_AUTHORITY","disable","true");
	}
	
	if(ActivityName=='OPS_Data_Entry_Maker')
	{
		setStyle("NO_OF_APPROVAL_REQD","disable","false");
	}
	
	if(ActivityName == 'Introduction' || ActivityName == 'Initiator_Reject' || ActivityName == 'Obtain_Org_Add_Documents' || ActivityName=="OPS_Document_Checker" ) //ActivityName=="OPS_Document_Checker" AS part of CR:29
	{
		setStyle("ORIGINAL_HELD","disable","false");
	}
	if(ActivityName == 'CBWC_Maker')
	{
		//setStyle("RISK_RATING","disable","false");--CR:32
		//setStyle("ISCREDITAPPROVAL","disable","false");
	}
	//if(ActivityName == 'Control_Maker' || ActivityName == 'Control_Checker')
	if(ActivityName == 'Control_Maker')
	{
		setStyle("RISK_RATING","disable","false");
		setStyle("DNFBP_Status","disable","false");
	}
	if(ActivityName == 'Control_Checker')
	{
		setStyle("DNFBP_Status","disable","false");
	}
}
function loadServiceRequests(id,request_type,eventType)
{
	if(eventType=='load'){
			var values=executeServerEvent('ServiceRequestdropdownRequestWise',"FormLoad","",true).trim();
			//alert("values coming from server for SRs "+values);
			var serviceRequestValue=getValue('SERVICE_REQUEST_SELECTED');
			//alert(serviceRequestValue);
			if(serviceRequestValue!='' || serviceRequestValue!='' || serviceRequestValue!=null){
				//alert('coming here');
				serviceRequestValue=serviceRequestValue.split("|");
				for(var j=0;j<serviceRequestValue.length;j++)
				{
					var opt = document.createElement("option");
					opt.text = serviceRequestValue[j];
					opt.value =serviceRequestValue[j];
					opt.title =serviceRequestValue[j];
					opt.style='font-family:Arial';
					if(ActivityName!='Introduction')
						opt.disabled = true;
					if(opt.value!="")
					{
						try
						{
							window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestSelected").options.add(opt);
						}
						catch(ex)
						{
							window.frames['iframe2'].document.getElementById("ServiceRequestSelected").options.add(opt);
						}
					}
				}
			}
		values = values.split("~");
		for(var j=0;j<values.length;j++)
		{
			var opt = document.createElement("option");
			opt.text = values[j];
			opt.value =values[j];
			opt.title =values[j];
			opt.style='font-family:Arial';
			if(opt.value!="")
			{
				try
				{
					window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestdropdown").options.add(opt);
				}
				catch(ex)
				{
					window.frames['iframe2'].document.getElementById("ServiceRequestdropdown").options.add(opt);
				}
			}
		}
	
	}
	else
	{
		//var x=getValue('SERVICE_REQUEST_TYPE');
		
		var comboValue = getValue('SERVICE_REQUEST_TYPE');
			if(comboValue == ""){
				try
				{
					window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestdropdown").options.length=0;
				}
				catch(ex)
				{
					window.frames['iframe2'].document.getElementById("ServiceRequestdropdown").options.length=0;
				}
				try
				{
					window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestSelected").options.length=0;
				}
				catch(ex)
				{
					window.frames['iframe2'].document.getElementById("ServiceRequestSelected").options.length=0;
				}
			}
			
			
			else if(comboValue != ""){
	
				var ServiceRequestSelected;
				try
				{
					ServiceRequestSelected=window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestSelected");
				}
				catch(ex)
				{
					ServiceRequestSelected=window.frames['iframe2'].document.getElementById("ServiceRequestSelected");
				}
				
				ServiceRequestSelected.options.length=0;
				/*if(len>0)
				{
					//var selectRequests=document.getElementById("mySelect");
					for (var i=0; i<len; i++){
						try
						{
							window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestSelected").remove(i);
						}
						catch(ex)
						{
							window.frames['iframe2'].document.getElementById("ServiceRequestSelected").remove(i);
						}
					}
				}*/
				var values1=executeServerEvent("ServiceRequestdropdownRequestWise","FormLoad","",true).trim();
				var status = executeServerEvent("PL_TypeAccFlag","FormLoad","",true).trim();
				if(status == "PL_Type" && (values1.indexOf("Employer Change – EOSB Release")!=-1) && (getValue("EOSB_LP_DATE") == null || getValue("EOSB_LP_DATE") == ""))
				{
					setStyle("EOSB_LP_DATE", "mandatory", "true");
				}else
				{
					setStyle("EOSB_LP_DATE", "mandatory", "false");
				}
				var ServiceRequestdropDown;
				try
				{
					ServiceRequestdropDown= window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestdropdown");
				}
				catch(ex)
				{
					ServiceRequestdropDown= window.frames['iframe2'].document.getElementById("ServiceRequestdropdown");
				}
				ServiceRequestdropDown.options.length=0;
				//var selectRequests=document.getElementById("mySelect");
				/*for (var p=0; p<len; p++){
					try
					{
						window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestdropdown").remove(p);
					}
					catch(ex)
					{
						window.frames['iframe2'].document.getElementById("ServiceRequestdropdown").remove(p);
					}
				}*/
				//alert(values1);
				if(values1!="" || values1!=''){
					values1 = values1.split("~");
					for(var j=0;j<values1.length;j++)
					{
						var opt = document.createElement("option");
						opt.text = values1[j];
						opt.value =values1[j];
						opt.title =values1[j];
						opt.style='font-family:Arial';
						if(opt.value!="")
						{
							try{
								window.frames['iframe2'].contentWindow.document.getElementById("ServiceRequestdropdown").options.add(opt);
							}
							catch(ex)
							{
								window.frames['iframe2'].document.getElementById("ServiceRequestdropdown").options.add(opt);
							}
						}
					}
				}
			
			}
			
			
		
	}

}

function populateDecisionDropDown()
{
	var decisions=executeServerEvent("DecisionDropDown","FormLoad","",true).trim();
	if (ActivityName == 'Compliance' || ActivityName == 'Compliance_WC')
	{
		if(getValue("SUB_SEGMENT")=='CBD' || getValue("SUB_SEGMENT")=='PBN' || getValue("SUB_SEGMENT")=='PRS')
		{
			var x = document.getElementById("qDecision");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Additional Information Required Control Maker' || x.options[i].value=='Additional Information Required Control Checker')
				{
					x.options[i].disabled = true;
				}
			}
		}
	}
	if(ActivityName=='Credit_Analyst')
	{
		if(getValue("SUB_SEGMENT")=='CBD')
		{
			var x = document.getElementById("qDecision");
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Refer to Senior Analyst')
				{
					x.options[i].disabled = true;
				}
			}
		}
	}
	if(ActivityName=="Freeze_Acc_Checker")
	{
		var x = document.getElementById("qDecision");
		var Dec_Freeze_Acc_Checker = getValue("ACC_FREEZE_CHECK_DEC");
		
		if(Dec_Freeze_Acc_Checker == 'Submit for Account Freeze')
		{
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Submit for Account Freeze')
				{
					x.options[i].disabled = true;
				}
			}
		}
		else
		{
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Approve')
				{
					x.options[i].disabled = true;
				}
				if(x.options[i].value=='Reject To Account Freeze Maker')
				{
					x.options[i].disabled = true;
				}
			}
		}
	}
	if(ActivityName=="OPS_Document_Checker")
	{
		var x = document.getElementById("qDecision");
		var sysChecks = getValue("ISSYSTEMCHECKS");
		
		if(sysChecks != 'Required')
		{
			for (var i = 0; i < x.options.length; i++) 
			{
				if(x.options[i].value=='Perform Additional Checks')
				{
					x.options[i].disabled = true;
				}
				/* if(x.options[i].value=='Reject to CBWC Maker')
				{
					x.options[i].disabled = true;
				} */
				if(x.options[i].value=='Re-perform checks')
				{
					x.options[i].disabled = true;
				}
			}
		}
	}
	
}
function loadProducts(id)
{
	var values = executeServerEvent("ProductListdropdown","FormLoad","",true).trim();
	//alert("Product Return from server is "+values);
	values = values.split("~");
	for(var j=0;j<values.length;j++)
	{
		var opt = document.createElement("option");
		opt.text = values[j];
		opt.value =values[j];
		opt.style='font-family:Arial';
		if(opt.value!=""){
			try{
				window.frames['iframe4'].contentWindow.document.getElementById(id).options.add(opt);
			}
			catch(ex)
			{
				window.frames['iframe4'].document.getElementById(id).options.add(opt);
			}
		}
	}
	
	
	var selectedProducts=getValue('PRODUCT_HELD');
	//alert(selectedProducts);
	if(selectedProducts!='' || selectedProducts!='' || selectedProducts!=null){
		//alert('coming here for selected products');
		selectedProducts=selectedProducts.split("|");
		for(var j=0;j<selectedProducts.length;j++)
		{
			var opt = document.createElement("option");
			opt.text = selectedProducts[j];
			opt.value =selectedProducts[j];
			opt.style='font-family:Arial';
			if(ActivityName!='Introduction')
				opt.disabled = true;
			if(opt.value!="")
			{
				try
				{
					window.frames['iframe4'].contentWindow.document.getElementById("ProductSelected").options.add(opt);
				}
				catch(ex)
				{
					window.frames['iframe4'].document.getElementById("ProductSelected").options.add(opt);
				}
			}
		}
	}
}

function loadSolId()
{
	var solId = executeServerEvent("SolId","FormLoad",user,true).trim();
	setControlValue("Sol_Id",solId);
}

function loadRelatedCif()
{
	//alert("hi");
			var relatedCifValue=getValue('RELATED_CIF_SEARCH');
			//alert(relatedCifValue);
		if(relatedCifValue!='' || relatedCifValue!=""){
			//alert('coming here');
			relatedCifValue=relatedCifValue.split("|");
			for(var j=0;j<relatedCifValue.length;j++)
			{
				var opt = document.createElement("option");
				opt.text = relatedCifValue[j];
				opt.value =relatedCifValue[j];
				opt.style='font-family:Arial';
				if(opt.value!="")
				{
					try
					{
						window.frames['iframe3'].contentWindow.document.getElementById("RelatedCIFdropdown").options.add(opt);
					}
					catch(ex)
					{
						window.frames['iframe3'].document.getElementById("RelatedCIFdropdown").options.add(opt);
					}
				}
			}
		}
			
			
			
			var selectedrelatedCif=getValue('SELECTED_RELATED_CIF');
			//alert("selectedrelatedCif--"+selectedrelatedCif);
			
			if(selectedrelatedCif!='' || selectedrelatedCif!=""){
				//alert('coming here');
				selectedrelatedCif=selectedrelatedCif.split("|");
				for(var j=0;j<selectedrelatedCif.length;j++)
				{
					var opt = document.createElement("option");
					opt.text = selectedrelatedCif[j];
					opt.value =selectedrelatedCif[j];
					opt.style='font-family:Arial';
					if(opt.value!="")
					{
						try
						{
							window.frames['iframe3'].contentWindow.document.getElementById("RelatedCIFSelected").options.add(opt);
						}
						catch(ex)
						{
							window.frames['iframe3'].document.getElementById("RelatedCIFSelected").options.add(opt);
						}
					}
				}
			}
	
}
	/*var selectedProducts=getValue('PRODUCT_HELD');
	//alert(selectedProducts);
	if(selectedProducts!='' || selectedProducts!='' || selectedProducts!=null){
		//alert('coming here for selected products');
		selectedProducts=selectedProducts.split("|");
		for(var j=0;j<selectedProducts.length;j++)
		{
			var opt = document.createElement("option");
			opt.text = selectedProducts[j];
			opt.value =selectedProducts[j];
			opt.style='font-family:Arial';
			if(opt.value!="")
				window.frames['iframe4'].contentWindow.document.getElementById("ProductSelected").options.add(opt);
		}
	}
}*/

function addDataToMemoPad(id)
{
	//alert(getGridRowCount('Q_USR_0_PC_MEMOPAD_GRID'));
	
	if(getGridRowCount('Q_USR_0_PC_MEMOPAD_GRID')==0)
		var response=executeServerEvent("Q_USR_0_PC_MEMOPAD_GRID","FormLoad","",true).trim();
}
function getDuplicateWorkitems()
{
	var duplicateWIs=executeServerEvent("DuplicateWI","FormLoad","",true).trim();
	if(parseInt(duplicateWIs)>0)
	{
		//showMessage('Q_USR_0_PC_DUPLICATE_WI','Duplicates are identified for this request',"error");
		alert("Duplicates are identified for this request");
		setStyle('frmDuplicateWI','visible','true');
		setStyle('Q_USR_0_PC_DUPLICATE_WI','disable','true');
	}
	else
	{
		setStyle('frmDuplicateWI','visible','false');
	}
}
function addDataToChecklist(id)
{
		clearTable("Q_USR_0_PC_CHECKLIST_GRID",true)
		var response=executeServerEvent("Q_USR_0_PC_CHECKLIST_GRID","FormLoad","",true).trim();
		
		clearTable("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID",true)
		var response=executeServerEvent("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID","FormLoad","",true).trim();
}
function checkIfSamplingRequired()
{
	if(ActivityName=='Introduction')
	{
		if(getValue('ISSAMPLINGREQUIRED')=="" || getValue('ISSAMPLINGREQUIRED')=='')
		{
			var response=executeServerEvent("ISSAMPLINGREQUIRED","FormLoad","",true).trim();
			if(response>100)
			{
				setControlValue("ISSAMPLINGREQUIRED","No");
			}
			else
			{
				setControlValue("ISSAMPLINGREQUIRED","Yes");
			}
		}
	}
}
function loadExceptions()
{
	//setStyle('Q_USR_0_PC_EXCEPTION_HISTORY','disable','true');
	if(getGridRowCount('Q_USR_0_PC_EXCEPTION_HISTORY')==0){
		var response=executeServerEvent("Exception","FormLoad","",true).trim();
		//alert("the response data for exception is "+response);
		var canRaise;
		var canClear;
		var canView;
		var ExcRights;
		if(response=="")
			return;
		response=response.split('~');
		for(var i=0;i<response.length;i++)
		{
			setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY", i,1,"true");
			setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY", i,2,"true");
			setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY", i,3,"true");
			setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY", i,4,"true");
			setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY", i,5,"true");
			ExcRights=response[i];
			ExcRights=ExcRights.split(':');
			canRaise=ExcRights[1];
			//alert("Exception can Raise for Exception "+ExcRights[0]+" is "+canRaise);
			canClear=ExcRights[2];
			//alert("Exception can Clear for Exception "+ExcRights[0]+" is "+canClear);
			canView=ExcRights[3];
			//alert("Exception can View Rights for Exception "+ExcRights[0]+" is "+canView);
			
			var isAlreadyChecked = getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",i,0);
			setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY",i,0,"true");
			setTableCellColor("Q_USR_0_PC_EXCEPTION_HISTORY", i,0,"D7DBDD");   // Gray color
			
			if(canRaise=="Y" && (isAlreadyChecked == false || isAlreadyChecked == ""))
			{
				setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY",i,0,"false");
				setTableCellColor("Q_USR_0_PC_EXCEPTION_HISTORY", i,0,"FBFCFC");   // White color
			}
			if(canClear=="Y" && isAlreadyChecked == true)
			{
				setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY",i,0,"false");
				setTableCellColor("Q_USR_0_PC_EXCEPTION_HISTORY", i,0,"FBFCFC");   // White color
			}	
		}
	}
	else
	{
		var response=executeServerEvent("Exception","FormLoad","Rights",true).trim();
		var canRaise;
		var canClear;
		var canView;
		var ExcRights;
		try
		{
		response=response.split('~')
		{
			for(var i=0;i<response.length;i++)
			{
				ExcRights=response[i];
				ExcRights=ExcRights.split(':');
				canRaise=ExcRights[1];
				canClear=ExcRights[2];
				canView=ExcRights[3];
				
				var Exception;
				// making default disabled
				setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY",i,0,"true");
				setTableCellColor("Q_USR_0_PC_EXCEPTION_HISTORY", i,0,"D7DBDD");   // Gray color
				//************************
				
				for(var j=0;j<response.length;j++)
				{
					Exception=getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",j,1);
					var isAlreadyChecked = getValueFromTableCell("Q_USR_0_PC_EXCEPTION_HISTORY",j,0);
					//alert("Exception grid not empty "+Exception)
					if(Exception==ExcRights[0])
					{
						
						if(canRaise=="Y" && (isAlreadyChecked == false || isAlreadyChecked == ""))
						{
							setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY",j,0,"false");
							setTableCellColor("Q_USR_0_PC_EXCEPTION_HISTORY", j,0,"FBFCFC");   // White color
						}
						if(canClear=="Y" && isAlreadyChecked == true)
						{
							setCellDisabled("Q_USR_0_PC_EXCEPTION_HISTORY",j,0,"false");
							setTableCellColor("Q_USR_0_PC_EXCEPTION_HISTORY", j,0,"FBFCFC");   // White color
						}
					}
				}
			}
		}
	}
	catch(ex)
	{
		
	}
	}
}


function enableDisableSearchControls()
{
	if(getValue('MAIN_CIF_SEARCH')=='Y')
	{
		setStyle("CIF_NUMBER_SEARCH","disable","true");
		setStyle("EMIRATES_ID_SEARCH","disable","true");
		setStyle("ACCOUNT_NUMBER","disable","true");
		setStyle("CARD_NUMBER","disable","true");
		setStyle("LOAN_AGREEMENT_ID","disable","true");
		setStyle("btn_CIF_Search","disable","true");
		
	}
	else
	{
		setStyle("CIF_NUMBER_SEARCH","disable","false");
		setStyle("EMIRATES_ID_SEARCH","disable","false");
		setStyle("ACCOUNT_NUMBER","disable","false");
		setStyle("CARD_NUMBER","disable","false");
		setStyle("LOAN_AGREEMENT_ID","disable","false");
		setStyle("btn_CIF_Search","disable","false");
	}
	
	if(getValue("SELECTED_CIF_SEARCH")=='Y')
	{
		setStyle("tblAccountDetails","disable","true");
		//setStyle("Q_USR_0_PC_CIF_DETAILS","disable","true");
	}
	
	else
	{
		setStyle("tblAccountDetails","disable","false");
		//setStyle("Q_USR_0_PC_CIF_DETAILS","disable","false");
	}
}


function setMemopadSrNoDisable()
{
	var memoPadGridLength=getGridRowCount('Q_USR_0_PC_MEMOPAD_GRID');
	//alert("disabling memopad sr no");
	for(var i=0;i<memoPadGridLength;i++)
	{
		setCellDisabled("Q_USR_0_PC_MEMOPAD_GRID", i,0,"true");
		setCellDisabled("Q_USR_0_PC_MEMOPAD_GRID", i,2,"true");
	}
}
function enableDisableCheckListGrid()
{
	var checklistGridLength=getGridRowCount('Q_USR_0_PC_CHECKLIST_GRID');
	
	if(ActivityName=='OPS_Data_Entry_Maker' ||ActivityName=='Freeze_Acc_Maker')
	{
		for(var i=0;i<checklistGridLength;i++  )
		{
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,0,"true");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,1,"false");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,2,"true");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,3,"false");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,4,"true");
		}
	}
	else if(ActivityName=='OPS_Data_Entry_Checker' || ActivityName=='Freeze_Acc_Checker')
	{
		for(var i=0;i<checklistGridLength;i++)
		{
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,0,"true");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,1,"true");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,2,"false");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,3,"true");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,4,"false");
		}
	}else if(ActivityName=='OPS_Document_Checker')
	{
		for(var i=0;i<checklistGridLength;i++)
		{
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,0,"true");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,1,"false");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,2,"false");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,3,"false");
			setCellDisabled("Q_USR_0_PC_CHECKLIST_GRID", i,4,"false");
		}
	}
	else
	{
		setStyle("Q_USR_0_PC_CHECKLIST_GRID","disable","true");
	}
	
}

function enableDisableOPSDocCheckListGrid()
{
	if(ActivityName=='OPS_Document_Checker' || ActivityName=='OPS_Bil_Document_Checker' || ActivityName=='Freeze_Acc_Checker' )
	{
		var checklistGridLength=getGridRowCount('Q_USR_0_PC_OPSDOC_CHECKLIST_GRID');
		for(var i=0;i<checklistGridLength;i++)
		{
			setCellDisabled("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID", i,0,"true");
			setCellDisabled("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID", i,1,"false");
			setCellDisabled("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID", i,2,"false");
		}
	} 
	else
	{
		setStyle("Q_USR_0_PC_OPSDOC_CHECKLIST_GRID","disable","true");
	}
}

function handleErrorHandlingFieldEditableforCellChange()
{
		if(ActivityName=='Error_Hand_Data_Entry_Maker')
		{
			var getErrorTableRowCount = getGridRowCount('Q_USR_0_PC_ERR_HANDLING');
			setStyle('Q_USR_0_PC_ERR_HANDLING','disable','true');
			 
			for(var j=0;j<getErrorTableRowCount;j++)
			{
				var Status =  getValueFromTableCell("Q_USR_0_PC_ERR_HANDLING",j,3);
				if(Status=="New" || Status=="Failure" || Status=="Select" || Status=="")
				{
					
					setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,3,"false");
					
				}
				else
				{
					var currentdate = new Date();  currentdate.getDate()
					var datetime = currentdate.getDate() + "/"
					+ (currentdate.getMonth()+1) + "/" 
					+ currentdate.getFullYear() + " "  
					+ currentdate.getHours() + ":"  
					+ currentdate.getMinutes() + ":" 
					+ currentdate.getSeconds();
					setTableCellData("Q_USR_0_PC_ERR_HANDLING",j,0,datetime,true);
					
				}
			}
		
	}
}

function riskRatingSectionDisable(flag, ActivityName){
	
	setStyle("CUST_CATEGORY","disable",flag);	
	setStyle("IS_POLITICALLY_EXPO","disable",flag);
	setStyle("EMP_TYPE","disable",flag);
	setStyle("RR_CUSTOMER_NAME","disable",flag);
	
	setStyle("Q_USR_0_PC_COUNTRY_DTLS","disable",flag);
	setStyle("Q_USR_0_PC_DEMOGRAPHIC_DTLS","disable",flag);
	setStyle("Q_USR_0_PC_INDUSTRY_DTLS","disable",flag);
	setStyle("Q_USR_0_PC_RRC_PRODUCT_DTLS","disable",flag);
	
	//if(ActivityName=='CBWC_Maker' ||ActivityName=='OPS_Document_Checker')
	
	setStyle("RISK_BTN","disable",flag);
	
	//setStyle("RISK_BTN","disable", ''+ (ActivityName != 'OPS_Document_Checker' +''));
	//setStyle("RISK_BTN","disable", ''+ (ActivityName != 'CBWC_Maker' +''));
	
}
function riskRatingFieldMandate(flag, ActivityName){
	
	setStyle("CUST_CATEGORY","mandatory",flag);	
	setStyle("IS_POLITICALLY_EXPO","mandatory",flag);
	setStyle("EMP_TYPE","mandatory",flag);
	//setStyle("RISK_RATING","mandatory",flag);
	//setStyle("Q_USR_0_PC_COUNTRY_DTLS","mandatory",flag);
	//setStyle("Q_USR_0_PC_DEMOGRAPHIC_DTLS","mandatory",flag); 
	//setStyle("Q_USR_0_PC_INDUSTRY_DTLS","mandatory",flag);
	//setStyle("Q_USR_0_PC_RRC_PRODUCT_DTLS","mandatory",flag);

}
function phoneBankingLogic()
{
	var status=executeServerEvent("OnLoadloggedInUser","FORMLOAD","",true).trim();
	if(status=='Employer Change – EOSB Release')
	{
		loadServiceRequests('ServiceRequestdropdown',getValue('SERVICE_REQUEST_TYPE'),'load');
		try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('ServiceRequestdropdown').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('ServiceRequestdropdown').disabled=true;
		}
		/*try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('ServiceRequestSelected').disabled=true;
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('ServiceRequestSelected').disabled=true;
		}*/
		try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('addButtonSR').disabled=true;
			window.frames['iframe2'].contentWindow.document.getElementById('addButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].contentWindow.document.getElementById('addButtonSR').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('addButtonSR').disabled=true;
			window.frames['iframe2'].document.getElementById('addButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].document.getElementById('addButtonSR').style.backgroundColor="#e4e4e4";
		}
		try
		{
			window.frames['iframe2'].contentWindow.document.getElementById('removeButtonSR').disabled=true;
			window.frames['iframe2'].contentWindow.document.getElementById('removeButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].contentWindow.document.getElementById('removeButtonSR').style.backgroundColor="#e4e4e4";
		}
		catch(ex)
		{
			window.frames['iframe2'].document.getElementById('removeButtonSR').disabled=true;
			window.frames['iframe2'].document.getElementById('removeButtonSR').style.color="#2d2c2c7a";
			window.frames['iframe2'].document.getElementById('removeButtonSR').style.backgroundColor="#e4e4e4";
		}
	}
}
//Added on 21_07_25 for CLM
function CLMOnLoad(){
	//setStyle("qDecision", "mandatory", "true");
	//populateDecisionDropDown(getWorkItemData("ActivityName"),
	setStyle("frame2","disable","true");
	setStyle("frame16","disable","true");
	setStyle("viewclm_flag","visible","false");
}