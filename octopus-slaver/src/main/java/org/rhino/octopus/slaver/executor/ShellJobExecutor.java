package org.rhino.octopus.slaver.executor;

import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.slaver.model.JobLog;

public class ShellJobExecutor extends JobExecutor {

	public ShellJobExecutor(JobProperties jobProp, JobLog jobLog,
			String flowParams) {
		super(jobProp, jobLog, flowParams);
	}

	@Override
	protected String getExecCommand() throws OctopusException {
		return null;
	}

}
