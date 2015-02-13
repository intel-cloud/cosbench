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
            var xvalue=new Array();
            var yvalue = new Array();
            var axis=new Array();

            //bandwidth
            var ybandread=new Array();
            var ybandwrite=new Array();
            var ybanddelete=new Array();
            var bandwidth;
   
            //throughput
            var ytputread=new Array();
            var ytputwrite=new Array();
            var ytputdelete=new Array();
            var sampletype;
            var throughput;

            //restime
            var yrtimeread=new Array();
            var yrtimewrite=new Array();
            var yrtimedelete=new Array();
            var avgResTime;

            //ratio
            var yraread=new Array();
            var yrawrite=new Array();
            var yradelete=new Array();
            var ratio;

            <#list sInfo.snapshots as snapshot>
               <#list snapshot.report.allMetrics as mInfo>
            // bandwidth
                bandwidth="${mInfo.bandwidth}";
                bandwidth=bandwidth.replace(/,/gi,'');

            //resTime
                avgResTime="${mInfo.avgResTime}";
                avgResTime=avgResTime.replace(/,/gi,'');
              
            //Succ-Ratio
                ratio="${mInfo.ratio}";
                ratio=ratio.replace(/,/gi,'');

            // throughput
                sampletype="${mInfo.sampleType}";
                throughput="${mInfo.throughput}";
                throughput=throughput.replace(/,/gi,'');
                if(sampletype=="read"){
                    ytputread.push(Math.round(eval(throughput)));
                    ybandread.push(Math.round(eval(bandwidth/1024)));
                    yrtimeread.push(Math.round(eval(avgResTime)));
                    yraread.push(Math.round(eval(ratio*100)));
                }else if(sampletype=="write"){
                    ytputwrite.push(Math.round(eval(throughput)));
                    ybandwrite.push(Math.round(eval(bandwidth/1024)));
                    yrtimewrite.push(Math.round(eval(avgResTime)));
                    yrawrite.push(Math.round(eval(ratio*100)));
                }else if(sampletype=="delete"){
                    ytputdelete.push(Math.round(eval(throughput)));
                    ybanddelete.push(Math.round(eval(bandwidth/1024)));
                    yrtimedelete.push(Math.round(eval(avgResTime)));
                    yradelete.push(Math.round(eval(ratio*100)));
                }          
            //xvalue
                xvalue.push(i);
                i++;
                </#list>
            </#list>
            //restime
            yvalue.length=0;
            if(yrtimeread.length>0){
                yvalue.push(yrtimeread);
                axis.push("read");
            }if(yrtimewrite.length>0){
                yvalue.push(yrtimewrite);
                axis.push("write");
            }if(yrtimedelete.length>0){
                yvalue.push(yrtimedelete);
                axis.push("delete");
            }
            xvalue.length=yvalue[0].length;
            forchart(xvalue,yvalue,"resTime","sp",id,axis);
            //ratio
            yvalue.length=0;
            if(yraread.length>0){
                yvalue.push(yraread);
            }if(yrawrite.length>0){
                yvalue.push(yrawrite);
            }if(yradelete.length>0){
                yvalue.push(yradelete);
            }
            forchart(xvalue,yvalue,"ratio","%",id,axis);
            //bandwith
            yvalue.length=0;
            if(ybandread.length>0){
                yvalue.push(ybandread);
            }if(ybandwrite.length>0){
                yvalue.push(ybandwrite);
            }if(ybanddelete.length>0){
                yvalue.push(ybanddelete);
            }
            forchart(xvalue,yvalue,"bandwidth","KB/S",id,axis);
            //throughput
            yvalue.length=0;
            if(ytputread.length>0){
                yvalue.push(ytputread);
            }if(ytputwrite.length>0){
                yvalue.push(ytputwrite);
            }if(ytputdelete.length>0){
                yvalue.push(ytputdelete);
            }
            forchart(xvalue,yvalue,"throughput","op/s",id,axis);
         }
    </#list> 
}
</script>