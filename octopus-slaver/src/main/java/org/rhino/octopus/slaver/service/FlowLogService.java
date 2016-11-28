package org.rhino.octopus.slaver.service;

import org.rhino.octopus.base.model.flow.FlowLog;
import org.springframework.stereotype.Service;


@Service("flowLogService")
public class FlowLogService {

	/**
	 * @param flowId
	 * @return
	 */
	public int queryRunningLogNum(String flowId){
		return -1;
	}


	public void saveFlowLog(FlowLog flowLog){
		
	}
}
