package org.rhino.octopus.slaver.executor;

import org.rhino.octopus.base.configuration.Property;
import org.rhino.octopus.base.configuration.OctopusConfiguration.ConfigurationItem;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.model.JobLog;

public class JavaJobExecutor extends JobExecutor {
	
	private static final String SEPERATOR = System.getProperty("path.separator");
	
	private static final String JAVA = "java ";
	
	private static final String CLASS_PATH = " -classpath ";
	
	private static final String MAIN_CLASS = " org.rhino.octopus.slaver.executor.JobRunner ";

	public JavaJobExecutor(JobProperties jobProp, JobLog jobLog,
			String flowParams) {
		super(jobProp, jobLog, flowParams);
	}
	
	
	protected String getExecCommand() throws OctopusException{
		String fullName = this.getClass().getResource(this.getClass().getSimpleName() + ".class").toString();
		String[] arr = fullName.split("!")[0].split("/");
		String jarName = arr[arr.length -1];
		Property libPathProp = SlaverContext.getInstance().getConfiguration().getProperty(ConfigurationItem.USR_LIB_PATH);
		
		StringBuilder builder = new StringBuilder();
		builder.append(JAVA);
		builder.append(CLASS_PATH);
		builder.append(jarName);
		builder.append(SEPERATOR);
		builder.append(libPathProp.getValue() + "/*");
		builder.append(SEPERATOR); 
		builder.append(MAIN_CLASS);
		builder.append(this.jobProp.getClsName());
		return builder.toString();
	}

}
