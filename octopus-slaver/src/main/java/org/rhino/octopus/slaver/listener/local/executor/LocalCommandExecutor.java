package org.rhino.octopus.slaver.listener.local.executor;

import org.rhino.octopus.base.exception.OctopusException;

/**
 * @author 王铁
 *
 */
public interface LocalCommandExecutor {
	public void execute() throws OctopusException;
}
