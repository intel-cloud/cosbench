/** 
 
Copyright 2013 Intel Corporation, All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
*/ 

package com.intel.cosbench.bench;

import java.util.*;


/**
 * This class encapsulates operations needed to merge metrics into report.
 * 
 * @author ywang19, qzheng7
 *
 */
public class ReportMerger {

    /* metrics types */
    private Set<String> types = new LinkedHashSet<String>();

    /* children reports */
    private List<Report> children = new ArrayList<Report>();

    public ReportMerger() {
        /* empty */
    }

    public void add(Report report) {
        for (Metrics metrics : report)
            types.add(metrics.getName());
        children.add(report);
    }

    public Report merge() {
        Report report = new Report();
        for (String type : types)
            report.addMetrics(getMetrics(type));
        return report;
    }

    private Metrics getMetrics(String type) {
        Metrics metrics;
        Aggregator aggregator = new Aggregator(type);
        for (Report report : children)
            if ((metrics = report.getMetrics(type)) != null)
                aggregator.addMetrics(metrics);
        metrics = aggregator.aggregate();
        return metrics;
    }

}
