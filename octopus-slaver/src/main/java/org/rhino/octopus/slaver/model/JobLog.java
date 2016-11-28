package org.rhino.octopus.slaver.model;

public class JobLog {

	private String flowId;
	
	private String flowRuntimeId;
	
	private String jobId;
	
	private String sysLog;
	
	private String errLog;
	
	private String statusCode;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getFlowRuntimeId() {
		return flowRuntimeId;
	}

	public void setFlowRuntimeId(String flowRuntimeId) {
		this.flowRuntimeId = flowRuntimeId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getSysLog() {
		return sysLog;
	}

	public void setSysLog(String sysLog) {
		this.sysLog = sysLog;
	}

	public String getErrLog() {
		return errLog;
	}

	public void setErrLog(String errLog) {
		this.errLog = errLog;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
