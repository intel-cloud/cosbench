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

/**
 * This class encapsulates operations needed to merge report to snapshot.
 * 
 * @author ywang19, qzheng7
 *
 */
public class SnapshotMerger {

    private int count;
    private double versionSum = 0D;

    private int minVersion = Integer.MAX_VALUE;
    private int maxVersion = Integer.MIN_VALUE;

    private ReportMerger merger = new ReportMerger();

    public SnapshotMerger() {
        /* empty */
    }

    public void add(Snapshot snapshot) {
        int version = snapshot.getVersion();
        if (version < minVersion)
            minVersion = version;
        if (version > maxVersion)
            maxVersion = version;
        count++;
        versionSum += version;
        merger.add(snapshot.getReport());
    }

    public Snapshot merge() {
        Report report = merger.merge();
        Snapshot snapshot = new Snapshot(report);
        snapshot.setVersion(getMidVersion());
        snapshot.setMinVersion(minVersion);
        snapshot.setMaxVersion(maxVersion);
        return snapshot;
    }

    private int getMidVersion() {
        return (count == 0) ? 0 : (int) (versionSum / count);
    }

}
