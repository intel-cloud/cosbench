/**

Copyright 2019 OpenIO Corporation, All Rights Reserved.

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

/*
 *
 * PROMETHEUS EXPORTER
 *
 * @Author: Jérôme Loyet for OpenIO
 */
package com.intel.cosbench.controller.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.*;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.web.servlet.*;

import com.intel.cosbench.bench.Metrics;
import com.intel.cosbench.bench.Report;
import com.intel.cosbench.model.*;
import com.intel.cosbench.service.ControllerService;

public class PrometheusController extends IndexPageController {

  /*
   * Custom "inline" View dedicated for Prometheus output generation
   */
  private static final View PROMETHEUS = new PrometheusView();
  private static class PrometheusView implements View {

    /*
     * # Prometheus FORMAT
     * chart_name_with_underscores_separator{label1="value1",label2="value2"} 0.0000000 1546933475642
     * Followed by the value of the metric as a float
     * At the end, the timestamp can be added (Unix time in milliseconds)
     *
     * More at https://prometheus.io/docs/instrumenting/exposition_formats/
     */

    /* constants */
    private static final String PROM_SP          = " ";
    private static final String PROM_SEP         = "_";
    private static final String PROM_LINE_SEP    = "\n";
    private static final String PROM_LABEL_SEP   = ",";
    private static final String PROM_LABEL_QUOTE = "\"";
    private static final String PROM_LABEL_EQ    = "=";
    private static final String PROM_LABEL_START = "{";
    private static final String PROM_LABEL_END   = "}";
    private static final String PROM_PREFIX      = "cosbench" + PROM_SEP;

    /*
     * Output one metric with labels
     */
    private void printProm(PrintWriter writer, long date, float value, String name, Map<String, String> labels) {

      /* print chart name */
      writer.print(PROM_PREFIX + name);

      /* handle labels if present */
      if (labels != null) {

        Iterator<Map.Entry<String, String>> it = labels.entrySet().iterator();
        boolean first = true;

        /* if not labels list not empty */
        if (it.hasNext()) {

          writer.print(PROM_SP + PROM_LABEL_START);

          /* iterate through labels */
          while (it.hasNext()) {

            /* print PROM_LABL_SEP ',' only since the 2nd item */
            if (first) first = false;
            else writer.print(PROM_LABEL_SEP);

            /* extract next label */
            Map.Entry<String, String> label = (Map.Entry<String, String>) it.next();

            /* print the label */
            writer.print(label.getKey() + PROM_LABEL_EQ + PROM_LABEL_QUOTE + label.getValue() + PROM_LABEL_QUOTE);
          }

          writer.print(PROM_LABEL_END);
        }
      }

      /* handle printing the float value to ensure it uses only num and the dot characters */
      if (value == (long) value) {
        writer.print(PROM_SP + String.format("%d", (long)value));
      } else {
        writer.print(PROM_SP + String.format("%s", value));
      }

      /* print the date if present */
      if (date > 0) writer.print(PROM_SP + String.valueOf(date));

      /* End of the metric */
      writer.print(PROM_LINE_SEP);
    }

    /*
     *
     */
    private void printMetrics(Report report, HashMap<String, String> labels, PrintWriter writer) {
      printMetrics(report, labels, 0, writer);
    }

    /*
     * Print complete operation metrics, composed of
     *   - Operations count
     *   - Transfer Byte count
     *   - Average Response time
     *   - Average Transfer time
     *   - Average Process time (which is Response time - Transfer Time)
     *   - Throughput (which is the number of operations per seconds)
     *   - Bandwidth (which is the number transfer bytes per seconds)
     *   - Success Percentage
     */
    private void printMetrics(Report report, HashMap<String, String> labels, long time, PrintWriter writer) {
      /* duplicate labels for each workload */
      labels = (HashMap<String,String>)labels.clone();

      Metrics allMetrics[] = report.getAllMetrics();
      for (int i=0; i<allMetrics.length; i++) {
        Metrics metrics     = allMetrics[i];
        String opId         = metrics.getOpId();
        String opName       = metrics.getOpName();
        String opType       = metrics.getOpType();

        if (opId != null && opId.length() > 0) opName = opId + ":" + opName;
        labels.put("operation_name", opName);

        if (opType != null && opType.length() > 0) {
          labels.put("operation_type", opType);
        }

        labels.put("unit", "operation");
        printProm(writer, time, (float)metrics.getSampleCount(), "workload_ops_count", labels);

        labels.put("unit", "byte");
        printProm(writer, time, (float)metrics.getByteCount(), "workload_byte_count", labels);

        labels.put("unit", "ms");
        printProm(writer, time, (float)metrics.getAvgResTime(), "workload_average_response_time", labels);

        labels.put("unit", "ms");
        printProm(writer, time, (float)metrics.getAvgXferTime(), "workload_average_transfer_time", labels);

        labels.put("unit", "ms");
        printProm(writer, time, (float)metrics.getAvgResTime() - (long)metrics.getAvgXferTime(), "workload_average_process_time", labels);

        labels.put("unit", "op/s");
        printProm(writer, time, (float)metrics.getThroughput(), "workload_throughput", labels);

        labels.put("unit", "byte/s");
        printProm(writer, time, (float)metrics.getBandwidth(), "workload_bandwidth", labels);

        labels.put("unit", "%");
        printProm(writer, time, (float)(metrics.getRatio()*100), "workload_success_ratio", labels);
      }
    }

    /*
     * Return the Content-Type for prometheus
     *   force charset to prevent tomcat to add another
     */
    @Override
    public String getContentType() {
      return "text/plain; version=0.0.4; charset=utf-8";
    }

    /*
     * Render the View
     * Output statistics for Active and Historical Workloads by default
     * Archive workloads are not exported by default
     * change settings to :
     *   - prometheus.export.workloads.active
     *   - prometheus.export.workloads.historical
     *   - prometheus.export.workloads.archive
     *
     * By default statistics for loaded workloads are sent for 5 minutes after workload finished.
     * The time can be changed with the setting 'prometheus.export.timeout'.
     * Set it to 0 will disable this behavior and continue to send metrics until workload is loaded
     * Warning: this can overlap with the parameter 'prometheus.export.workloads.archive'
     */
    @Override
    public void render(Map<String, ?> model, HttpServletRequest req, HttpServletResponse res) throws Exception {

      /* get the controller */
      ControllerService controller = (ControllerService)model.get("controller");

      /* handle System properties */
      String includeActiveWorkloads = System.getProperty("prometheus.export.workloads.active");
      String includeHistoryWorkloads = System.getProperty("prometheus.export.workloads.history");
      String includeArchivedWorkloads = System.getProperty("prometheus.export.workloads.archived");
      int stopExporting;
      try {
          /* convert to int */
          stopExporting = Integer.parseInt(System.getProperty("prometheus.export.timeout"));
      } catch (Exception e) {
          /* default to 5 minutes*/
          stopExporting = 5;
      }

      /* handle workloads to include */
      WorkloadInfo workloads[] = new WorkloadInfo[0];
      if (includeActiveWorkloads == null || "true".equalsIgnoreCase(includeActiveWorkloads)) workloads = (WorkloadInfo[])ArrayUtils.addAll(workloads, controller.getActiveWorkloads());
      if (includeHistoryWorkloads == null || "true".equalsIgnoreCase(includeHistoryWorkloads)) workloads = (WorkloadInfo[])ArrayUtils.addAll(workloads, controller.getHistoryWorkloads());
      if ("true".equalsIgnoreCase(includeArchivedWorkloads)) workloads = (WorkloadInfo[])ArrayUtils.addAll(workloads, controller.getArchivedWorkloads());

      /* set Content-Type and get servlet Writer */
      res.setContentType(this.getContentType());
      PrintWriter writer = res.getWriter();

      /* setup default labels with current hostname */
      HashMap<String,String> labels = new HashMap<String, String>();
      labels.put("instance", InetAddress.getLocalHost().getHostName());

      /* iterate through each workloads */
      for (int i=0; i<workloads.length; i++) {

        /* get current workload */
        WorkloadInfo workload = workloads[i];

        /* stop sending statistics 'stopExporting' minutes after workload has stopped */
        if (stopExporting > 0 && WorkloadState.isStopped(workload.getState())) {
            if ((new Date()).getTime() > workload.getStopDate().getTime() + (1000 * 60 * stopExporting)) {
                continue;
            }
        }

        /* set workload id and name as label by default */
        labels.put("workload", workload.getId() + " (" + workload.getWorkload().getName() + ")");

        /* convert workload state to integer */
        float state;
        switch(workload.getState()) {
          case QUEUING:
            state = 0;
            break;
          case PROCESSING:
            state = 1;
            break;
          case FINISHED:
            state = 2;
            break;
          case FAILED:
            state = 3;
            break;
          case TERMINATED:
            state = 4;
            break;
          case CANCELLED:
            state = 5;
            break;
          default:
            state = -1;
            break;
        }

        /* output workload_state metric */
        printProm(writer, 0, state, "workload_state", labels);

        // no other stats until it starts running
        if (workload.getState() == WorkloadState.QUEUING) continue;

        /* get Report that contains all metrics
         * If the workload is running (state == PROCESSING) then the report is to be fetch from the last SnapShot
         * otherwise the workload is over and the report is to be taken directly
         */
        Report report;
        if (workload.getState() == WorkloadState.PROCESSING) {
            report = workload.getSnapshot().getReport();
        } else {
            report = workload.getReport();
        }

        /* Output all metrics */
        printMetrics(report, labels, writer);

      }

      /* print empty line */
      writer.print(PROM_LINE_SEP);
    }
  }

  /*
   * Controller main function
   */
  @Override
  protected ModelAndView process(HttpServletRequest req, HttpServletResponse res) throws IOException {

    /* instanciate custom "inline" View */
    ModelAndView result = new ModelAndView(PROMETHEUS);

    /* pass controller Object to View */
    result.addObject("controller", controller);

    return result;
  }
}
