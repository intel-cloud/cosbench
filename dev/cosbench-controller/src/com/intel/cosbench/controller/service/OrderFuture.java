package com.intel.cosbench.controller.service;

import java.util.concurrent.FutureTask;

class OrderFuture<T> extends FutureTask<T> {

	private int order;
	
	public OrderFuture(Runnable runnable, T value, int order) {
		super(runnable, value);
		this.order = order;	
	}

	public int getOrder() {
		return order;
	}
}
