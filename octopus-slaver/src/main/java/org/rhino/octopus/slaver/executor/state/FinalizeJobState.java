package org.rhino.octopus.slaver.executor.state;

import java.util.List;

import org.rhino.octopus.base.constants.FlowRuntimeStatus;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.executor.JobExecutor;

public class FinalizeJobState implements IState {

	
	private Controller controller;
	
	public FinalizeJobState(Controller controller){
		this.controller = controller;
	}
	
	@Override
	public void execute()throws OctopusException {
		
		boolean hasFatal = false;
		
		List<JobExecutor> failedList = this.controller.getContext().getFailedList();
		for(int i = 0, len = failedList.size(); i < len; i++){
			JobExecutor failedExecutor = failedList.get(i);
			if(failedExecutor.getJob().isFatal()){
				hasFatal = true;
				break;
			}
		}
		
		if(hasFatal == false){
			this.controller.setState(new StartJobState(this.controller));
		}else{
			this.controller.getContext().getFlowLog().setStatus(FlowRuntimeStatus.FAIL.getCode());
			this.controller.setState(new StopState(this.controller));
		}
	}

}
