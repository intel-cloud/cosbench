
    <script type="text/javascript">
    function forchart(xvalue,yvalue,name,sp,stagename,axis){
        var seriesdate="[";
        for(var i=0;i<yvalue.length;i++){
            seriesdate+="{";
            seriesdate+="\"name\":\""+axis[i]+"\",";
            seriesdate+="\"type\":\"line\",";
            seriesdate+="\"data\":["+yvalue[i].toString()+"],";
            seriesdate+="\"markPoint\":{\"data\":[{\"type\":\"max\",\"name\":\"max\"},{\"type\":\"min\",\"name\":\"min\"}]},";
            seriesdate+="\"markLine\":{\"data\":[{\"type\":\"average\",\"name\":\"average\"}]}";
            seriesdate+="},";
           
        }
        seriesdate=seriesdate.substring(0,seriesdate.length-1);
        seriesdate+="]";
        var jsdata=eval(seriesdate);
       
        var axistitle="{\"data\":[";
        for(var j=0;j<axis.length;j++){

            axistitle+="\""+axis[j]+"\","
        }
        axistitle=axistitle.substring(0,axistitle.length-1);
        axistitle+="]}";
        var titlename = JSON.parse(axistitle); 

        require.config({
            paths: {
                 echarts: 'resources/build/dist'
            }
        });
        require(
            [
                'echarts',
                'echarts/chart/bar',
                'echarts/chart/line'
            ],
            function (ec) {
                var myChart = ec.init(document.getElementById(name)); 
                option = {
                    title : {
                        text: name+' '+'Graph',
                        subtext: stagename
                    },
                    tooltip : {
                        trigger: 'axis'
                    },
                    legend : titlename,
                    toolbox: {
                        show : true,
                        feature : {
                            mark : {show: true},
                            dataView : {show: true, readOnly: false},
                            magicType : {show: true, type: ['line', 'bar']},
                            restore : {show: true},
                            saveAsImage : {show: true}
                        }
                    },
                    calculable : true,
                    xAxis : [
                        {
                            type : 'category',
                            boundaryGap : false,
                            data : xvalue
                        }
                    ],
                    yAxis : [
                        {
                            type : 'value',
                            axisLabel : {
                                formatter: '{value} '+sp
                            }
                        }
                    ],
                    series:jsdata
                }
                myChart.setOption(option); 
            }
        );
    }
</script>