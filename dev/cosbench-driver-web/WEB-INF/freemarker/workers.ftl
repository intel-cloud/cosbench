<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <#if isRunning >
    <meta http-equiv="refresh" content="${info.mission.interval}; url=workers.html?id=${info.id}">
  <#elseif !isStopped >
    <meta http-equiv="refresh" content="10; url=workers.html?id=${info.id}">
  </#if>
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Workers</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <p>
    <a href="index.html">index</a> ->
    <a href="mission.html?id=${info.id}">mission</a> ->
    <span>workers</span>
  </p>
  <h2>Workers</h2>
  <h3>Mission Info</h3>
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
      ${info.state?lower_case}
    </span>
  </p>
  <#if isStopped >
    <h3>Final Report</h3>
    <#list info.workerInfos as wInfo>
      <h4>Work ${wInfo.index}</h4>
      <#assign allMetrics = wInfo.report.allMetrics >
      <#include "metrics.ftl">
    </#list>
  <#elseif isRunning >
    <h3>Snapshots</h3>
    <#list info.workerInfos as wInfo>
      <h4>Work ${wInfo.index}</h4>
      <#assign snapshot = wInfo.snapshot >
      <#if snapshot?? >
        <#assign allMetrics = snapshot.report.allMetrics >
        <#include "metrics.ftl">
        <p class="warn">The snapshot was taken at ${snapshot.timestamp?time} with version ${snapshot.version}.</p>
      <#else>
        <#assign allMetrics = [] >
        <#include "metrics.ftl">
      </#if>
    </#list>
  <#else>
    <p class="warn">Status is not currently available.</p>
  </#if>
  <p class="warn">There are totally ${info.workerCount} workers spawned.</p>
  <p><a href="mission.html?id=${info.id}">go back to mission</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>