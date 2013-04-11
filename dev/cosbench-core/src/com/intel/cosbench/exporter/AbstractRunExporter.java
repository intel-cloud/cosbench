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

import java.io.*;

import com.intel.cosbench.model.WorkloadInfo;

/**
 * This class is the base class for exporting run information.
 * 
 * @author ywang19, qzheng7
 *
 */
abstract class AbstractRunExporter implements RunExporter {

    protected WorkloadInfo workload;

    public AbstractRunExporter() {
        /* empty */
    }

    public void setWorkload(WorkloadInfo workload) {
        this.workload = workload;
    }

    @Override
    public void init(Writer writer) throws IOException {
        writeHeader(writer);
        writer.flush();
    }

    protected abstract void writeHeader(Writer writer) throws IOException;

    @Override
    public void export(Writer writer) throws IOException {
        writeWorkload(writer);
        writer.flush();
    }

    protected abstract void writeWorkload(Writer writer) throws IOException;

}
