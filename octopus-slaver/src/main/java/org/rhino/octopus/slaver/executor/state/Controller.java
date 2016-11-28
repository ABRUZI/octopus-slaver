package org.rhino.octopus.slaver.executor.state;

import org.rhino.octopus.base.exception.OctopusException;

public class Controller {

	
	private FlowExecContext context;
	
	private IState state;
	
	public Controller(FlowExecContext context){
		this.context = context;
	}
	
	public void setState(IState state){
		this.state = state;
	}
	
	public void execute() throws OctopusException{
		this.state.execute();
	}
	
	public FlowExecContext getContext(){
		return this.context;
	}
	
	public boolean hasNextStep(){
		return this.state != null;
	}
	
}
