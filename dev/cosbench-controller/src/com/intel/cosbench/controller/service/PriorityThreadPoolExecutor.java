package com.intel.cosbench.controller.service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityThreadPoolExecutor extends ThreadPoolExecutor {

	public PriorityThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
			long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
		return new PriorityFuture<T>(runnable, null,
				((ControllerThread) runnable).getPriority());
	}

	public Future<?> submit(Runnable task) {
		if (task == null)
			throw new NullPointerException();
		RunnableFuture<Object> ftask = newTaskFor(task, null);
		execute(ftask);
		return ftask;
	}
}
