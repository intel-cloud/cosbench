package com.intel.cosbench.controller.service;

class ControllerThread implements Runnable {
	private int priority;
	private WorkloadProcessor processor;

	public ControllerThread(WorkloadProcessor processor) {
		this.processor = processor;
		setPriority(processor.getWorkloadContext().getPriority());
	}

	@Override
	public void run() {
		processor.process();
		processor.getWorkloadContext().setFuture(null);
	}

	public int getPriority() {
		return this.priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}