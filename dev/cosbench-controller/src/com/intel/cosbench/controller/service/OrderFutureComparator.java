/**

Copyright 2013 Intel Corporation, All Rights Reserved.
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
package com.intel.cosbench.controller.service;

import java.util.Comparator;

class OrderFutureComparator implements Comparator<Runnable> {
    public int compare(Runnable o1, Runnable o2) {
        if (o1 == null && o2 == null)
            return 0;
        else if (o1 == null)
            return -1;
        else if (o2 == null)
            return 1;
        else {
            int p1 = ((OrderFuture<?>) o1).getOrder();
            int p2 = ((OrderFuture<?>) o2).getOrder();

            return p1 > p2 ? 1 : (p1 == p2 ? 0 : -1);
        }
    }
}
