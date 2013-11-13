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

package com.intel.cosbench.model;

import java.util.Date;

import com.intel.cosbench.utils.ListRegistry;

public class StateRegistry extends ListRegistry<StateInfo> {

    private static class StateItem implements StateInfo {

        private String name;
        private Date date;

        public StateItem(String name, Date date) {
            this.name = name;
            this.date = date;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Date getDate() {
            return date;
        }

    }

    public void addState(String name) {
        addItem(new StateItem(name, new Date()));
    }
    
    public void addState(String name, Date date) {
    	addItem(new StateItem(name, date));
    }

    public StateInfo getState(int index) {
        return getItem(index);
    }

    public StateInfo[] getAllStates() {
        return getAllItems().toArray(new StateInfo[getSize()]);
    }

}
