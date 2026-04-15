function handleException(rowIndex,colIndex,ref,controlId)
{
	if(ref.id.indexOf('checkbox')!=-1)
	{
		var exceptionRaise=executeServerEvent("raiseClearException","formload",rowIndex,true).trim();
		loadExceptions();
		
	}
}


function addRowPostHook(tableId)
{
	if(tableId=="Q_USR_0_PC_UID_DTLS")
	{
		handleUIDgridFieldEditable();
	}	
}


function handleUIDgridFieldEditable()
{
	var getUIDTablerowCount = getGridRowCount('Q_USR_0_PC_UID_DTLS');
	if(ActivityName=='OPS_Document_Checker' || ActivityName=='OPS_Bil_Document_Checker')
	{	
		setStyle("Q_USR_0_PC_UID_DTLS","disable","false");
		for(var j=0;j<getUIDTablerowCount;j++)
		{
			//setCellDisabled("Q_USR_0_PC_UID_DTLS",j,0,"false");
			//setCellDisabled("Q_USR_0_PC_UID_DTLS",j,1,"false");
			setCellDisabled("Q_USR_0_PC_UID_DTLS",j,2,"true");
		}
	}
	else if(ActivityName=='Control_Maker' || ActivityName=='Control_Checker' || ActivityName=='Compliance' || ActivityName=='Compliance_WC' || ActivityName=='Compliance_Manager')
	{
		setStyle("Q_USR_0_PC_UID_DTLS","disable","true");
		for(var j=0;j<getUIDTablerowCount;j++)
		{
			//setCellDisabled("Q_USR_0_PC_UID_DTLS",j,0,"true");
			//setCellDisabled("Q_USR_0_PC_UID_DTLS",j,1,"true");
			setCellDisabled("Q_USR_0_PC_UID_DTLS",j,2,"false");
			if(ActivityName=='Control_Maker' || ActivityName=='Control_Checker' || ActivityName=='Compliance_WC'){
				
                             setCellDisabled("Q_USR_0_PC_UID_DTLS",j,3,"false");
                          }
		}
		//Below Line Added for Save Remarks Issue
		document.getElementById("Q_USR_0_PC_UID_DTLS").removeAttribute("disabled");
	}
	
	else
	{
		setStyle("Q_USR_0_PC_UID_DTLS","disable","true");
	}
	
	if(ActivityName=='Control_Maker' || ActivityName=='Control_Checker' || ActivityName=='Compliance_WC')
	{
		setStyle("Q_USR_0_PC_UID_DTLS","disable","true");
		for(var j=0;j<getUIDTablerowCount;j++)
		{		
			setCellDisabled("Q_USR_0_PC_UID_DTLS",j,3,"false");
		}
	}
}

function handleErrorHandlingFieldEditable()
{
	var getErrorTableRowCount = getGridRowCount('Q_USR_0_PC_ERR_HANDLING');
	if(ActivityName=='Error_Hand_Data_Entry_Maker')
	{
		for(var j=0;j<getErrorTableRowCount;j++)
		{
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,0,"true");
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,1,"true");
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,2,"true");
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,4,"true");
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,5,"true");
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,6,"true");
			setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,7,"true");
			
			var Status =  getValueFromTableCell("Q_USR_0_PC_ERR_HANDLING",j,3);
			if(Status=="New" || Status=="Failure" || Status=="Select" || Status=="")
			{
				setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,3,"false");
			}
			else
			{
				setCellDisabled("Q_USR_0_PC_ERR_HANDLING",j,3,"true");
			}
		}
	} 
	else
	{
		setStyle('Q_USR_0_PC_ERR_HANDLING','disable','true');
	}
}

function validateInputtedMemoPad(rowIndex,colIndex,ref,controlId)
{
	if(colIndex == 2)
	{
		//var exceptionRaise=executeServerEvent("raiseClearException","formload",rowIndex,true).trim();
	}
	if(colIndex == 1)
	{
		if(getValueFromTableCell("Q_USR_0_PC_MEMOPAD_GRID",rowIndex,1))
		{
			setCellDisabled("Q_USR_0_PC_MEMOPAD_GRID", rowIndex,2,"false");
		}
		else
		{
			setCellDisabled("Q_USR_0_PC_MEMOPAD_GRID", rowIndex,2,"true");
			if (getValueFromTableCell("Q_USR_0_PC_MEMOPAD_GRID",rowIndex,3) != '')
				setTableCellData("Q_USR_0_PC_MEMOPAD_GRID",rowIndex,2, getValueFromTableCell("Q_USR_0_PC_MEMOPAD_GRID",rowIndex,3) ,true);
		}
	}
}


function relatedPartyValidations(id){
	
	if(getValue(id) == 'Non-Individual'){
		
		setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY'],'false');
		
		setVisible([ 'Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_MIDDLE_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_EMIRATES_ID','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_PASSPORT_NO','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_VISA_NO'], 'false');
		
		setVisible(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO'], 'true');
		
		setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO'],'true');
		
		clearData('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_MIDDLE_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_EMIRATES_ID','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_PASSPORT_NO','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_VISA_NO');
		
	} else if(getValue(id) == 'Individual'){
		
		setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO'],'false');
		
		setVisible(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO'], 'false');
		
		setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY'],'true');
		
		setVisible([ 'Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_MIDDLE_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_EMIRATES_ID','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_PASSPORT_NO','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_VISA_NO'], 'true');
		
		clearData('Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO');
		
	} else if(getValue(id) == ''){
		
		setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO'],'false');
		
		setVisible(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COMPANY_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DATE_INCORP','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_TL_NO'], 'false');
		
		setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY'],'false');
		
		setVisible([ 'Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_FIRST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_MIDDLE_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_LAST_NAME','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_GENDER','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_COUNTRY_RESIDENCE','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_DOB','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_NATIONALITY','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_EMIRATES_ID','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_PASSPORT_NO','Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_VISA_NO'], 'false');
	}
	
	setMandatory(['Q_USR_0_PC_RELATED_PARTY_GRID_DTLS_PASSPORT_NO'],'true');
	
}

function setVisible(arr, flag){
	for(var i=0; i<arr.length; i++){
		setStyle(arr[i],'visible', flag);
	}
}

function setMandatory(arr, flag){
	for(var i=0; i<arr.length; i++){
		setStyle(arr[i],'mandatory', flag);
	}
} 

function clearData(arr){
	for(var i=0; i<arr.length; i++){
		setValue(arr[i],'');
	}
} 
function balFetchEnquiry()
{
	var AccountGridrowCount = getGridRowCount("tblAccountDetails");
	for(var i =0;i<AccountGridrowCount;i++)
	{
		if(getValueFromTableCell("tblAccountDetails",i,8))
		{
			var accNo=getValueFromTableCell("tblAccountDetails",i,0);
			var data=accNo+"~"+i+"~7";
			break;
		}
	}
	var status=executeServerEvent("BAL_FETCH_BTN","click",data,true).trim();
	if(status=="FETCH ACCOUNT BALANCE SUCCESS"){
		showMessage("",'Balance refreshed successfully','error');
		saveWorkItem();
	}
	else
		showMessage("",'Some error occured in Balance refresh','error');
}
