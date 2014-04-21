<h4>General Report</h4>
<table class="info-table">
  <tr>
    <th class="id" style="width:13%;">Op-Type</th>
    <th>Op-Count</th>
    <th>Byte-Count</th>
    <th>Avg-ResTime</th>
    <th>Avg-ProcTime</th>
    <th>Throughput</th>
    <th>Bandwidth</th>
    <th>Succ-Ratio</th>
  </tr>
  <#list allMetrics as mInfo>
    <tr>
      <td>${mInfo.opName}<#if mInfo.opName != mInfo.sampleType>-${mInfo.sampleType}</#if></td>
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
      <td>
        <#if mInfo.avgResTime == 0>
          N/A
        <#else>
          ${mInfo.avgResTime?string("0.##")} ms
        </#if>
      </td>
      <td>
        <#assign procTime = mInfo.avgResTime - mInfo.avgXferTime>
        <#if procTime == 0>
          N/A
        <#else>
          ${procTime?string("0.##")} ms
        </#if>
      </td>
      <td>${mInfo.throughput?string("0.##")} op/s</td>
      <td>
        <#assign bw = mInfo.bandwidth >
        <#if (bw > 1000) >
          <#assign bw = bw / 1000 >
          <#if (bw > 1000) >
            <#assign bw = bw / 1000 >
            <#if (bw > 1000) >
              <#assign bw = bw / 1000 >
              ${bw?string("0.##")} GB/S
            <#else>
              ${bw?string("0.##")} MB/S
            </#if>
          <#else>
            ${bw?string("0.##")} KB/S
          </#if>
        <#else>
          ${bw?string("0.##")} B/S
        </#if>
      </td>
      <td>
        <#if mInfo.totalSampleCount == 0 >
          N/A
        <#else>
          <#assign sRatio = mInfo.ratio >
          ${sRatio?string("0.##%")}
        </#if>
      </td>
    </tr>
  </#list>
</table>
<#if perfDetails >
<h4>ResTime (RT) Details</h4>
<table class="info-table">
  <tr>
    <th class="id" style="width:13%;">Op-Type</th>
    <th>60%-RT</th>
    <th>80%-RT</th>
    <th>90%-RT</th>
    <th>95%-RT</th>
    <th>99%-RT</th>
    <th>100%-RT</th>
  </tr>
  <#list allMetrics as mInfo>
    <tr>
      <td>${mInfo.opName}<#if mInfo.opName != mInfo.sampleType>-${mInfo.sampleType}</#if></td>
      <td>
        <#if !mInfo.latency._60?? >
          N/A
        <#else>
          &lt; ${mInfo.latency._60[1]} ms
        </#if>
      </td>
      <td>
        <#if !mInfo.latency._80?? >
          N/A
        <#else>
          &lt; ${mInfo.latency._80[1]} ms
        </#if>
      </td>
      <td>
        <#if !mInfo.latency._90?? >
          N/A
        <#else>
          &lt; ${mInfo.latency._90[1]} ms
        </#if>
      </td>
      <td>
        <#if !mInfo.latency._95?? >
          N/A
        <#else>
          &lt; ${mInfo.latency._95[1]} ms
        </#if>
      </td>
      <td>
        <#if !mInfo.latency._99?? >
          N/A
        <#else>
          &lt; ${mInfo.latency._99[1]} ms
        </#if>
      </td>
      <td>
        <#if !mInfo.latency._100?? >
          N/A
        <#else>
          &lt; ${mInfo.latency._100[1]} ms
        </#if>
      </td>
    </tr>
  </#list>
</table>
</#if>