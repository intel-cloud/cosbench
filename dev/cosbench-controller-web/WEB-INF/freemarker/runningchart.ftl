   <script type="text/javascript">
    //bandwidth
    var ybandwidth=new Array();
    var i=0;
        ybandwidth.push(0);
    var xvalue=new Array();
    var bandwidthname="bandwidth";
    var bandwidthsp="KB/S";
    var bandwidth="";
    //throughput
    var ythroughput=new Array();
        ythroughput.push(0);
    var throughputname="throughput";
    var throughputsp="op/s";
    var throughput="";
    //restime
    var yRestime=new Array();
        yRestime.push(0);
    var avgResTime="";
    var avgResTimename="resTime";
    var avgResTimesp="ms";

    //ratio
    var yratio=new Array();
        yratio.push(0);
    var rationame="ratio";
    var ratiosp="%";
    var ratio="";

    <#assign  sInfo=info.currentStage>
     var stagename="${sInfo.id}";
      <#list sInfo.snapshots as snapshot>
        <#list snapshot.report.allMetrics as mInfo>
           // bandwidth
            bandwidth="${mInfo.bandwidth}";
            bandwidth=bandwidth.replace(/,/gi,'');
            ybandwidth.push(Math.round(eval(bandwidth/1024)));
        // throughput
            throughput="${mInfo.throughput}";
            throughput=throughput.replace(/,/gi,'');
            ythroughput.push(Math.round(eval(throughput)));
        //resTime
            avgResTime="${mInfo.avgResTime}";
            avgResTime=avgResTime.replace(/,/gi,'');
            yRestime.push(Math.round(eval(avgResTime)));

        //Succ-Ratio
            ratio="${mInfo.ratio}";
            ratio=ratio.replace(/,/gi,'');
            yratio.push(Math.round(eval(ratio*100)));
        //xvalue
            var now = new Date();
            xvalue.push(i);
            i++;
        </#list>
    </#list>
    forchart(xvalue,ybandwidth,bandwidthname,bandwidthsp,stagename);
    forchart(xvalue,ythroughput,throughputname,throughputsp,stagename);
    forchart(xvalue,yRestime,avgResTimename,avgResTimesp,stagename);
    forchart(xvalue,yratio,rationame,ratiosp,stagename);
</script>