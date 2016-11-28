package org.rhino.octopus.slaver.executor.state;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.rhino.octopus.base.constants.FlowRuntimeStatus;
import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.base.model.flow.FlowLog;
import org.rhino.octopus.base.model.line.Line;
import org.rhino.octopus.slaver.executor.JobExecutor;

/**
 * Flow执行时使用的上下文对象
 * @author 王铁
 */
public class FlowExecContext {
	
	private FlowLog flowLog;

	private Flow flow;
	
	/**
	 * 等待执行列表
	 */
	private List<JobExecutor> waitingList;
	
	/**
	 * 执行中列表
	 */
	private List<JobExecutor> executingList;
	
	/**
	 * 成功列表
	 */
	private List<JobExecutor> successList;
	
	/**
	 * 失败列表
	 */
	private List<JobExecutor> failedList;
	
	/**
	 * 全部列表
	 */
	private List<JobExecutor> allList;
	
	
	/**
	 * 无需执行列表
	 */
	private List<JobExecutor> noNeedList;
	
	/**
	 * 关联关系列表
	 */
	private List<Line> lineList;
	
	
	
	public FlowExecContext(Flow flow){
		this.flow = flow;
		this.flowLog = new FlowLog();
		this.flowLog.setFlowId(this.flow.getId());
		this.flowLog.setFlowName(this.flow.getName());
		this.flowLog.setRuntimeId(UUID.randomUUID().toString());
		this.flowLog.setStatus(FlowRuntimeStatus.RUNNING.getCode());
		this.waitingList = new ArrayList<JobExecutor>();
		this.executingList = new ArrayList<JobExecutor>();
		this.successList = new ArrayList<JobExecutor>();
		this.failedList = new ArrayList<JobExecutor>();
		this.allList = new ArrayList<JobExecutor>();
		this.noNeedList = new ArrayList<JobExecutor>();
		this.lineList = new ArrayList<Line>();
	}
	

	public List<JobExecutor> getWaitingList() {
		return waitingList;
	}


	public List<JobExecutor> getExecutingList() {
		return executingList;
	}
	

	public List<JobExecutor> getSuccessList() {
		return successList;
	}


	public List<JobExecutor> getFailedList() {
		return failedList;
	}


	public List<Line> getLineList() {
		return lineList;
	}

	public Flow getFlow() {
		return flow;
	}


	public FlowLog getFlowLog() {
		return flowLog;
	}


	public void setFlowLog(FlowLog flowLog) {
		this.flowLog = flowLog;
	}


	public List<JobExecutor> getAllList() {
		return allList;
	}


	public List<JobExecutor> getNoNeedList() {
		return noNeedList;
	}
}
