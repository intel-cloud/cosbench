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

import com.intel.cosbench.utils.MapRegistry;

/**
 * This class is a list of mark, it represents all marks at different time point.
 * 
 * @author ywang19, qzheng7
 * @see com.intel.cosbench.bench.Metrics
 *
 */
public class Status extends MapRegistry<Mark> {

    public void addMark(Mark mark) {
        addItem(mark);
    }

    public Mark getMark(String name) {
        return getItem(name);
    }

    public Mark[] getAllMarks() {
        return getAllItems().toArray(new Mark[getSize()]);
    }

}
