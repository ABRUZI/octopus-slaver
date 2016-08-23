package org.rhino.octopus.slaver.executor;

import org.rhino.octopus.base.constants.FlowRuntimeStatus;
import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.state.Controller;
import org.rhino.octopus.slaver.executor.state.FlowExecContext;
import org.rhino.octopus.slaver.executor.state.InitState;
import org.rhino.octopus.slaver.service.FlowLogService;

public class FlowExecutor implements Runnable{

	
	private Flow flow;
	
	public FlowExecutor(Flow flow){
		this.flow = flow;
	}
	
	@Override
	public void run() {
		FlowExecContext context = new FlowExecContext(this.flow);
		FlowLogService flowLogService = null;
		try {
			flowLogService = (FlowLogService)SlaverContext.getInstance().getBean("flowLogService");
			flowLogService.saveFlowLog(context.getFlowLog());
			Controller control = new Controller(context);
			InitState firstState = new InitState(control);
			control.setState(firstState);
			while(control.hasNextStep()){
				control.execute();
			}
			context.getFlowLog().setStatus(FlowRuntimeStatus.SUCCESS.getCode());
		} catch (Exception e) {
			e.printStackTrace();
			context.getFlowLog().setStatus(FlowRuntimeStatus.FAIL.getCode());
		}
		flowLogService.saveFlowLog(context.getFlowLog());
	}

}
