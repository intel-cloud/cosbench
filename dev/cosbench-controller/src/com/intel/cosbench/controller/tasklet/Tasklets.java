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

package com.intel.cosbench.controller.tasklet;

import java.util.*;

import com.intel.cosbench.controller.model.*;

public class Tasklets {

    public static List<Tasklet> newBooters(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Bootor(task));
        }
        return result;
    }

    public static List<Tasklet> newSubmitters(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Submitter(task));
        }
        return result;
    }

    public static List<Tasklet> newAuthenticators(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Authenticator(task));
        }
        return result;
    }

    public static List<Tasklet> newLaunchers(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Launcher(task));
        }
        return result;
    }

    public static List<Tasklet> newQueriers(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Querier(task));
        }
        return result;
    }

    public static List<Tasklet> newClosers(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Closer(task));
        }
        return result;
    }

    public static List<Tasklet> newAborters(TaskRegistry tasks) {
        List<Tasklet> result = new ArrayList<Tasklet>();
        for (TaskContext task : tasks) {
            result.add(new Aborter(task));
        }
        return result;
    }
    
    public static List<Tasklet> newTriggers(String trigger, DriverRegistry registry, boolean option, String wsId) {
    	List<Tasklet> result = new ArrayList<Tasklet>();
        for (DriverContext driver : registry) {
            result.add(new Trigger(driver, trigger, option, wsId));
        }
        return result;
	}

}
