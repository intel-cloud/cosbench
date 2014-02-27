<table class="info-table">
  <#list allSnapshots as ssInfo >
    <#assign size = ssInfo.report.size >
    <tr>
      <th class="id" rowspan="2">Timestamp<br /><font size="-2">(Version)</font></th>
      <th colspan="${size}">Op-Count</th>
      <th colspan="${size}">Byte-Count</th>
      <th colspan="${size}">Avg-ResTime</th>
      <th colspan="${size}">Avg-ProcTime</th>
      <th colspan="${size}">Throughput</th>
      <th colspan="${size}">Bandwidth</th>
      <th colspan="${size}">Succ-Ratio</th>
    </tr>
    <tr>
      <#assign allMetrics = ssInfo.report.allMetrics >
      <#list allMetrics as mInfo >
        <td>${mInfo.opName}</td>
      </#list>
      <#list allMetrics as mInfo >
        <td>${mInfo.opName}</td>
      </#list>
      <#list allMetrics as mInfo >
        <td>${mInfo.opName}</td>
      </#list>
      <#list allMetrics as mInfo >
        <td>${mInfo.opName}</td>
      </#list>
      <#list allMetrics as mInfo >
        <td>${mInfo.opName}</td>
      </#list>
      <#list allMetrics as mInfo >
        <td>${mInfo.opName}</td>
      </#list>
    </tr>
    <#break>
  </#list>
  <#list allSnapshots as ssInfo >
    <tr>
      <td>${ssInfo.timestamp?time}<br />
      	<!--
      		<font size="-2">[${ssInfo.minVersion}, ${ssInfo.version}, ${ssInfo.maxVersion}]</font>
      	-->		
      </td>
      <#assign allMetrics = ssInfo.report.allMetrics >
      <#list allMetrics as mInfo >
        <td>
          <#assign op = mInfo.sampleCount >
          <#if (op > 1000) >
            <#assign op = op / 1000 >
            <#if (op > 1000) >
              <#assign op = op / 1000 >
              <#if (op > 1000) >
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
      </#list>
      <#list allMetrics as mInfo >
        <td>
          <#assign data = mInfo.byteCount >
          <#if (data > 1000) >
            <#assign data = data / 1000 >
            <#if (data > 1000) >
              <#assign data = data / 1000 >
              <#if (data > 1000) >
                <#assign data = data / 1000 >
                <#if (data > 1000) >
                  <#assign data = data / 1000 >
                  <#if (data > 1000) >
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
      </#list>
      <#list allMetrics as mInfo >
        <td>
          <#if mInfo.avgResTime == 0 >
            N/A
          <#else>
            ${mInfo.avgResTime?string("0.##")} ms</td>
          </#if>
      </#list>
      <#list allMetrics as mInfo >
        <td>
          <#assign procTime = mInfo.avgResTime - mInfo.avgXferTime>
          <#if procTime == 0 >
            N/A
          <#else>
            ${procTime?string("0.##")} ms</td>
          </#if>
      </#list>
      <#list allMetrics as mInfo >
        <td>${mInfo.throughput?string("0.##")} op/s</td>
      </#list>
      <#list allMetrics as mInfo >
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
      </#list>
      <#list allMetrics as mInfo >
        <td>
          <#if mInfo.totalSampleCount == 0 >
            N/A
          <#else>
            <#assign sRatio = mInfo.ratio >
            ${sRatio?string("0.##%")}
          </#if>
        </td>
      </#list>
    </tr>
  </#list>
</table>