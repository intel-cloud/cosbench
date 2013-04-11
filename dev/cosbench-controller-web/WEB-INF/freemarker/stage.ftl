<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <#if isRunning >
    <meta http-equiv="refresh" content="${sInfo.interval}">
  <#elseif !isStopped >
    <meta http-equiv="refresh" content="10">
  </#if>
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Stage Details</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <p>
    <a href="index.html">index</a> ->
    <a href="workload.html?id=${wInfo.id}">workload</a> ->
    <span>stage</span>
  </p>
  <h2>Stage</h2>
  <h3>Basic Info</h3>
  <p>
    <span class="grid">
      <span class="label"><strong>ID</strong>:</span>
      ${wInfo.id}-${sInfo.id}
    </span>
    <span class="grid">
      <span class="label"><strong>Name</strong>:</span>
      ${sInfo.stage.name}
    </span>
    <span class="grid">
      <span class="label"><strong>Interval</strong>:</span>
      <#if (sInfo.interval > 0) > 
        ${sInfo.interval} s
      <#else>
        N/A
      </#if>
    </span>
    <span class="grid">
      <span class="label"><strong>Current State</strong>:</span>
      <span class="stage-state-${sInfo.state?lower_case} state">${sInfo.state?lower_case}</span>
    </span>
  </p>
  <#if showDetails >
    <h4>State History:</h4>
    <table class="info-table">
      <tr>
        <th style="width:20%;">State</th>
        <th>Date</th>
      </tr>
      <#list sInfo.stateHistory as his >
        <#if his_has_next >
          <tr>
        <#else>
          <tr class="high-light">
        </#if>
          <td>${his.name?lower_case}</td>
          <td>${his.date?datetime}</td>
        </tr>
      </#list>
    </table>
    <p><a href="stage.html?wid=${wInfo.id}&sid=${sInfo.id}">hide details</a></p>
  <#else>  
    <p><a href="stage.html?wid=${wInfo.id}&sid=${sInfo.id}&showDetails=True">more info</a></p>
  </#if>
  <#if isStopped >
    <h3>Final Result</h3>
    <#assign allMetrics = sInfo.report.allMetrics >
    <#include "metrics.ftl">
    <p>
    <#if perfDetails >
      <a href="stage.html?wid=${wInfo.id}&sid=${sInfo.id}">hide peformance details</a>
    <#else>
      <a href="stage.html?wid=${wInfo.id}&sid=${sInfo.id}&perfDetails=True">show peformance details</a>
    </#if>
    <a href="timeline.html?wid=${wInfo.id}&sid=${sInfo.id}">view timeline status</a>
    </p>
  <#elseif isRunning >
    <h3>Snapshot</h3>
    <#assign snapshot = sInfo.snapshot >
    <#assign allMetrics = snapshot.report.allMetrics >
    <#include "metrics.ftl">
    <p class="warn">The snapshot was taken at ${snapshot.timestamp?time} with version ${snapshot.version}.</p>
  </#if>
  <h3>Missions to Driver</h3>
  <#if (sInfo.taskCount > 0) >
    <table class="info-table">
      <tr>
        <th class="id">Driver</th>
        <th>Mission</th>
        <th>Work</th>
        <th>Worker-Info</th>
        <th>Op-Info</th>
        <th>State</th>
        <th>Link</th>
      </tr>
      <#list sInfo.taskInfos as tInfo >
        <tr>
          <#assign schedule = tInfo.schedule >
          <td>${schedule.driver.name}</td>
          <td>${tInfo.missionId!"N/A"}</td>
          <td>${schedule.work.name}</td>
          <td>${schedule.offset + 1} - ${schedule.workers + schedule.offset}</td>
          <td>
            <#list schedule.work.operations as op >
              ${op.type} (${op.ratio}%)<#if op_has_next>,</#if>
            </#list>
          </td>
          <td><span class="task-state-${tInfo.state?lower_case} state">${tInfo.state?lower_case}</span></td>
          <td>
            <#if tInfo.missionId?? >
              <a href="${schedule.driver.url}/mission.html?id=${tInfo.missionId}" target="_blank">view details</a>
            <#else>
              N/A
            </#if>
          </td>
        </tr>
      </#list>
    </table>
    <p class="warn">There are ${sInfo.taskCount} driver tasks involved.</p>
  <#else>
    <p class="warn">As the stage has not been run yet, no task information is available right now.</p>
  </#if>
  <p><a href="workload.html?id=${wInfo.id}">go back to workload</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>