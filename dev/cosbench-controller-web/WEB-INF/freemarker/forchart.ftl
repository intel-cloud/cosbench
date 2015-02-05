
    <script type="text/javascript">
    function forchart(xvalue,yvalue,name,sp,stagename){
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
                    legend: {
                        data:[name]
                    },
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
                    series : [
                        {
                            name:name,
                            type:'line',
                            data:yvalue,
                            markPoint : {
                                data : [
                                    {type : 'max', name: 'max'},
                                    {type : 'min', name: 'min'}
                                ]
                            },
                            markLine : {
                                data : [
                                   {type : 'average', name: 'average'}
                                ]
                            }
                        }
                    ]
                    };
                myChart.setOption(option); 
            }
        );
    }
</script>