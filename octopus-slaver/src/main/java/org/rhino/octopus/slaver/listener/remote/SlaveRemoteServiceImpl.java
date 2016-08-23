package org.rhino.octopus.slaver.listener.remote;

import org.apache.thrift.TException;
import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.base.remote.SlaverRemoteService;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.FlowExecutor;
import org.rhino.octopus.slaver.service.FlowService;

public class SlaveRemoteServiceImpl implements SlaverRemoteService.Iface{

	@Override
	public void start(String flowId) throws TException {

		SlaverContext slaverContext;
		try {
			slaverContext = SlaverContext.getInstance();
			FlowService flowService = (FlowService)slaverContext.getBean("flowService");
			Flow flow = flowService.queryFlowById(flowId);
			new Thread(new FlowExecutor(flow)).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop(String flowId) throws TException {
		System.out.println("stopped.....");
	}

}
