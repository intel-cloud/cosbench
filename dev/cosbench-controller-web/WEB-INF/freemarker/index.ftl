<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="refresh" content="60">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <script type="text/javascript" src="resources/cosbench.js"></script>
  <title>COSBench Controller</title> 
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <h3>Controller Overview</h3>
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
      <th style="width:15%;">Link</th>
    </tr>
    <#list cInfo.driverInfos as dInfo >
      <tr>
        <td>${dInfo_index + 1}</td>
        <td>${dInfo.name}</td>
        <td>${dInfo.url}</td>
        <td><a href="${dInfo.url}" target="_blank">view details</a></td>
      </tr>
    </#list>
  </table>
  <p>There are ${cInfo.driverCount} drivers attached to the controller.</p>
  <h3>Active Workloads</h3>
  <div>
    <table class="info-table">
      <tr>
      	<th style="width:5%;"><input type="checkbox" name="AllActive" onclick="checkAll('ActiveWorkload')"></th>
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Submitted-At</th>
        <th>State</th>
        <th style="width:5%;">Order</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list aInfos?sort_by("priority") as aInfo >
        <#if highlightId?? && aInfo.id == highlightId >
        	<tr class="high-light">
        <#else>
          <tr>
        </#if>
          <td><input type="checkbox" id="checkbox-${aInfo.id}" name="ActiveWorkload" onclick="checkItem('AllActive')" value="${aInfo.id}"></td>
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
				<input type="image" style="width:20px;height:20px;" src="resources/up_arrow.png" onclick="changePriority('${aInfo.id}','yes');" value="up">
				<input type="image" style="width:20px;height:20px;" src="resources/down_arrow.png" onclick="changePriority('${aInfo.id}','no');" value="up">
			</form>
			</#if>
          </td>
          <td><a href="workload.html?id=${aInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
  </div>
  <p class="warn">There are currently ${aInfos?size} active workloads.</p>
  
  <form id="cancelForm" method="POST" action="index.html">
  	<input id="cancelIds" type="hidden" name="cancelIds" value="">
  	<input type="hidden" name="cancel" value="yes">
  	<input type="button" onclick="cancelWorkloads();" value="Cancel">
  </form>
  <p><a href="submit.html">submit new workloads</a></p>
  <p><a href="config.html">config workloads</a></p>
  <h3>Historical Workloads</h3>
  <div>
    <p><a href="matrix.html?ops=read&ops=write&ops=delete&metrics=rt&rthisto=_95rt&metrics=t&metrics=succ">view performance matrix</a></p>
    <table class="info-table">
      <tr>
        <th style="width:5%;"><input type="checkbox" name="AllHistory" onclick="checkAll('HistoryWorkload')"></th>
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Duration</th>
        <th>Op-Info</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list hInfos as hInfo >
        <tr>
          <td><input type="checkbox" id="checkbox-${hInfo.id}" name="HistoryWorkload" onclick="checkItem('AllHistory')" value="${hInfo.id}"></td>
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
    <#if (hInfos?size < 10) >
      <p class="warn">There are ${hInfos?size} Historical workloads.</p>
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