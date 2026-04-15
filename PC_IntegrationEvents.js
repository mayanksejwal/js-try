function mainCIFSearchClickHandling(controlId)
{
	var searchControlNameList = "CIF_NUMBER_SEARCH, EMIRATES_ID_SEARCH, ACCOUNT_NUMBER, CARD_NUMBER, LOAN_AGREEMENT_ID";
	var searchControlNameListArr = searchControlNameList.split(",");
	
	var isAtLeastOneFieldFilled = false;
	for(var idx=0;idx<searchControlNameListArr.length;idx++)
	{
		var searchControlValue = getValue(searchControlNameListArr[idx].trim()).trim();
		
		if(searchControlValue!="")
		{
			isAtLeastOneFieldFilled = true;			
		}
		
		if((ComponentValidatedMap[searchControlNameListArr[idx].trim()] != undefined))
		{	
			if(ComponentValidatedMap[searchControlNameListArr[idx].trim()] == false)
			{
				showMessage("CIF_NUMBER_SEARCH",getIformCustomMsg(searchControlNameListArr[idx].trim(),"CUSTOMPATTERN_MSG"),"error");
				return false;
			}
		}
	}
	
	if(isAtLeastOneFieldFilled == false)
	{
		showMessage(controlId,"Please enter an valid Emirates ID/Loan Agreement Id/Card Number/CIF Number/ A/c No.","error");
		return false;
	}
	
	
	var response = executeServerEvent("btn_CIF_Search","click","",true);
	response = response.split("~");
	if(response[0]=="0000")
	{
		showMessage(controlId,"Successful in getting Entity Details","error");
		setStyle("CIF_NUMBER_SEARCH","disable","true");
		setStyle("EMIRATES_ID_SEARCH","disable","true");
		setStyle("ACCOUNT_NUMBER","disable","true");
		setStyle("CARD_NUMBER","disable","true");
		setStyle("LOAN_AGREEMENT_ID","disable","true");
		setStyle("btn_CIF_Search","disable","true");
		saveWorkItem();
	}
	else if(response[0].indexOf("Error")!=-1)
	{
		showMessage(controlId,"Problem in fetching entity detail","error");		
	}
	
	else if(response[0].indexOf("0000")==-1)
	{
		showMessage(controlId,"Unable to fetch details: "+response[1],"error");			
	}
}


function clearBtnClickHandling()
{
	executeServerEvent("btn_CIF_Clear","click","",true).trim();
	setStyle("CIF_NUMBER_SEARCH","disable","false");
	setStyle("EMIRATES_ID_SEARCH","disable","false");
	setStyle("ACCOUNT_NUMBER","disable","false");
	setStyle("CARD_NUMBER","disable","false");
	setStyle("LOAN_AGREEMENT_ID","disable","false");
	setStyle("btn_CIF_Search","disable","false");
	clearTable("Q_USR_0_PC_DUPLICATE_WI",true);
	//call function for clearing the Related Cif table
	clearRelatedCIfTable();
	saveWorkItem();
}

function clearRelatedCIfTable()
{
	//alert("inside abc 123");
	var mySelect;
	try
	{
		mySelect = window.frames['iframe3'].contentWindow.document.getElementById("RelatedCIFdropdown");
	}
	catch(ex)
	{
		mySelect = window.frames['iframe3'].document.getElementById("RelatedCIFdropdown");
	}
	/*for (var i=0; i<mySelect.length; i++){
	  if (mySelect.options[i].value != '' )
		 mySelect.remove(i);
	  }*/
	   mySelect.options.length = 0;
	 var mySelect1;
	 try
	 {
		mySelect1 = window.frames['iframe3'].contentWindow.document.getElementById("RelatedCIFSelected");  
	 }
	 catch(ex)
	 {
		 mySelect1 = window.frames['iframe3'].document.getElementById("RelatedCIFSelected");  
	 }
	 mySelect1.options.length = 0;
	   
}


var signaturePopupWindow;
function getSignature() 
{
	//var AccountnoSig="0003263401101@0003263401001@0003263401124";
	var AccountGridrowCount = getGridRowCount("tblAccountDetails");
	
	var totalaccno="";
	for(var i =0;i<AccountGridrowCount;i++)
	{
		var Account_no=getValueFromTableCell("tblAccountDetails",i,0);
		var AccountType=getValueFromTableCell("tblAccountDetails",i,2);
		
		if(AccountType=='ODA' || AccountType=='SBA')
		{
			if(totalaccno=="")
			{
				totalaccno	=Account_no;
			}
			else
				totalaccno = totalaccno+"@"+Account_no;
		}
	}
	
	//var ActivityName =getWorkItemData("ActivityName");
	//var sOptions = 'width=950px;height=650px; dialogLeft:450px; dialogTop:100px; center:yes;edge:raised; help:no; resizable:no; scroll:yes;scrollbar:yes; status:no; statusbar:no; toolbar:no; menubar:no; addressbar:yes; titlebar:no;';
	/*
	
	//fine
	//var sOptions = 'dialogWidth:450px; dialogHeight:450px; dialogLeft:450px; dialogTop:100px; center:yes;edge:raised; help:no; resizable:no; scroll:yes;scrollbar:yes; status:no; statusbar:no; toolbar:no; menubar:no; addressbar:yes; titlebar:no;';
	
	//perfectly fine
	//var sOptions = 'width=950px;height=650px; dialogLeft:450px; dialogTop:100px; center:yes;edge:raised; help:no; resizable:no; scroll:yes;scrollbar:yes; status:no; statusbar:no; toolbar:no; menubar:no; addressbar:yes; titlebar:no;';
	
	*/
	var sOptions = 'left=300,top=200,width=850,height=650,scrollbars=1,resizable=1; center:yes;edge:raised; help:no; resizable:no; scroll:yes;scrollbar:yes; status:no; statusbar:no; toolbar:no; menubar:no; addressbar:no; titlebar:no;';
	
	var url = "/PC/PC/CustomJSP/OpenImage.jsp?acc_num_new="+totalaccno;
	signaturePopupWindow = window.open(url, "Popup", sOptions);
}

function onloadclickMe(element) {
	//alert("inside onloadclickMe");
	var debtAccNum = element;
	executeServerEvent("btn_View_Signature","click","",true);
	//document.getElementById('if').src="/PC/PC/CustomJSP/loadImages.jsp?debtAccNum="+debtAccNum;
}

function clickMe(element) 
{
	var debtAccNum = element.innerHTML;
	var returnedsig =executeServerEvent("btn_View_Signature","click",debtAccNum,true);
	var splitstring = returnedsig.split("~");
	var returnCode = splitstring[0];
	if(returnCode == "0000")
	{
		var totalsig = splitstring[1];
		var remarks = splitstring[2];
		var siggroupname = splitstring[3];
		var custname = splitstring[4];
		signaturePopupWindow.document.getElementById('if').src="/PC/PC/CustomJSP/loadImages.jsp?debtAccNum="+debtAccNum+"&returnedsig="+totalsig+"&remarks="+remarks+"&siggroupname="+siggroupname+"&custname="+custname;
	}
	else
	{
		alert("Unable to fetch Signature of this Account: "+debtAccNum+", ReturnCode: "+splitstring[0]+", ReturnDesc: "+splitstring[1]);
	}
}


function handleonChangecif(rowIndex,colIndex,ref,controlId)
{	
	var ref_id  = ref.id;
	//alert("refid in handleonchangeCIF is "+ref_id);
	//alert("The row index is "+rowIndex);
	//alert("The column index is "+colIndex);
	var value_checkbox = document.getElementById(ref_id).value;
	
	if(ref_id.indexOf('checkbox')!=-1)
	{
		if(value_checkbox=="true")
		{
			//alert("getting id of selected checkbox");
			
			var cifGridSize=getGridRowCount("Q_USR_0_PC_CIF_DETAILS");
			var count=0;
			for(var p=0;p<cifGridSize;p++)
			{
				if(rowIndex==p)
					continue;
				else
				{
					if(getValueFromTableCell("Q_USR_0_PC_CIF_DETAILS",p,0)==true)
					{
						setTableCellData("Q_USR_0_PC_CIF_DETAILS",p,0,false,true);
					}
				}
			}
			var selectedcifid = getValueFromTableCell("Q_USR_0_PC_CIF_DETAILS",rowIndex,colIndex+1);
			var response = executeServerEvent("tblCIF", "click", selectedcifid, false);	
			setColumnDisable("Q_USR_0_PC_CIF_DETAILS",0,true,true);
			CreateIndicator("temp"); 
			/* var cifids = response.split("@")[0];
			var returncode_entitycall = response.split("@")[1];
			var returncode_account = response.split("@")[2];
			var returncode_customerSummary = response.split("@")[2];
			var returncode_RPentity = response.split("@")[2];
			if(cifids!='' || cifids!=null)
			{

				cifids=cifids.split(",");
				for(var j=0;j<cifids.length;j++)
				{
					var opt = document.createElement("option");
					opt.text = cifids[j];
					opt.value =cifids[j];
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
				
			// Error Code CINF362 from Account Summary will be treated as success i.e. no accounts are available on selected CIF	
			if(returncode_entitycall=="0000" && (returncode_account=="0000" || returncode_account=="CINF362"))
			{
				showMessage(controlId,"Successful in getting entity and account details on click of checkbox","error");
				//disablerelatedcif();
				//setStyle("Q_USR_0_PC_CIF_DETAILS","disable","true");
				setStyle("tblAccountDetails","disable","true");
				saveWorkItem();
			}
			else if(returncode_entitycall!="0000" || returncode_account!="0000")
			{
				showMessage(controlId,"Problem in getting entity and account details on click of checkbox","error");
				setControlValue(ref_id,false);
				saveWorkItem();
			}
			else if(returncode_customerSummary!="Success" || returncode_customerSummary!="")
			{
				showMessage(controlId,"Problem in getting related CIFs for Company","error");
				setControlValue(ref_id,false);
				saveWorkItem();
			}
			else if(returncode_RPentity!="Success")
			{
				showMessage(controlId,"Problem in getting entity details for related CIFs","error");
				setControlValue(ref_id,false);
				saveWorkItem();
			} */
		}
	}
}
function setDateInErrorGrid(rowIndex,colIndex,ref,controlId)
{
	var Status =  getValueFromTableCell("Q_USR_0_PC_ERR_HANDLING",rowIndex,colIndex);
	//alert("Status in error grid is "+Status);
	var currentdate = new Date();  currentdate.getDate()
					var datetime = currentdate.getDate() + "/"
					+ (currentdate.getMonth()+1) + "/" 
					+ currentdate.getFullYear() + " "  
					+ currentdate.getHours() + ":"  
					+ currentdate.getMinutes() + ":" 
					+ currentdate.getSeconds();
	/*if(Status=="")
	{
		showMessage("Q_USR_0_PC_ERR_HANDLING","Status cannot be blank","error");
		setTableCellData("Q_USR_0_PC_ERR_HANDLING",rowIndex,colIndex,"New",true);
	}*/
	if(Status=="Success")
	{
		showMessage("Q_USR_0_PC_ERR_HANDLING","Status cannot be selected as Success","error");
		setTableCellData("Q_USR_0_PC_ERR_HANDLING",rowIndex,colIndex,"Select",true);
		return false;
	}
	if(Status=="Manually Updated")
	{
		setTableCellData("Q_USR_0_PC_ERR_HANDLING",rowIndex,0,datetime,true);
	}
	else{
		setTableCellData("Q_USR_0_PC_ERR_HANDLING",rowIndex,0,"",true);
	}
}

function postServerEventHandler(controlName,EventType,response)
{
	RemoveIndicator("temp");    //Loading Indication will end here
	if(controlName=="tblCIF" && EventType=="click")
	{	
		setColumnDisable("Q_USR_0_PC_CIF_DETAILS",0,false,true);
		unableDisableProductType('REQUEST_TYPE',getValue('REQUEST_TYPE'),'onChange');
		if(getValue("CIF_TYPE")=="Non-Individual")
		{
			setValue("EMP_TYPE","");
			setStyle("EMP_TYPE","disable","true");
		}
		else
		{
			setStyle("EMP_TYPE","disable","false");
		}
		var cifids = response.split("@")[0];
		var returncode_entitycall = response.split("@")[1];
		var returncode_account = response.split("@")[2];
		var returncode_accountFlexCube = response.split("@")[3];
		var returncode_customerSummary = response.split("@")[4];
		var returncode_RPentity = response.split("@")[5];
		if(cifids!='' || cifids!=null)
		{

			cifids=cifids.split(",");
			for(var j=0;j<cifids.length;j++)
			{
				var opt = document.createElement("option");
				opt.text = cifids[j];
				opt.value =cifids[j];
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
			
		// Error Code CINF362 from Account Summary will be treated as success i.e. no accounts are available on selected CIF	
		if(returncode_entitycall=="0000" && (returncode_account=="0000" || returncode_account=="CINF362") &&(returncode_customerSummary=="Success" || returncode_customerSummary=="") && (returncode_RPentity=="Success"))
		{
			showMessage(controlName,"Successful in getting entity and account details on click of checkbox","error");
			//disablerelatedcif();
			//setStyle("Q_USR_0_PC_CIF_DETAILS","disable","true");
			setStyle("tblAccountDetails","disable","true");
			collectChargeValidation();
			eosbValidation();
			saveWorkItem();
		}
		else if(returncode_entitycall!="0000" || returncode_account!="0000")
		{
			showMessage(controlName,"Problem in getting entity and account details on click of checkbox","error");
			//setControlValue(ref_id,false);
			saveWorkItem();
		}
		else if(returncode_customerSummary!="Success" || returncode_customerSummary!="")
		{
			showMessage(controlName,"Problem in getting related CIFs for Company","error");
			//setControlValue(ref_id,false);
			saveWorkItem();
		}
		else if(returncode_RPentity!="Success")
		{
			showMessage(controlName,"Problem in getting entity details for related CIFs","error");
			//setControlValue(ref_id,false);
			saveWorkItem();
		}

		try {
			window.parent.WFSave();
		} catch(ex) {
			
		}
	}	
}