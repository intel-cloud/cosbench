<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="refresh" content="60">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <script type="text/javascript">
	function checkAll(event, str) {
		var e = window.event ? window.event.srcElement : event.currentTarget;
		var a = document.getElementsByName(str);
		for ( var i = 0; i < a.length; i++)
			a[i].checked = e.checked;
	}
	function checkItem(event, allId) {
		var e = window.event ? window.event.srcElement : event.currentTarget;
		var all = document.getElementById(allId);
		check(e, all);
	}
	function checkMe(id, allId) {
		document.getElementById('checkbox-' + id).checked = !document
				.getElementById('checkbox-' + id).checked;
		var all = document.getElementById(allId);
		check(document.getElementById('checkbox-' + id), all);
	}
	function check(item, all) {
		if (item.checked) {
			var a = document.getElementsByName(item.name);
			all.checked = true;
			for ( var i = 0; i < a.length; i++) {
				if (!a[i].checked) {
					all.checked = false;
					break;
				}
			}
		} else
			all.checked = false;
	}
	function findChecked(name) {
		var a = document.getElementsByName(name);
		var value = '';
		for ( var i = 0; i < a.length; i++) {
			if (a[i].checked) {
				value += a[i].value;
				value += "_";
			}
		}
		return value.substring(0, value.length - 1);
	}
	function changeOrder(id, value) {
		var neighid = findChecked('ActiveWorkload');
		if (neighid.indexOf("_") != -1) {
			neighid = 0;
		}
		document.getElementById('neighid-' + id).value = neighid;
		document.getElementById('up-' + id).value = value;
	}
	function cancelWorkloads() {
		var answer = confirm("Are you sure to cancel checked workload?");
		if (answer == true) {
			var ids = findChecked('ActiveWorkload');
			document.getElementById('cancelIds').value = ids;
			document.getElementById('cancelForm').submit();
		}
	}
	function resubmitWorkloads() {
		var hids = findChecked('HistoryWorkload');
		var aids = findChecked('ArchivedWorkload');
		var ids = '';
		if(hids.length > 0)
			ids = hids + '_';
		ids += aids;
		document.getElementById('resubmitIds').value = ids;
		document.getElementById('resubmitForm').submit();
	}
  </script>
  <title>COSBench Controller</title> 
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <h3>Controller Overview  <span class="counter state">${cInfo.driverCount}</span></h3>
  <p>
    <span class="grid">
      <span class="label"><strong>Name</strong>:</span>
      <#if cInfo.name == "N/A" ><i class="low">not configured</i><#else>${cInfo.name}</#if>
    </span>
    <span class="grid">
      <span class="label"><strong>URL</strong>:</span>
      <#if cInfo.url == "N/A" ><i class="low">not configured</i><#else>${cInfo.url}</#if>
    </span>
  </p>
  <table class="info-table">
    <tr>
      <th>Driver</th>
      <th>Name</th>
      <th>URL</th>
      <th>IsAlive</th>
      <th style="width:15%;">Link</th>
    </tr>
    <#list cInfo.driverInfos as dInfo >
      <tr>
        <td>${dInfo_index + 1}</td>
        <td>${dInfo.name}</td>
        <td>${dInfo.url}</td>
        <#if dInfo.aliveState>
        	<td><div class="alive"></div></td>
        <#else>
        	<td><div class="dead"></div></td>
        </#if>
        <td><a href="${dInfo.url}" target="_blank">view details</a></td>
      </tr>
    </#list>
  </table>
  
  <div>
  	<p/>
    <p><a href="submit.html">submit new workloads</a></p>
  	<p><a href="config.html">config workloads</a></p>
  	<p><a href="advanced-config.html">advanced config for workloads</a></p>
  	<p/>
  </div>
  
  <h3>Active Workloads  <span class="counter state">${aInfos?size}</span></h3>
  <div>
    <table class="info-table">
      <tr>
      	<th style="width:5%;"><input type="checkbox" id="AllActive" onclick="checkAll(event,'ActiveWorkload')"></th>
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Submitted-At</th>
        <th>State</th>
        <th style="width:5%;">Order</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list aInfos?sort_by("order") as aInfo >
        <#if highlightId?? && aInfo.id == highlightId >
        	<tr class="high-light">
        <#else>
          <tr>
        </#if>
          <td><input type="checkbox" id="checkbox-${aInfo.id}" name="ActiveWorkload" onclick="checkItem(event,'AllActive')" value="${aInfo.id}"></td>
          <td onclick="checkMe('${aInfo.id}','AllActive');">${aInfo.id}</td>
          <td onclick="checkMe('${aInfo.id}','AllActive');">${aInfo.workload.name}</td>
          <td onclick="checkMe('${aInfo.id}','AllActive');">${aInfo.submitDate?datetime}</td>
          <td onclick="checkMe('${aInfo.id}','AllActive');"><span class="workload-state-${aInfo.state?lower_case} state">${aInfo.state?lower_case}</span></td>
          <td>
            <#if aInfo.state?lower_case == "queuing" >
          	<form id="form-${aInfo.id}" method="POST" align="center" action="index.html">
				<input id="id-${aInfo.id}" type="hidden" name="id" value="${aInfo.id}">
				<input id="neighid-${aInfo.id}" type="hidden" name="neighid" value="">
				<input id="up-${aInfo.id}" type="hidden" name="up" value="yes">
				<input type="image" style="width:20px;height:20px;" src="resources/up_arrow.png" onclick="changeOrder('${aInfo.id}','yes');" value="up">
				<input type="image" style="width:20px;height:20px;" src="resources/down_arrow.png" onclick="changeOrder('${aInfo.id}','no');" value="up">
			</form>
			</#if>
          </td>
          <td><a href="workload.html?id=${aInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
  </div>
  
  <form id="cancelForm" method="POST" action="index.html">
  	<input id="cancelIds" type="hidden" name="cancelIds" value="">
  	<input type="hidden" name="cancel" value="yes">
  	<input type="button" onclick="cancelWorkloads();" value="Cancel">
  </form>
  
  <div>
   <h3>Historical Workloads  <span class="counter state">${hInfos?size}</span></h3>
    <p><a href="matrix.html?type=histo&ops=read&ops=write&ops=delete&metrics=rt&rthisto=_95rt&metrics=t&metrics=succ">view performance matrix</a></p>
    <table class="info-table">
      <tr>
        <th style="width:5%;"><input type="checkbox" id="AllHistory" onclick="checkAll(event,'HistoryWorkload')"></th>
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Duration</th>
        <th>Op-Info</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list hInfos as hInfo >
        <tr>
          <td><input type="checkbox" id="checkbox-${hInfo.id}" name="HistoryWorkload" onclick="checkItem(event,'AllHistory')" value="${hInfo.id}"></td>
          <td onclick="checkMe('${hInfo.id}','AllHistory');");">${hInfo.id}</td>
          <td onclick="checkMe('${hInfo.id}','AllHistory');");">${hInfo.workload.name}</td>
          <td onclick="checkMe('${hInfo.id}','AllHistory');");"><#if hInfo.startDate?? >${hInfo.startDate?datetime}<#else>N/A</#if> - ${hInfo.stopDate?time}</td>
          <td onclick="checkMe('${hInfo.id}','AllHistory');");">
            <#list hInfo.allOperations as op >
              ${op}<#if op_has_next>,</#if>
            </#list>
          </td>
          <td onclick="checkMe('${hInfo.id}','AllHistory');"><span class="workload-state-${hInfo.state?lower_case} state">${hInfo.state?lower_case}</span></td>
          <td><a href="workload.html?id=${hInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
    

    <h3>Archived Workloads  <span class="counter state">${archInfos?size}</span></h3>
    <p><a href="matrix.html?type=arch&ops=read&ops=write&ops=delete&metrics=rt&rthisto=_95rt&metrics=t&metrics=succ">view performance matrix</a></p>
    <p>
	    <#if loadArch == false>
		  <p><a href="index.html?loadArch=true">load archived workloads</a></p>
		  <#elseif loadArch == true>
		  <p><a href="index.html?loadArch=false">unload archived workloads</a></p>
	    </#if>
    </p>
    
    <#if loadArch == true>
    <table class="info-table">
      <tr>
        <th style="width:5%;"><input type="checkbox" id="AllArchived" onclick="checkAll(event,'ArchivedWorkload')"></th>
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Duration</th>
        <th>Op-Info</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
       <#list archInfos as aInfo >
        <tr>
          <td><input type="checkbox" id="checkbox-${aInfo.id}" name="ArchivedWorkload" onclick="checkItem(event,'AllArchived')" value="${aInfo.id}"></td>
          <td onclick="checkMe('${aInfo.id}','AllArchived');");">${aInfo.id}</td>
          <td onclick="checkMe('${aInfo.id}','AllArchived');");">${aInfo.workload.name}</td>
          <td onclick="checkMe('${aInfo.id}','AllArchived');");"><#if aInfo.startDate?? >${aInfo.startDate?datetime}<#else>N/A</#if> - ${aInfo.stopDate?time}</td>
          <td onclick="checkMe('${aInfo.id}','AllArchived');");">
            <#list aInfo.allOperations as op >
              ${op}<#if op_has_next>,</#if>
            </#list>
          </td>
          <td onclick="checkMe('${aInfo.id}','AllArchived');"><span class="workload-state-${aInfo.state?lower_case} state">${aInfo.state?lower_case}</span></td>
          <td><a href="workload.html?id=${aInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
    </#if>
    
    <form id="resubmitForm" method="POST" action="index.html">
  		<input id="resubmitIds" type="hidden" name="resubmitIds" value="">
  		<input type="hidden" name="resubmit" value="yes">
  		<input type="button" onclick="resubmitWorkloads();" value="resubmit">
  	</form>
  	
  </div>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>