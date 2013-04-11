<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <#if isStageRunning >
    <meta http-equiv="refresh" content="${info.currentStage.interval}; url=workload.html?id=${info.id}" />
  <#elseif isRunning >
    <meta http-equiv="refresh" content="5; url=workload.html?id=${info.id}" />
  <#elseif !isStopped >
    <meta http-equiv="refresh" content="10; url=workload.html?id=${info.id}" />
  </#if>
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Workload Details</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <p>
    <a href="index.html">index</a> ->
    <span>workload</span>
  </p>
  <h2>Workload</h2>
  <h3>Basic Info</h3>
  <p>
    <span class="grid">
      <span class="label"><strong>ID</strong>:</span>
      ${info.id}
    </span>
    <span class="grid">
      <span class="label"><strong>Name</strong>:</span>
      ${info.workload.name}
    </span>
    <span class="grid">
      <span class="label"><strong>Current State</strong>:</span>
      <span class="workload-state-${info.state?lower_case} state">${info.state?lower_case}</span>
    </span>
    <#if isRunning && info.currentStage?? >
      <span class="grid">
        <span class="label"><strong>Current Stage</strong>:</span>
        ${info.currentStage.stage.name}
      </span>
    </#if>
  </p>
  <p>
    <span class="grid">
      <span class="label"><strong>Submitted At</strong>:</span>
      ${info.submitDate?datetime}
    </span>
    <span class="grid">
      <span class="label"><strong>Started At</strong>:</span>
      <#if info.startDate?? >
        ${info.startDate?datetime}
      <#else>N/A</#if>
    </span>
    <span class="grid">
      <span class="label"><strong>Stopped At</strong>:</span>
      <#if info.stopDate?? >
        ${info.stopDate?datetime}
      <#else>N/A</#if>
    </span>
  </p>
  <#if showDetails >
    <h4>State History</h4>
    <table class="info-table">
      <tr>
        <th style="width:20%;">State</th>
        <th>Date</th>
      </tr>
      <#list info.stateHistory as his >
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
    <p><a href="workload.html?id=${info.id}">hide details</a></p>
  <#else>
    <p><a href="workload.html?id=${info.id}&showDetails=True">more info</a></p>
  </#if>
  <#if isStopped >
    <h3>Final Result</h3>
    <#assign allMetrics = info.report.allMetrics >
    <#include "metrics.ftl">
    <#if perfDetails >
      <p><a href="workload.html?id=${info.id}">hide peformance details</a></p>
    <#else>
      <p><a href="workload.html?id=${info.id}&perfDetails=True">show peformance details</a></p>
    </#if>
  <#elseif isRunning >
    <h3>Snapshot</h3>
    <#assign snapshot = info.snapshot >
    <#assign allMetrics = snapshot.report.allMetrics >
    <#include "metrics.ftl">
    <p class="warn">The snapshot was taken at ${snapshot.timestamp?time} with version ${snapshot.version}.</p>
  </#if>
  <h3>Stages</h3>
  <div class="workflow">
    <center>
      <#list info.stageInfos as sInfo >
        <#if info.currentStage?? && info.currentStage.id == sInfo.id >
        <strong>${sInfo.stage.name}</strong>
        <#else>
        ${sInfo.stage.name}
        </#if>
        <#if sInfo_has_next > --> </#if>
      </#list>
    </center>
  </div>
  <table class="info-table">
    <tr>
      <th class="id">ID</th>
      <th>Name</th>
      <th>Works</th>
      <th>Workers</th>
      <th>Op-Info</th>
      <th>State</th>
      <th>Link</th>
    </tr>
    <#list info.stageInfos as sInfo >
      <#if info.currentStage?? && sInfo.id == info.currentStage.id >
        <tr class="high-light">
      <#else>
        <tr>
      </#if>
        <td>${info.id}-${sInfo.id}</td>
        <td>${sInfo.stage.name}</td>
        <td>${sInfo.workCount} wks</td>
        <td>${sInfo.workerCount} wkrs</td>
        <td>
          <#list sInfo.operations as op >
            ${op}<#if op_has_next>,</#if>
          </#list>
        </td>
        <td><span class="stage-state-${sInfo.state?lower_case} state">${sInfo.state?lower_case}</span></td>
        <td><a href="stage.html?wid=${info.id}&sid=${sInfo.id}">view details</a></td>
      </tr>
    </#list>
  </table>
  <p class="warn">There are ${info.stageCount} stages in this workload.</p>
  <h3>Actions</h3>
  <p>
    <#if !isStopped >
      <a class="label" href="cancel-workload.do?id=${info.id}">cancel-workload</a>
    <#else>
      <a class="label" href="download-log.do?id=${info.id}">download-log</a>
      <a class="label" href="download-config.do?id=${info.id}">download-config</a>
    </#if>
  </p>  
  <p><a href="index.html">go back to index</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>