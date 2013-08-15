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
