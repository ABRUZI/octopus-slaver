package org.rhino.octopus.slaver.listener.remote;

import org.apache.thrift.TException;
import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.base.remote.SlaverRemoteService;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.FlowExecutor;
import org.rhino.octopus.slaver.service.FlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlaveRemoteServiceImpl implements SlaverRemoteService.Iface{

	private static final Logger logger = LoggerFactory.getLogger(SlaveRemoteServiceImpl.class);
	
	@Override
	public void start(String flowId) throws TException {
		try {
			logger.debug("接受到Master指令，准备启动flow flowId=" + flowId);
			SlaverContext slaverContext = SlaverContext.getInstance();
			FlowService flowService = (FlowService)slaverContext.getBean("flowService");
			Flow flow = flowService.queryFlowById(flowId);
			logger.debug("flow 加载完毕，准备启动。" + flow);
			FlowExecutor.getInstance().execute(flow);
			logger.debug("flow 启动完毕。" + flow);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(String flowId) throws TException {
		try{
			logger.debug("接收到停止flow的指令，flowId=" + flowId);
			FlowExecutor.getInstance().stop(flowId);
			logger.debug("flow已经停止, flowId=" + flowId);
		}catch(Exception e){
			logger.error("stop flow failed flowId=" + flowId, e);
		}
		
	}

}
