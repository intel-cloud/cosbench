package com.intel.cosbench.controller.service;

import java.util.concurrent.FutureTask;

class OrderFuture<T> extends FutureTask<T> {

	private int priority;
	
	public OrderFuture(Runnable runnable, T value, int priority) {
		super(runnable, value);
		this.priority = priority;	
	}

	public int getPriority() {
		return priority;
	}
}
