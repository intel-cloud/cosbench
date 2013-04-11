<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Mission Submission</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <#if submitted?? >
    <h3>Submission Results</h3>
    <div>
      <p><span class="success"><strong>Success</strong>: ${submitted}!</span><p>
      <p><a href="mission.html?id=${id}">view mission info</a></p>
      <p>You may continue to submit new missions via the following form.</p>
    </div>
  </#if>
  <h3>Mission Submission</h3>
  <div>
    <p class="warn">This is a mission submission form; workload should be submitted to the controller.</p>
    <form name="submit-mission" action="submit-mission.do" method="POST" enctype="multipart/form-data">
      <#if error?? >
        <p><span class="error"><strong>Note</strong>: ${error}!</span></p>
      </#if>
      <label for="config">mission config:</label>
      <input name="config" type="file" id="config" style="width:500px" />
      <input type="submit" value="submit" /> 
    </form>
  </div>
  <p><a href="index.html">go back to index</a></p>
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
        <#if submitted?? && aInfo.id == id >
          <tr class="high-light">
        <#else>
          <tr>
        </#if>
          <td>${aInfo.id}</td>
          <td>${aInfo.mission.name}</td>
          <td>${aInfo.date?datetime}</td>
          <td>${aInfo.state?lower_case}</td>
          <td><a href="mission.html?id=${aInfo.id}">view details</a></td>
        </tr>
      </#list>
    </table>
    <p class="warn">There are currently ${aInfos?size} active missions.</p>
  </div>
  <p><a href="index.html">go back to index</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>