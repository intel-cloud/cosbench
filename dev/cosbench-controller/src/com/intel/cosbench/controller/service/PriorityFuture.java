package com.intel.cosbench.controller.service;

import java.util.concurrent.FutureTask;

class PriorityFuture<T> extends FutureTask<T> {

	private int priority;
	
	public PriorityFuture(Runnable runnable, T value, int priority) {
		super(runnable, value);
		this.priority = priority;	
	}

	public int getPriority() {
		return priority;
	}
}
