package org.rhino.octopus.slaver.executor.state;

import java.util.ArrayList;
import java.util.List;

import org.rhino.octopus.base.constants.JobRuntimeStatus;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.JobExecutor;
import org.rhino.octopus.slaver.service.JobLogService;
import org.springframework.util.CollectionUtils;

public class JudgeRunningJobState implements IState {

	private Controller controller;
	
	public JudgeRunningJobState(Controller controller){
		this.controller = controller;
	}
	
	@Override
	public void execute() throws OctopusException{
		List<JobExecutor> executingList = this.controller.getContext().getExecutingList();
		
		List<JobExecutor> finishList = new ArrayList<JobExecutor>();
		for(int i = executingList.size() - 1; i >= 0; i--){
			JobExecutor jobExecutor = executingList.get(i);
			if(this.isFinished(jobExecutor)){
				finishList.add(jobExecutor);
				executingList.remove(i);
				JobLogService logSvc = (JobLogService)SlaverContext.getInstance().getBean("jobLogService");
				logSvc.saveJobLog(jobExecutor.getJobLog());
			}
		}
		
		
		if(CollectionUtils.isEmpty(finishList) == false){
			for(int i = 0, len = finishList.size(); i < len; i++){
				JobExecutor jobExecutor = finishList.get(i);
				if(jobExecutor.getJobLog().getStatusCode().equals(JobRuntimeStatus.FAIL.getCode())){
					this.controller.getContext().getFailedList().add(jobExecutor);
				}else{
					this.controller.getContext().getSuccessList().add(jobExecutor);
				}
			}
			this.controller.setState(new FinalizeJobState(this.controller));
		}else{
			try {Thread.sleep(1000L);} catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	private boolean isFinished(JobExecutor jobExecutor){
		String statusCode = jobExecutor.getJobLog().getStatusCode();
		return JobRuntimeStatus.FAIL.getCode().equals(statusCode) || JobRuntimeStatus.SUCCESS.getCode().equals(statusCode);
	}

}
