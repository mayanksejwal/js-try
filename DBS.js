var DBS_onLoad = document.createElement('script');
DBS_onLoad.src = '/DBS/DBS/CustomJS/DBS_onLoad.js';
document.head.appendChild(DBS_onLoad);

var DBS_mandatory = document.createElement('script');
DBS_mandatory.src = '/DBS/DBS/CustomJS/DBS_MandatoryFieldValidations.js';
document.head.appendChild(DBS_mandatory);

var DBS_onSaveDone = document.createElement('script');
DBS_onSaveDone.src = '/DBS/DBS/CustomJS/DBS_onSaveDone.js';
document.head.appendChild(DBS_onSaveDone);

var DBS_EventHandler = document.createElement('script');
DBS_EventHandler.src = '/DBS/DBS/CustomJS/DBS_EventHandler.js';
document.head.appendChild(DBS_EventHandler);

function setCommonVariables() {
	Processname = getWorkItemData("ProcessName");
	ActivityName = getWorkItemData("ActivityName");
	WorkitemNo = getWorkItemData("processinstanceid");
	cabName = getWorkItemData("cabinetname");
	user = getWorkItemData("username");
	viewMode = window.parent.wiViewMode;
}
function afterFormload() {
	
	//var reqcode = getValue("SERVICE_REQ_CODE");
	setCommonVariables();

	//loadMandatory();
	var id = getValue('SERVICE_REQ_CODE');
	checksCollapsed(id + "_SECTION");
	onsectionExpandCustom(id + "_SECTION");/* --this is used for adding any validation on sections which is collapsed mode. */
	executeServerEvent("", "formload", "", true);
	disableDecision('Refer back to Maker',ActivityName);
	readOnlyValidations();

}

function addCustomEventListeners(id, event) {

	document.getElementById(id).addEventListener(event, keyUpHook);
}

function keyUpHook(event) {

	var value = event.target.value;

	value = value.replace(/[^-.@#%*(),\/&?\w\s]+/g, '');

	event.target.value = value;
}

function readOnlyValidations() {
	var readOnlyflag = (parent.document.title).indexOf("(read only)");
	if (readOnlyflag > 0) // workitem opened in ReadOnly Mode
	{
		if(getValue("SERVICE_REQ_CODE")=='DBS005'){
		setStyle("TRANSFER_CHANNEL", "disable", "true");
		setStyle("FTS_REF_NO", "disable", "true");
		setStyle("SWIFT_COPY", "disable", "true");
		setStyle("DBS005_COLLECTCHARGE", "disable", "true");
		}
	}
}

function hoverAction(s) {
	$(document).ready(function () {
		$('#infoButton').attr('title', s);
	});
}

function customValidationsBeforeSaveDone(op) {
	if (op == "S") {
		setValuesOnSection();
		return true;
	}
	else if (op == "I" || op == "D") {
		setValuesOnSection();
		if (mandatoryFieldValidation(ActivityName) == false) {
			return false;
		}

		if (ActivityName == "OPS_Review" && getValue("SERVICE_REQ_CODE") == 'DBS003') {
			if (getValue("ID_DECISION") == "Approve") {
				var date1 = getValue("BAL_CONFIRMED_ASOF")
				var date2 = getValue("DATARETRIEVEASOF")
				if (date1 != date2) {
					alert("Before proceed futher, Kindly verify the account details");
				}
			}
		}
		var confirmDoneResponse = confirm("You are about to submit the workitem. Do you wish to continue?");
		if (confirmDoneResponse == true) {
			saveWorkItem();
			if ((ActivityName == "Dispatch" && getValue("DELIVERYTYPE")=="Courier" && getValue("ID_DECISION") == "Submit") 
				|| (ActivityName == "Branch" && getValue("DELIVERYTYPE")=="Branch" &&(getValue("ID_DECISION") == "Approve" || getValue("ID_DECISION")=="Reject")) 
				|| (ActivityName == "OPS_Review_Checker" && getValue("ID_DECISION")=="Reject") || (ActivityName == "OPS_Review_Checker" && getValue("SERVICE_REQ_CODE")=="DBS005" && getValue("ID_DECISION") == "Approve"))
				{
					 if(!NotifyCall())
					 {
						 return false;
					 }
				}
			var status=communication();
			if(status!="true"){
				return false;
			}
			setCustomControlsValue(); //set custom control values based on activity for charge collection
			var status = insertIntoHistoryTable();
			if (status != 'INSERTED') {
				return false;
			}
			try {
				saveWorkItem();
			} catch (ex) {
				window.parent.WFSave();
			}
			return true;
		}
		else {
			return false;
		}
	}
	return true;
}

function pausecomp(millis) {
	var date = new Date();
	var curDate = null;
	do { curDate = new Date(); }
	while (curDate - date < millis);
}
function isDocTypeAttached(docattached) {
	var docInterface = window.parent.getInterfaceData('D');
	var docListSize = docInterface.length;
	var attachedFlag = false;
	for (var docCounter = 0; docCounter < docListSize; docCounter++) {
		var docName = docInterface[docCounter].name;
		if (docName == docattached) {
			attachedFlag = true;
			break;
		}
	}
	return attachedFlag;
}


function eventDispatched(controlObj, eventObj) {
	var controlId = controlObj.id;
	var controlEvent = eventObj.type;
	var ControlIdandEvent = controlId + '_' + controlEvent;
	
	switch (ControlIdandEvent) {
		case 'ID_DECISION_change':
			if (ActivityName == "OPS_Review"){
			
				if(getValue("SERVICE_REQ_CODE")=='DBS004'){
					if(getValue("ID_DECISION") == "Approve"){
					setStyle("NEW_DOC_ID", "mandatory", "true");
					setStyle("NEW_DOC_EXP_DATE", "mandatory", "true");
					if(getValue('DOC_TYPE')!='EMID'){
						setStyle("NEW_DOC_ISSUE_DATE", "mandatory", "true");
					}
					}	
					else if(getValue("ID_DECISION") == "" || getValue("ID_DECISION") == "Reject"){

						setStyle("NEW_DOC_ID", "mandatory", "false");
						setStyle("NEW_DOC_ISSUE_DATE", "mandatory", "false");
						setStyle("NEW_DOC_EXP_DATE", "mandatory", "false");
					}	
				}
				if(getValue("SERVICE_REQ_CODE")=='DBS003'){
					if(getValue("ID_DECISION") == "Approve") {
					setStyle("IsDataVerified", "mandatory", "true");
					}
					else if(getValue("ID_DECISION") == "" || getValue("ID_DECISION") == "Reject"){
						setStyle("IsDataVerified", "mandatory", "false");
					}
				}
				if(getValue("SERVICE_REQ_CODE")=='DBS005'){
					if(getValue("ID_DECISION") != "Approve") {
						setStyle("TRANSFER_CHANNEL", "mandatory", "false");
					}
					else{
						setStyle("TRANSFER_CHANNEL", "mandatory", "true");
					}
				}
			}
			executeServerEvent(controlId, "change", "", true);
			return
			break;

		case 'DBS003_COLLECTCHARGE_click':
			if(!accountDetailsValidation()){
				showMessage("","Request can't be processed, account details is blank","error")
			}
			else{
			var response = executeServerEvent("COLLECTCHARGE", "click", "", false);
			CreateIndicator("temp");
			setStyle('DBS003_COLLECTCHARGE', 'disable', 'true');
			}
			return
			break;
		case 'AWB_BTN_click':
			var response = executeServerEvent("AWB_GENERATION", "click", "", false);
			CreateIndicator("temp");
			setStyle('AWB_BTN', 'disable', 'true');
			return
			break;
		case 'NEW_DOC_ID_change':
			if(getValue('DOC_TYPE')=='EMID'){
				emirateIdValidation();
			}
			return
			break;
		case 'DBS005_COLLECTCHARGE_click':
			if(getValue("PREV_WS_DECISION")=='Reject'){
				showMessage("","Charge collection is not allowed as the Ops Maker has rejected the request","error");
				return
				break;
			}
			if(chargeCollectionValidation()){
				var response = executeServerEvent("COLLECTCHARGE", "click", "", false);
				CreateIndicator("temp");
				setStyle('DBS005_COLLECTCHARGE', 'disable', 'true');
			}
			return
			break;
		case 'TRANSFER_CHANNEL_change':
			if(getValue("TRANSFER_CHANNEL")=='FTS'||getValue("TRANSFER_CHANNEL")=='RMT'||getValue("TRANSFER_CHANNEL")=='AANI/IPP'){
					setValue("SWIFT_COPY","");
					setStyle("SWIFT_COPY","visible","false");
					setStyle("FTS_REF_NO","visible","true");
					setStyle("FTS_REF_NO","disable","false");
					setStyle("FTS_REF_NO","mandatory","true");
					document.getElementById('DBS005_COLLECTCHARGE').innerHTML = 'Charge Collect';
				}
			else if(getValue("TRANSFER_CHANNEL")=='Swift'){
				setValue("FTS_REF_NO","");
				setStyle("FTS_REF_NO","visible","false");
				setStyle("SWIFT_COPY","visible","true");
				setStyle("SWIFT_COPY","disable","false");
				setStyle("SWIFT_COPY","mandatory","true");
				document.getElementById('DBS005_COLLECTCHARGE').innerHTML = 'PDF Generate';
			}
				
			else{
				setValue("SWIFT_COPY","");
				setValue("FTS_REF_NO","");
				setStyle("SWIFT_COPY","visible","false");
				setStyle("FTS_REF_NO","visible","false");
			}
			return
			break;
	}
}
function appendTableHTML(table, tableHeaders) {

	var str = "<div class='table-responsive'>" +
		"	<TABLE id='" + table + "' class='modalDHTable'>" +
		"		<TR>";

	for (var idx in tableHeaders) {
		str += "<TH> " + tableHeaders[idx] + "</TH>";
	}

	str += "		</TR> ";

	str += (table == 'Q_USR_0_DBS_WIHISTORY') ? tdLogicWIHistory(table) : tdLogicFullData(table, 9);

	str += '</TABLE>';
	str += '</div>';

	return str;
	//////////
}

function tdLogicWIHistory(table) {

	var str = '';
	var str_ClrGridData = executeServerEvent("ClrGridData", "click", "", true);
	var set1 = [0, 1, 2, 3, 4, 6, 10];

	var dcsnGridSize = getGridRowCount(table);

	for (var i = 0; i < dcsnGridSize; i++) {
		str += '<TR>';

		for (var j = 0; j < 11; j++) {
			if (set1.indexOf(j) != -1) {

				if (j == 10) { // clarification column
					//var str_ClrGridData = executeServerEvent("ClrGridData","click","", true); // server side data

					var arr = str_ClrGridData.split("$~$");
					var strToAddinModal = "";
					var rowsCount = 1;
					for (var k = 0; k < arr.length; k++) {
						var ICcolumns = arr[k].split('#~#');

						var DHdecSeqNo = ICcolumns[0];
						var ICdecSeqNo = getValueFromTableCell("Q_USR_0_DBS_WIHISTORY", i, 10);
						if (DHdecSeqNo == '' || ICdecSeqNo == '' || (DHdecSeqNo != ICdecSeqNo)) continue;

						strToAddinModal += (rowsCount++) + "). ";

						for (var idx = 1; idx < ICcolumns.length; idx++) {
							var ICkey = ICcolumns[idx].substring(0, ICcolumns[idx].indexOf('-') + 2);
							var ICvalue = ICcolumns[idx].substring(ICkey.length);

							strToAddinModal += (idx == 1) ? '<u><strong>' + ICvalue + '</strong></u><br>' : '<strong>' + ICkey + '</strong>' + ICvalue + '<br>';
						}
						if (k < arr.length - 1) strToAddinModal += '<br><br>';
					}

					var cell = strToAddinModal;
					str += '<TD>' + cell + '</TD>'

				} else { // other columns
					var cell = getValueFromTableCell(table, i, j) + "";
					str += '<TD>' + cell + '</TD>'
				}
			}
		}

		str += '</TR>';
	}

	str += '</TABLE>';
	str += '</div>';

	return str;
}

function tdLogicFullData(table, colCount) {
	var str = '';

	var dcsnGridSize = getGridRowCount(table);
	for (var i = 0; i < dcsnGridSize; i++) {
		str += '<TR>';

		for (var j = 0; j < colCount; j++) {
			var cell = getValueFromTableCell(table, i, j) + "";
			str += '<TD>' + cell + '</TD>';
		}

		str += '</TR>';
	}

	return str;
}
function onsectionExpandCustom(frameId) {

	//for populating the hidden field value in section-
	var id = frameId.split('_');
	//document.getElementById(id[0]+"_CHARGEAMT").value=getValue("CHARGEAMT");
	setValue(id[0] + "_CHARGEAMT", getValue("CHARGEAMT"));
	setValue(id[0] + "_LETTERRECIPIENT", getValue("LETTERRECIPIENT"));
	setValue(id[0] + "_ADDRESS", getValue("ADDRESSLINE1") + " " + getValue("ADDRESSLINE2") + " " + getValue("EMIRATES") + " " + getValue("POBOX") + " " + getValue("COUNTRY"));

	if (frameId == 'DecisionHistory') {
		var statusDecisionHistory = executeServerEvent('PostHookloadSection', 'click', '', true);
		var arrColDecisionHistory = statusDecisionHistory.split(',');
		for (var i = 0; i < arrColDecisionHistory.length; i++) {
			setCellDisabled("Q_USR_0_DBS_WIHISTORY", arrColDecisionHistory[i], 9, "true");
		}

		var readOnlyflag = (parent.document.title).indexOf("(read only)");
		if (readOnlyflag > 0) {

			var row = getGridRowCount('Q_USR_0_DBS_WIHISTORY');
			while (--row >= 0) {
				var decision = getValueFromTableCell('Q_USR_0_DBS_WIHISTORY', row, 4);
				if ('STP_Operator' == getValueFromTableCell('Q_USR_0_DBS_WIHISTORY', row, 1) && ('Correction required at Front End' == decision || decision.indexOf("Assign To") != -1)) {
					setCellDisabled('Q_USR_0_DBS_WIHISTORY', row, 9, 'false');
				}
			}

			setStyle('ViewHistoryBtn', 'disable', 'false');
		}

	}
	if (frameId == "DBS003_SECTION") {
		if (ActivityName == "OPS_Review"){
			setStyle("IsDataVerified", "disable", "false");
			setStyle("Q_USR_0_DBS003_ACCT_DTLS", "disable", "false");
		}
		else{
			setStyle("Q_USR_0_DBS003_ACCT_DTLS", "disable", "true");
		}
		if (ActivityName == "OPS_Review_Checker"){
			setStyle("DBS003_COLLECTCHARGE", "visible", "true");
			if(getValue("DELIVERYTYPE")=="Courier"){
				setStyle("AWB_BTN", "visible", "true");
				if(getValue("IS_CHARGE_COLLECTED")!="Y"){
					setStyle("AWB_BTN", "disable", "true");
				}
				else if(getValue("AWBNO")!="" && getValue("IS_CHARGE_COLLECTED")=="Y"){
					setStyle("AWB_BTN", "disable", "true");
				}
				else{
					setStyle("AWB_BTN", "disable", "false");
				}
			}
			if(getValue("PREV_WS")=="Doc_collection"){
				setStyle("DBS003_COLLECTCHARGE", "disable", "true");
			} 
		}
	}
	if (frameId == "DBS004_SECTION") {
		if(getValue("ROUTING_FLAG")=='C'){
			setValue("OCR_STATUS",'Success');
		}
		else{
			setValue("OCR_STATUS",'Failed');
		}
		if (ActivityName == "OPS_Review"){
			setStyle("NEW_DOC_ID", "mandatory", "true");
			setStyle("NEW_DOC_EXP_DATE", "mandatory", "true");
			if(getValue('DOC_TYPE')!='EMID'){
				setStyle("NEW_DOC_ISSUE_DATE", "mandatory", "true");
			}
		}
	}
	if (frameId == "DBS005_SECTION") {
		// Update button label based on transfer channel
		var transferChannel = getValue("TRANSFER_CHANNEL");
		if(transferChannel == "Swift"){
			document.getElementById('DBS005_COLLECTCHARGE').innerHTML = 'PDF Generate';
		} 
		else if(transferChannel == "FTS"){
			document.getElementById('DBS005_COLLECTCHARGE').innerHTML = 'Charge Collect';
		}
		if(getValue("PAYMENT_TYPE")=='Inward'){
				setStyle("label6","visible","false");
				setStyle("Q_USR_0_DBS_005_ACCT_DTLS_FOR_CHARGE","visible","false");
			}
		if (ActivityName == "OPS_Review" && getValue("PAYMENT_TYPE")=='Outward' && (parent.document.title).indexOf("(read only)")<0) {
			
			setColumnDisable("Q_USR_0_DBS_005_ACCT_DTLS_FOR_CHARGE",3,false,true);
		}
	}
}

function onSectionToggleCustom(frameId) {


}

function onChangeDecision(ActivityName) {
}

function openPDFLink() {
	window.open(getValue("AWB_PDF_LINK"), '_blank');
}
function communication()
{
	var eventName="";
	var comms_status="true";
	if(ActivityName=="Dispatch"){
		if(getValue("LETTERRECIPIENT")==getValue("CUSTOMERNAME")){
			eventName=ActivityName+"~Self";
		}
		else{
			eventName=ActivityName+"~OtherEntity";
		}
	}
	else if(ActivityName=="OPS_Review_Checker"){
		if(getValue("SERVICE_REQ_CODE")=="DBS005"){

			if(getValue("ID_DECISION")=="Approve" || getValue("ID_DECISION")=="Reject"){
				if(getValue("PAYMENT_TYPE")=="Inward"){
					eventName=ActivityName+"~"+getValue("PAYMENT_TYPE");
				}
				else{
					eventName=ActivityName+"~"+getValue("PAYMENT_TYPE")+"_"+getValue("REQ_TYPE");
				}
			}
		}
		else{
			if(getValue("ID_DECISION")=="Reject")
			eventName=ActivityName+"~Reject";
		}
	}
	else if(ActivityName=="Doc_collection"){
		if(getValue("ID_DECISION")=="Approve")
			eventName=ActivityName+"~BranchCollection";
	}
	if(eventName!=""){
		comms_status=executeServerEvent("comms_trigger","introducedone",eventName,true);
	}
	return comms_status;
}
function disableDecision(decision,activityname){
	if(activityname == 'OPS_Review_Checker')
	{
		if(getValue("IS_CHARGE_COLLECTED") == "Y")
		{
			var x = document.getElementById("ID_DECISION");
			for (var i = 0; i < x.options.length; i++) 
			{				
				if(x.options[i].value==decision)				
					x.options[i].disabled = true;				
			}
			
		}
	}
}
function emirateIdValidation()
 {  
	var value=getValue('NEW_DOC_ID');
	value = myTrim(value);
	if (value != '' && value != null)
	{
		if(value.indexOf("784") === 0)
		{
			  // Check for non-digit characters
			if (!/^\d+$/.test(value)) {
				showMessage('', "Emirates ID must contain only digits", 'error');
				setFocus("NEW_DOC_ID");
				return false;
			}
			if (value.length != 15)
			{
				showMessage('',"Invalid Emirates Id length, must be 15 characters",'error');
				setFocus("NEW_DOC_ID");
				return false;
			}
        
			var dob=getValue('DATE_OF_BIRTH');
			if (dob != '' && dob != null)
			{
				var year =dob.split('/');	
				if(value.substring(3,7)!=year[2])
				{
					showMessage('',"Invalid Emirates Id format, 4,5,6,7th digit should be year of birth",'error');
					setFocus("NEW_DOC_ID");
					return false;
				}
			}
		}
		else
		{
			showMessage('',"Invalid Emirate Id, First 3 digit should be 784",'error');
			setFocus("NEW_DOC_ID");
			return false;
		}
	}
	return true;
 }
 function chargeCollectionValidation(){
	if(getValue("PAYMENT_TYPE")!="Outward"){
		return true;
	}
	var accountDetailsGridSize = getGridRowCount('Q_USR_0_DBS_005_ACCT_DTLS_FOR_CHARGE');
	var count=0;
	for (var i = 0; i < accountDetailsGridSize; i++) {
		var chargeCollect = getValueFromTableCell('Q_USR_0_DBS_005_ACCT_DTLS_FOR_CHARGE', i, 3);
		if (chargeCollect){
			count++;
		}
	}
	if(count==0){
		showMessage("","Request can't be processed, please select at least one account for charge collection","error")
		return false;
	}
	else if(count>1){
		showMessage("","Request can't be processed, please select only one account for charge collection","error")
		return false;
	}
	return true;
 }