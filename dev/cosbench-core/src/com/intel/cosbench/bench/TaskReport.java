package com.intel.cosbench.bench;
/**
 * The class is the data structure of task info
 * 
 * @author liyuan 
 *
 */
public class TaskReport {
	private String driverName;
	private String driverUrl;
	private Report report;
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	public String getDriverUrl() {
		return driverUrl;
	}
	public void setDriverUrl(String driverUrl) {
		this.driverUrl = driverUrl;
	}
	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
	
}
