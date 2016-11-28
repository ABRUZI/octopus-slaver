package org.rhino.octopus.slaver.executor;

import java.util.List;

import org.rhino.octopus.base.constants.FlowRuntimeStatus;
import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.state.Controller;
import org.rhino.octopus.slaver.executor.state.FlowExecContext;
import org.rhino.octopus.slaver.executor.state.InitState;
import org.rhino.octopus.slaver.service.FlowLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowRunner implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(FlowRunner.class);
			
	private Flow flow;
	
	private volatile boolean cancel;
	
	private Controller control;
	
	public FlowRunner(Flow flow){
		this.flow = flow;
		this.cancel = false;
	}
	
	@Override
	public void run() {
		logger.debug("准备执行Flow" + flow);
		logger.debug("初始化flow运行时上下文:" + flow);
		FlowExecContext context = new FlowExecContext(this.flow);
		FlowLogService flowLogService = null;
		try {
			flowLogService = (FlowLogService)SlaverContext.getInstance().getBean("flowLogService");
			flowLogService.saveFlowLog(context.getFlowLog());
			logger.debug("初始化状态机，所属flow:" + flow);
			this.control = new Controller(context);
			InitState firstState = new InitState(control);
			control.setState(firstState);
			logger.debug("状态机初始化完毕，所属flow:" + flow);
			while(control.hasNextStep() && this.cancel == false){
				control.execute();
			}
			context.getFlowLog().setStatus(FlowRuntimeStatus.SUCCESS.getCode());
		} catch (Exception e) {
			logger.error("execute flow failed " + flow, e);
			e.printStackTrace();
			context.getFlowLog().setStatus(FlowRuntimeStatus.FAIL.getCode());
		}
		
		if(this.cancel == true){
			logger.debug("当前flow被用户强制终止" + flow + " 运行时ID=" + context.getFlowLog().getFlowId());
			context.getFlowLog().setStatus(FlowRuntimeStatus.SHUTDOWN.getCode());
		}
		try{
			flowLogService.saveFlowLog(context.getFlowLog());
		}catch(Exception e){
			logger.error("update flow status in db failed" , e);
		}finally{
			FlowExecutor.getInstance().removeCache(this.flow.getId());
		}
	}
	
	public Flow getFlow(){
		return this.flow;
	}
	
	public void stop(){
		logger.debug("准备终止当前flow的所有Job , flow信息=" + this.flow);
		this.cancel = true;
		List<JobExecutor> runningExecutorList = this.control.getContext().getExecutingList();
		for(int i = 0, len = runningExecutorList.size(); i < len; i++){
			JobExecutor executor = runningExecutorList.get(i);
			try{
				logger.debug("准备终止flow=" + this.flow + "中的 Job = " + executor.getJob());
				executor.shutdown();
				logger.debug("flow=" + this.flow + "中的 Job = " + executor.getJob() + " 终止完毕");
			}catch(Exception e){
				logger.error("Shutdown flow=" + flow + "Job=" + executor.getJob() + " failed ", e);
				e.printStackTrace();
			}
		}
	}
}
