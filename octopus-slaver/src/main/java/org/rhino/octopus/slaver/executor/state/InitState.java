package org.rhino.octopus.slaver.executor.state;

import java.util.List;

import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.flow.FlowLog;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.JobExecutor;
import org.rhino.octopus.slaver.model.JobLog;
import org.rhino.octopus.slaver.service.FlowLogService;


public class InitState implements IState{

	
	private Controller controller;
	
	public InitState(Controller controller){
		this.controller = controller;
	}
	
	@Override
	public void execute() throws OctopusException {
		
		FlowLogService svc = (FlowLogService)SlaverContext.getInstance().getBean("flowLogService");
		int runningNum = svc.queryRunningLogNum(this.controller.getContext().getFlow().getId());
		if(runningNum > 0){
			controller.setState(null);
		}else{
			FlowLog flowLog = new FlowLog();
			svc.saveFlowLog(flowLog);
			this.controller.getContext().setFlowLog(flowLog);
			this.initContext();
			this.controller.setState(new StartJobState(this.controller));
		}
	}
	
	private void initContext() throws OctopusException{
		
		FlowExecContext execContext = this.controller.getContext();
		execContext.getLineList().addAll(execContext.getFlow().getLineList());
		List<JobProperties> jobPropList = execContext.getFlow().getJobList();
		for(int i = 0, len = jobPropList.size(); i < len; i++){
			JobProperties jobProp = jobPropList.get(i);
			try {
				JobLog jobLog = new JobLog();
				jobLog.setFlowId(execContext.getFlow().getId());
				jobLog.setFlowRuntimeId(execContext.getFlowLog().getRuntimeId());
				jobLog.setJobId(jobProp.getId());
				JobExecutor jobExecutor = new JobExecutor(jobProp, jobLog);				
				execContext.getWaitingList().add(jobExecutor);
				execContext.getAllList().add(jobExecutor);
			} catch (Exception e) {
				throw new OctopusException(e);
			} 
		}
	}

}
