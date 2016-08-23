package org.rhino.octopus.slaver.executor.state;

import org.rhino.octopus.base.exception.OctopusException;

public interface IState {

	
	public void execute() throws OctopusException;
}
