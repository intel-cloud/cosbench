<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="refresh" content="60">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>COSBench Driver</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <h3>Driver Overview</h3>
  <p>
    <span class="grid">
      <span class="label"><strong>Name</strong>:</span>
      <#if dInfo.name == "N/A" ><i class="low">not configured</i><#else>${dInfo.name}</#if>
    </span>
    <span class="grid">
      <span class="label"><strong>URL</strong>:</span>
      <#if dInfo.url == "N/A" ><i class="low">not configured</i><#else>${dInfo.url}</#if>
    </span>
  </p>
  <h3>Active Missions</h3>
  <div>
    <table class="info-table">
      <tr>
        <th class="id" style="width:12%;">ID</th>
        <th>Name</th>
        <th>Submitted-At</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list aInfos as aInfo >
        <tr>
          <td>${aInfo.id}</td>
          <td>${aInfo.mission.name}</td>
          <td>${aInfo.date?datetime}</td>
          <td><span class="mission-state-${aInfo.state?lower_case} state">${aInfo.state?lower_case}</span></td>
          <td><a href="mission.html?id=${aInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
  </div>
  <p class="warn">There are currently ${aInfos?size} active missions.</p>
  <!--
  <p><a href="submit.html">submit new missions</a></p>
  -->
  <h3>History Missions</h3>
  <div>
    <table class="info-table">
      <tr>
        <th class="id" style="width:12%;">ID</th>
        <th>Name</th>
        <th>Submitted-At</th>
        <th>State</th>
        <th style="width:15%;">Link</th>
      </tr>
      <#list hInfos as hInfo >
        <tr>
          <td>${hInfo.id}</td>
          <td>${hInfo.mission.name}</td>
          <td>${hInfo.date?datetime}</td>
          <td><span class="mission-state-${hInfo.state?lower_case} state">${hInfo.state?lower_case}</span></td>
          <td><a href="mission.html?id=${hInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
    <p class="warn">There are ${hInfos?size} Historical missions.</p>
  </div>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>