package org.rhino.octopus.slaver.service;

import javax.annotation.Resource;

import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.slaver.dao.FlowDAO;
import org.springframework.stereotype.Service;

@Service("flowService")
public class FlowService {
	
	@Resource(name="flowDAO")
	private FlowDAO flowDAO;

	public Flow queryFlowById(String flowId){
		return this.flowDAO.queryFlowById(flowId);
	}
}
