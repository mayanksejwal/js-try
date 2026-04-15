function compareStringsIgnoreCase (string1, string2) 
{
	if(string1!=null && string2!=null)
    {
     string1 = string1.toLowerCase();
     string2 = string2.toLowerCase();
     return string1 === string2;
	}
    else
    	return false;
}

function isEmpty(string1)
{
	if(string1==null || string1=='')
		return true;
	else
		return false;		
}

function disableControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"disable","true");
		}
		catch(Exception)
		{}	
	}	
}

function enableControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"disable","false");
		}
		catch(Exception)
		{}	
	}
}

function lockControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"readonly","true");
		}
		catch(Exception)
		{}	
	}
}

function unlockControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"readonly","false");
		}
		catch(Exception)
		{}	
	}
}

function showControl(controlNameList)
{	
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"visible","true");
		}
		catch(Exception)
		{}	
	}
}

function hideControl(controlNameList)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"visible","false");
		}
		catch(Exception)
		{}
	}
}

function clearControlValue(controlNameList)
{		
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		var controlName = JSON.parse('{"'+controlNameArr[idx]+'":""}');
		try
		{
			setValues(controlName,true);
		}
		catch(Exception)
		{}
	}		
}

function setControlValue(controlName, controlValue)
{
	var controlObj = JSON.parse('{"'+controlName+'":"'+controlValue+'"}');
	setValues(controlObj,true);
}

function setControlColor(controlNameList,color)
{
	var controlNameArr = controlNameList.split(",");
	for(var idx=0;idx<controlNameArr.length;idx++)
	{
		try
		{
			setStyle(controlNameArr[idx],"backcolor",color);
		}
		catch(Exception)
		{}	
	}
	
}

function formatDate(dateObj,format)
{
    var curr_date = dateObj.getDate();
    var curr_month = dateObj.getMonth();
    curr_month = curr_month + 1;
    var curr_year = dateObj.getFullYear();
    var curr_min = dateObj.getMinutes();
    var curr_hr= dateObj.getHours();
    var curr_sc= dateObj.getSeconds();
    if(curr_month.toString().length == 1)
    curr_month = '0' + curr_month;      
    if(curr_date.toString().length == 1)
    curr_date = '0' + curr_date;
    if(curr_hr.toString().length == 1)
    curr_hr = '0' + curr_hr;
    if(curr_min.toString().length == 1)
    curr_min = '0' + curr_min;
	if(curr_sc.toString().length == 1)
    curr_sc = '0' + curr_sc;
    if(format ==1)//dd-mm-yyyy
    {
        return curr_date + "-"+curr_month+ "-"+curr_year;       
    }
    else if(format ==2)//yyyy-mm-dd
    {
        return curr_year + "-"+curr_month+ "-"+curr_date;       
	}
    else if(format ==3)//dd/mm/yyyy
    {
        return curr_date + "/"+curr_month+ "/"+curr_year;       
    }
    else if(format ==4)// MM/dd/yyyy HH:mm:ss
    {
        return curr_month+"/"+curr_date +"/"+curr_year+ " "+curr_hr+":"+curr_min+":"+curr_sc;       
    }
	else if(format ==5)// dd/MM/yyyy HH:mm:ss
    {
        return curr_date+"/"+curr_month +"/"+curr_year+ " "+curr_hr+":"+curr_min+":"+curr_sc;       
    }
}
