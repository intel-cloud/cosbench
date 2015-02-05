   <script type="text/javascript">
      function custage(){
        var d=document.getElementById("cstage").value;
        var element = document.getElementById("fchart")
            element.style.display ="";
        if(d=="close"){
             element.style.display ="none";
             return;
        }   
        <#list info.stageInfos as sInfo >
            var id="${sInfo.id}";
            if(id==d){
            var i=0;
            //bandwidth
            var ybandwidth=new Array();
            var xvalue=new Array();
            var bandwidthname="bandwidth";
            var bandwidthsp="KB/S";
            var bandwidth="";
            //throughput
            var ythroughput=new Array();
            var throughputname="throughput";
            var throughputsp="op/s";
            var throughput="";
            //restime
            var yRestime=new Array();
            var avgResTime="";
            var avgResTimename="resTime";
            var avgResTimesp="ms";

            //ratio
            var yratio=new Array();
            var rationame="ratio";
            var ratiosp="%";
            var ratio="";

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
                xvalue.push(i);
                i++;
                </#list>
            </#list>
            forchart(xvalue,ybandwidth,bandwidthname,bandwidthsp,id);
            forchart(xvalue,ythroughput,throughputname,throughputsp,id);
            forchart(xvalue,yRestime,avgResTimename,avgResTimesp,id);
            forchart(xvalue,yratio,rationame,ratiosp,id);
         }
    </#list> 
}
</script>