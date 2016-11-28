package org.rhino.octopus.slaver.executor.state;

import org.rhino.octopus.base.exception.OctopusException;

public class StopState implements IState {

	
	private Controller controller;
	
	public StopState(Controller controller){
		this.controller = controller;
	}
	
	@Override
	public void execute() throws OctopusException{
		this.controller.setState(null);
	}

}
