<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="refresh" content="60">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
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
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Submitted-At</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list aInfos as aInfo >
        <tr>
          <td>${aInfo.id}</td>
          <td>${aInfo.workload.name}</td>
          <td>${aInfo.submitDate?datetime}</td>
          <td><span class="workload-state-${aInfo.state?lower_case} state">${aInfo.state?lower_case}</span></td>
          <td><a href="workload.html?id=${aInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
  </div>
  <p class="warn">There are currently ${aInfos?size} active workloads.</p>
  <p><a href="submit.html">submit new workloads</a></p>
  <p><a href="config.html">config workloads</a></p>
  <h3>Historical Workloads</h3>
  <div>
    <p><a href="matrix.html?ops=read&ops=write&ops=delete&metrics=rt&rthisto=_95rt&metrics=t&metrics=succ">view performance matrix</a></p>
    <table class="info-table">
      <tr>
        <th class="id" style="width:5%;">ID</th>
        <th>Name</th>
        <th>Duration</th>
        <th>Op-Info</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list hInfos as hInfo >
        <tr>
          <td>${hInfo.id}</td>
          <td>${hInfo.workload.name}</td>
          <td><#if hInfo.startDate?? >${hInfo.startDate?datetime}<#else>N/A</#if> - ${hInfo.stopDate?time}</td>
          <td>
            <#list hInfo.allOperations as op >
              ${op}<#if op_has_next>,</#if>
            </#list>
          </td>
          <td><span class="workload-state-${hInfo.state?lower_case} state">${hInfo.state?lower_case}</span></td>
          <td><a href="workload.html?id=${hInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
    <#if (hInfos?size < 10) >
      <p class="warn">There are ${hInfos?size} Historical workloads.</p>
    </#if>
  </div>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>