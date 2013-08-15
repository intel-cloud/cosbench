package com.intel.cosbench.controller.service;

class ControllerThread implements Runnable {
	private int order;
	private WorkloadProcessor processor;

	public ControllerThread(WorkloadProcessor processor) {
		this.processor = processor;
		setOrder(processor.getWorkloadContext().getOrder());
	}

	@Override
	public void run() {
		processor.process();
		processor.getWorkloadContext().setFuture(null);
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}