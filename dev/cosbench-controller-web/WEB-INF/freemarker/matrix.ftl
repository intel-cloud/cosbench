<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <meta http-equiv="refresh" content="60">
  <link rel="stylesheet" type="text/css" href="resources/cosbench.css" />
  <title>Performance Matrix</title>
</head>
<body>
<#include "header.ftl">
<div id="main">
<div class="top"><br /></div>
<div class="content">
  <p>
    <a href="index.html">index</a> ->
    <span>matrix</span>
  </p>
  <h2>Performance Matrix</h2>
  <h3>Filters</h3>
  <div class="form">
    <form name="perf-matrix" action="matrix.html" method="GET" onreset="window.location.href='matrix.html?type=${type}&ops=read&ops=write&ops=delete&metrics=rt&rthisto=_95rt&metrics=t&metrics=succ'">
      <input name="type" type="hidden" value="${type}">
      <span class="label">Operations:</span>
      <span class="checkbox"><input name="ops" type="checkbox" value="read"
        <#if allOps!false || read!false >checked="true"</#if> /></span> Read
      <span class="checkbox"><input name="ops" type="checkbox" value="write"
        <#if allOps!false || write!false >checked="true"</#if> /></span> Write
      <span class="checkbox"><input name="ops" type="checkbox" value="delete"
        <#if allOps!false || delete!false >checked="true"</#if> /></span> Delete
     <span class="checkbox"><input name="ops" type="checkbox" value="init"
        <#if allOps!false || init!false >checked="true"</#if> /></span> Init
      <span class="checkbox"><input name="ops" type="checkbox" value="prepare"
        <#if allOps!false || prepare!false >checked="true"</#if> /></span> Prepare
      <span class="checkbox"><input name="ops" type="checkbox" value="cleanup"
        <#if allOps!false || cleanup!false >checked="true"</#if> /></span> Cleanup
      <span class="checkbox"><input name="ops" type="checkbox" value="dispose"
        <#if allOps!false || dispose!false >checked="true"</#if> /></span> Dispose
      <br /><br />
      <span class="label">General Metrics:</span>
      <span class="checkbox"><input name="metrics" type="checkbox" value="oc"
        <#if allMetrics!false || oc!false >checked="true"</#if> /></span> Op Count
      <span class="checkbox"><input name="metrics" type="checkbox" value="bc"
        <#if allMetrics!false || bc!false >checked="true"</#if> /></span> Byte Count
      <span class="checkbox"><input name="metrics" type="checkbox" value="rt"
        <#if allMetrics!false || rt!false >checked="true"</#if> /></span> Avg ResTime
      <span class="checkbox"><input name="metrics" type="checkbox" value="pt"
        <#if allMetrics!false || pt!false >checked="true"</#if> /></span> Avg ProcTime
      <span class="checkbox"><input name="metrics" type="checkbox" value="t"
        <#if allMetrics!false || t!false >checked="true"</#if> /></span> Throughput
      <span class="checkbox"><input name="metrics" type="checkbox" value="bw"
        <#if allMetrics!false || bw!false >checked="true"</#if> /></span> Bandwidth
      <span class="checkbox"><input name="metrics" type="checkbox" value="succ"
        <#if allMetrics!false || succ!false >checked="true"</#if> /></span> Success Ratio
      <br /><br />
      <span class="label">ResTime (RT):</span>
      <span class="checkbox"><input name="rthisto" type="checkbox" value="_60rt"
        <#if _60rt!false >checked="true"</#if> /></span> 60% RT
      <span class="checkbox"><input name="rthisto" type="checkbox" value="_80rt"
        <#if _80rt!false >checked="true"</#if> /></span> 80% RT
      <span class="checkbox"><input name="rthisto" type="checkbox" value="_90rt"
        <#if _90rt!false >checked="true"</#if> /></span> 90% RT
      <span class="checkbox"><input name="rthisto" type="checkbox" value="_95rt"
        <#if _95rt!false >checked="true"</#if> /></span> 95% RT
      <span class="checkbox"><input name="rthisto" type="checkbox" value="_99rt"
        <#if _99rt!false >checked="true"</#if> /></span> 99% RT
      <span class="checkbox"><input name="rthisto" type="checkbox" value="_100rt"
        <#if _100rt!false >checked="true"</#if> /></span> 100% RT
      <br /><br />
      <span class="label">Others:</span>
      <span class="checkbox"><input name="others" type="checkbox" value="cfg"
        <#if cfg!false >checked="true"</#if> /></span> Config Info
      <br /><br />
      <input type="submit" value="filter">
      <input type="reset" value="reset"/>
    </form>
  </div>
  <h3>Metrics</h3>
  <table class="info-table">
    <tr>
      <th class="id">ID</th>
      <th>Op-Type</th>
      <#if allMetrics!false || oc!false ><th>Op-Count</th></#if>
      <#if allMetrics!false || bc!false ><th>Byte-Count</th></#if>
      <#if allMetrics!false || rt!false ><th>Avg-ResTime</th></#if>
      <#if _60rt!false ><th>60%-ResTime</th></#if>
      <#if _80rt!false ><th>80%-ResTime</th></#if>
      <#if _90rt!false ><th>90%-ResTime</th></#if>
      <#if _95rt!false ><th>95%-ResTime</th></#if>
      <#if _99rt!false ><th>99%-ResTime</th></#if>
      <#if _100rt!false ><th>100%-ResTime</th></#if>
      <#if allMetrics!false || pt!false ><th>Avg-ProcTime</th></#if>
      <#if allMetrics!false || t!false ><th>Throughput</th></#if>
      <#if allMetrics!false || bw!false ><th>Bandwidth</th></#if>
      <#if allMetrics!false || succ!false ><th>Succ-Ratio</th></#if>
      <#if cfg!false ><th>Config-Info</th></#if>
      <th style="width:15%;">Link</th>
    </tr>
    <#list hInfos as hInfo >
      <#list hInfo.stageInfos as sInfo >
        <#assign mid = 1 >
        <#list sInfo.report.allMetrics as mInfo >
          <#if allOps!false ||
            (mInfo.opType == "read" && read!false) ||
            (mInfo.opType == "write" && write!false) ||
            (mInfo.opType == "delete" && delete!false) ||
            (mInfo.opType == "init" && init!false) ||
            (mInfo.opType == "prepare" && prepare!false) ||
            (mInfo.opType == "cleanup" && cleanup!false) || 
            (mInfo.opType == "dispose" && dispose!false) >
          <tr>
            <td>${hInfo.id}-${sInfo.id}-${mid}</td>
            <#assign mid = mid + 1 >
            <td>${mInfo.opType}<#if mInfo.opType != mInfo.sampleType>-${mInfo.sampleType}</#if></td>
            <#if allMetrics!false || oc!false >
            <td>
              <#assign op = mInfo.sampleCount >
              <#if (op >= 1000) >
                <#assign op = op / 1000 >
                <#if (op >= 1000) >
                  <#assign op = op / 1000 >
                  <#if (op >= 1000) >
                    <#assign op = op / 1000 >
                    ${op?string("0.##")} gops
                  <#else>
                    ${op?string("0.##")} mops
                  </#if>
                <#else>
                  ${op?string("0.##")} kops
                </#if>
              <#else>
                ${op?string("0.##")} ops
              </#if>
            </td>
            </#if>
            <#if allMetrics!false || bc!false >
            <td>
              <#assign data = mInfo.byteCount >
              <#if (data >= 1000) >
                <#assign data = data / 1000 >
                <#if (data >= 1000) >
                  <#assign data = data / 1000 >
                  <#if (data >= 1000) >
                    <#assign data = data / 1000 >
                    <#if (data >= 1000) >
                      <#assign data = data / 1000 >
                      <#if (data >= 1000) >
                        <#assign data = data / 1000 >
                        ${data?string("0.##")} PB
                      <#else>
                        ${data?string("0.##")} TB
                      </#if>
                    <#else>
                      ${data?string("0.##")} GB
                    </#if>
                  <#else>
                    ${data?string("0.##")} MB
                  </#if>
                <#else>
                  ${data?string("0.##")} KB
                </#if>
              <#else>
                ${data?string("0.##")} B
              </#if>
            </td>
            </#if>
            <#if allMetrics!false || rt!false >
            <td>
              <#if mInfo.avgResTime == 0>
                N/A
              <#else>
                ${mInfo.avgResTime?string("0.##")} ms
              </#if>
            </td>
            </#if>
            <#if _60rt!false >
            <td>
              <#if !mInfo.latency._60?? >
                N/A
              <#else>
                &lt; ${mInfo.latency._60[1]} ms
              </#if>
            </td>
            </#if>
            <#if _80rt!false >
            <td>
              <#if !mInfo.latency._80?? >
                N/A
              <#else>
                &lt; ${mInfo.latency._80[1]} ms
              </#if>
            </td>
            </#if>
            <#if _90rt!false >
            <td>
              <#if !mInfo.latency._90?? >
                N/A
              <#else>
                &lt; ${mInfo.latency._90[1]} ms
              </#if>
            </td>
            </#if>
            <#if _95rt!false >
            <td>
              <#if !mInfo.latency._95?? >
                N/A
              <#else>
                &lt; ${mInfo.latency._95[1]} ms
              </#if>
            </td>
            </#if>
            <#if _99rt!false >
            <td>
              <#if !mInfo.latency._99?? >
                N/A
              <#else>
                &lt; ${mInfo.latency._99[1]} ms
              </#if>
            </td>
            </#if>
            <#if _100rt!false >
            <td>
              <#if !mInfo.latency._100?? >
                N/A
              <#else>
                &lt; ${mInfo.latency._100[1]} ms
              </#if>
            </td>
            </#if>
            <#if allMetrics!false || pt!false >
            <td>
              <#assign procTime = mInfo.avgResTime - mInfo.avgXferTime>
              <#if procTime == 0>
                N/A
              <#else>
                ${procTime?string("0.##")} ms
              </#if>
            </td>
            </#if>
            <#if allMetrics!false || t!false >
            <td>${mInfo.throughput?string("0.##")} op/s</td>
            </#if>
            <#if allMetrics!false || bw!false >
            <td>
              <#assign width = mInfo.bandwidth >
              <#if (width > 1000) >
                <#assign width = width / 1000 >
                <#if (width > 1000) >
                  <#assign width = width / 1000 >
                  <#if (width > 1000) >
                    <#assign width = width / 1000 >
                    ${width?string("0.##")} GB/S
                  <#else>
                    ${width?string("0.##")} MB/S
                  </#if>
                <#else>
                  ${width?string("0.##")} KB/S
                </#if>
              <#else>
                ${width?string("0.##")} B/S
              </#if>
            </td>
            </#if>
            <#if allMetrics!false || succ!false >
            <td>
              <#if mInfo.totalSampleCount == 0 >
                N/A
              <#else>
                <#assign sRatio = mInfo.ratio >
                ${sRatio?string("0.##%")}
              </#if>
            </td>
            </#if>
            <#if cfg!false >
            <td>
              ${mInfo.workerCount} wkrs,
              <#list sInfo.stage.works as wInfo >
                <#list wInfo.operations as oInfo >
                  <#if oInfo.type == mInfo.opType >
                    ${oInfo.ratio}%,
                    <#list oInfo.config?split(";") as item >
  			          ${item}<#if item_has_next >, </#if>
                    </#list>                
                  </#if>
                </#list>
              </#list>
            </td>
            </#if>
            <td><a href="stage.html?wid=${hInfo.id}&sid=${sInfo.id}">view details</a></td>
          </#if>
          </tr>
        </#list>
      </#list>
    </#list>
  </table>
  <p>
    <a href="matrix.html?type=${type}&ops=read&ops=write&ops=delete&metrics=rt&rthisto=_95rt&others=cfg">show RT only</a>
    <a href="matrix.html?type=${type}&ops=read&ops=write&ops=delete&metrics=t&others=cfg">show T only</a>
    <a href="matrix.html?type=${type}&ops=read&ops=write&ops=delete&metrics=bw&others=cfg">show BW only</a>
  </p>
  <p><a href="index.html">go back to index</a></p>
</div> <#-- end of content -->
<div class="bottom"><br /></div>
</div> <#-- end of main -->
<#include "footer.ftl">
</body>
</html>