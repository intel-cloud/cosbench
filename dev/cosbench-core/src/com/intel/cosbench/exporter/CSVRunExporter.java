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

package com.intel.cosbench.exporter;

import static com.intel.cosbench.exporter.Formats.DATETIME;

import java.io.*;
import java.util.Date;

import com.intel.cosbench.model.StateInfo;
import com.intel.cosbench.model.WorkloadInfo;
import com.intel.cosbench.model.WorkloadState;

/**
 * This class is to export run information into CSV format.
 * 
 * @author ywang19, qzheng7
 *
 */
class CSVRunExporter extends AbstractRunExporter {

    public CSVRunExporter() {
        /* empty */
    }

    protected void writeHeader(Writer writer) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Id").append(',');
        buffer.append("Name").append(',');
        buffer.append("Submitted-At").append(',');
        buffer.append("Started-At").append(',');
        buffer.append("Stopped-At").append(',');
        buffer.append("Op-Info").append(',');
        buffer.append("State").append(',');
        buffer.append("Detailed State").append('\n');
        writer.write(buffer.toString());
    }

    protected void writeWorkload(Writer writer) throws IOException {
        StringBuilder buffer = new StringBuilder();
        buffer.append(workload.getId()).append(',');
        buffer.append(workload.getWorkload().getName()).append(',');
        appendDate(buffer, workload.getSubmitDate());
        appendDate(buffer, workload.getStartDate());
        appendDate(buffer, workload.getStopDate());
        appendOperations(buffer, workload.getAllOperations());
        appendState(buffer, workload.getState());
        appendDetailedState(buffer, workload);
        buffer.setCharAt(buffer.length() - 1, '\n');
        writer.write(buffer.toString());
    }
    
    private static void appendDetailedState(StringBuilder buffer, WorkloadInfo workload) {
        for(StateInfo state : workload.getStateHistory()) {
        	String detailedState = state.getName().toLowerCase() + " @ " + DATETIME.format(state.getDate());
        	buffer.append(detailedState).append(',');
        }
    }

    private static void appendDate(StringBuilder buffer, Date date) {
        if (date != null)
            buffer.append(DATETIME.format(date));
        buffer.append(',');
    }

    private static void appendOperations(StringBuilder buffer, String[] ops) {
        for (String op : ops)
            buffer.append(op).append(' ');
        if (buffer.charAt(buffer.length() - 1) == ' ')
            buffer.deleteCharAt(buffer.length() - 1);
        buffer.append(',');
    }

    private static void appendState(StringBuilder buffer, WorkloadState state) {
        buffer.append(state.toString().toLowerCase()).append(',');
    }

}
