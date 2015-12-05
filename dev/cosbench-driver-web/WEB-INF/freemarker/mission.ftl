<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <#if isRunning >
    <meta http-equiv="refresh" content="${info.mission.interval}; url=mission.html?id=${info.id}">
  <#elseif !isStopped >
    <meta http-equiv="refresh" content="10; url=mission.html?id=${info.id}">
  </#if>  
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Mission Details</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <p>
    <a href="index.html">index</a> ->
    <span>mission</span>
  </p>
  <h2>Mission</h2>
  <h3>Basic Info</h3>
  <p>
    <span class="grid">
      <span class="label"><strong>ID</strong>:</span>
      ${info.id}
    </span>
    <span class="grid">
      <span class="label"><strong>Submit Date</strong>:</span>
      ${info.date?datetime}
    </span>
    <span class="grid">
      <span class="label"><strong>Current State</strong>:</span>
      <span class="mission-state-${info.state?lower_case} state">${info.state?lower_case}</span>
    </span>
    <#if showDetails >
      <h4>State History:</h4>
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
      <h4>Target Systems:</h4>
      <table class="info-table">
        <tr>
          <th>Sys</th>
          <th>Type</th>
          <th>Config</th>
        </tr>
        <tr>
          <td>auth</td>
          <td>${info.mission.auth.type}</td>
          <td>
            <#assign config = info.mission.auth.config!"no config specified for this auth" >
            <#list config?split(";") as item >
  			  ${item}<#if item_has_next >,</#if>
            </#list>
          </td>
        </tr>
        <tr>
          <td>storage</td>
          <td>${info.mission.storage.type}</td>
          <td>
            <#assign config = info.mission.storage.config!"no config specified for this storage" >
            <#list config?split(";") as item >
  			  ${item}<#if item_has_next >,</#if>
            </#list>
          </td>
        </tr>
      </table>
      <p><a href="mission.html?id=${info.id}">hide details</a></p>
    <#else>
      <p><a href="mission.html?id=${info.id}&showDetails=True">more info</a></p>
    </#if>
  </p>
  <#if isStopped >
    <h3>Final Result</h3>
    <#assign allMetrics = info.report.allMetrics >
    <#include "metrics.ftl">
    <#if perfDetails >
      <p><a href="mission.html?id=${info.id}">hide peformance details</a></p>
    <#else>
      <p><a href="mission.html?id=${info.id}&perfDetails=True">show peformance details</a></p>
    </#if>
  <#elseif isRunning >
    <h3>Snapshot</h3>
    <#assign snapshot = info.snapshot >
    <#assign allMetrics = snapshot.report.allMetrics >
    <#include "metrics.ftl">
    <p class="warn">The snapshot was taken at ${snapshot.timestamp?time} with version ${snapshot.version}.</p>
  </#if>
  <h3>Work Info</h3>
  <p>
    <span class="grid">
      <span class="label"><strong>Name</strong>:</span>
      ${info.mission.name}
    </span>
    <span class="grid">
      <span class="label"><strong>Workers</strong>:</span>
      ${info.mission.offset + 1} - ${info.mission.offset + info.mission.workers}
    </span>
    <span class="grid">
      <span class="label"><strong>Interval</strong>:</span>
      ${info.mission.interval}s
    </span>
    <#if (info.mission.rampup > 0) >
      <span class="grid">
        <span class="label"><strong>Rampup</strong>:</span>
        ${info.mission.rampup}s
      </span>
    </#if>
    <#if (info.mission.runtime > 0) >
      <span class="grid">
        <span class="label"><strong>Runtime</strong>:</span>
        ${info.mission.runtime}s
      </span>
    </#if>
    <#if (info.mission.rampdown > 0) >
      <span class="grid">
        <span class="label"><strong>Rampdown</strong>:</span>
        ${info.mission.rampdown}s
      </span>
    </#if>
    <#assign totalOps = info.mission.totalOps>
    <#if (totalOps > 0) >
      <span class="grid">
        <span class="label"><strong>Total Ops</strong>:</span>
        <#if (totalOps >= 1000) >
          <#assign totalOps = totalOps / 1000 >
          <#if (totalOps >= 1000) >
            <#assign totalOps = totalOps / 1000 >
            <#if (totalOps >= 1000) >
              <#assign totalOps = totalOps / 1000 >
              <#if (totalOps >= 1000) >
                <#assign totalOps = totalOps / 1000 >
                <#if (totalOps >= 1000) >
                  <#assign totalOps = totalOps / 1000 >
                  ${totalOps} pops
                <#else>
                  ${totalOps} tops
                </#if>
              <#else>
                ${totalOps} gops
              </#if>
            <#else>
              ${totalOps} mops
            </#if>
          <#else>
            ${totalOps} kops
          </#if>
        <#else>
          ${totalOps} ops
        </#if>
      </span>
    </#if>
    <#assign totalBytes = info.mission.totalBytes >
    <#if (totalBytes > 0) >
      <span class="grid">
        <span class="label"><strong>Total Bytes</strong>:</span>
        <#if (totalBytes >= 1000) >
          <#assign totalBytes = totalBytes / 1000 >
          <#if (totalBytes >= 1000) >
            <#assign totalBytes = totalBytes / 1000 >
            <#if (totalBytes >= 1000) >
              <#assign totalBytes = totalBytes / 1000 >
              <#if (totalBytes >= 1000) >
                <#assign totalBytes = totalBytes / 1000 >
                <#if (totalBytes >= 1000) >
                  <#assign totalBytes = totalBytes / 1000 >
                  ${totalBytes} PB
                <#else>
                  ${totalBytes} TB
                </#if>
              <#else>
                ${totalBytes} GB
              </#if>
            <#else>
              ${totalBytes} MB
            </#if>
          <#else>
            ${totalBytes} KB
          </#if>
        <#else>
          ${totalBytes} B
        </#if>
      </span>
    </#if>
    <h4>Operations:</h4>
    <table class="info-table">
      <tr>
        <th class="id">Op</th>
        <th>Ratio</th>
        <th>Division</th>
        <th>Config</th>
      </tr>
      <#list info.mission.operations as op >
        <tr>
          <td>${op.type}</td>
          <td>${op.ratio}</td>
          <td>${op.division}</td>
          <td>
            <#list op.config?split(";") as item >
  			  ${item}<#if item_has_next >,</#if>
            </#list>
          </td>
        </tr>
      </#list>
    </table>
  </p>
  <#if isStopped>
    <#if showErrorStatistics> 
    <h3>Error Statistics</h3>
    <table class="info-table">
        <tr>
          <th>Error Message</th>
          <th>Error Code</th>
          <th>Occurrence Number</th>
          <th>StatckTrace</th>  
        </tr>
        <#list info.errorStatistics.stackTraceAndMessage?keys as trace>
          <#if trace_has_next >
            <tr>
          <#else>
            <tr class="high-light">
          </#if>
		  <#if trace??>
            <td class="id" style="width:13%">${info.errorStatistics.stackTraceAndMessage[trace]}</td>
            <td>${info.errorStatistics.stackTraceAndErrorCode[trace]}</td>
            <td>${info.errorStatistics.stackTraceAndNum[trace]}</td>
            <td>${info.errorStatistics.stackTraceAndEntireTrace[trace]?substring(0,50)+"......"}</td>
		  </#if>
       </tr>
        </#list>
      </table>
      <a class="label" href="download-log.do?id=${info.id}">download-log</a>  
      <p><a href="mission.html?id=${info.id}">hide error statistics details</a></p>
    <#else>
      <p><a href="mission.html?id=${info.id}&showErrorStatistics=True">show error statistics details</a></p>
    </#if>
  </#if>


  </p>
  <h3>Actions</h3>
  <p>
    <a class="label" href="workers.html?id=${info.id}">view workers</a>
    <#if !isStopped >
      <a class="label" href="abort-mission.do?id=${info.id}">abort-mission</a>
    </#if>
    <#if toBeAuthed >
      <a class="label" href="perform-login.do?id=${info.id}">perform-login</a>
    </#if>
    <#if toBeLaunched >
      <a class="label" href="launch-mission.do?id=${info.id}">launch-mission</a>
    </#if>
    <#if toBeClosed >
      <a class="label" href="close-mission.do?id=${info.id}">close-mission</a>
    </#if>
  </p>
  <p><a href="index.html">go back to index</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>
