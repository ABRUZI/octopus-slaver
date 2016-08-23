package org.rhino.octopus.slaver.listener.local.executor;

import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.register.SlaverRegister;

public class EvictCommandExecutor implements LocalCommandExecutor {

	@Override
	public void execute() throws OctopusException {
		SlaverRegister.getInstance().close();
	}

}
