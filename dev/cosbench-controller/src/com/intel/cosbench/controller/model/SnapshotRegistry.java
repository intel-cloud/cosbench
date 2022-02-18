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

package com.intel.cosbench.controller.model;

import com.intel.cosbench.bench.Snapshot;
import com.intel.cosbench.utils.ListRegistry;

public class SnapshotRegistry extends ListRegistry<Snapshot> {

    public void addSnapshot(Snapshot snapshot) {
        addItem(snapshot);
    }

    public Snapshot[] getAllSnapshot() {
        return getAllItems().toArray(new Snapshot[getSize()]);
    }

}
