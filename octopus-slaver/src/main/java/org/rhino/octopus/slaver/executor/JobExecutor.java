package org.rhino.octopus.slaver.executor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.rhino.octopus.base.constants.JobRuntimeStatus;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.slaver.executor.util.ShellFileUtil;
import org.rhino.octopus.slaver.model.JobLog;

public class JobExecutor implements Runnable {

	private static final int EXIT_SUCCESS  = 0;
	
	private static final String POINT = ".";
	
	private static final String SPACE = " ";
	
	protected JobProperties jobProp;
	
	private volatile JobLog jobLog;
	
	private Process process;
	
	private boolean halted;
	
	private int exitCode;
	
	private String shellFileFullName;

	public JobExecutor(JobProperties jobProp, JobLog jobLog)throws OctopusException {
		this.jobProp = jobProp;
		this.jobLog = jobLog;
		this.halted = false;
		this.exitCode = Integer.MIN_VALUE;
		this.shellFileFullName = ShellFileUtil.createShellFile(jobProp.getFileName(), jobProp.getFile());
	}

	@Override
	public void run() {
		this.jobLog.setStatusCode(JobRuntimeStatus.RUNNING.getCode());
		Runtime runtime = Runtime.getRuntime();
		
		BufferedReader stdReader = null;
		BufferedReader errReader = null;
		try {
			synchronized(this){
				if(this.halted == true){
					return;
				}
				String command = this.getExecCommand();
				this.process = runtime.exec(command);
			}
			stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			this.exitCode = process.waitFor();
			this.jobLog.setErrLog(readLog(errReader));
			this.jobLog.setSysLog(this.readLog(stdReader));
			if(this.exitCode >= EXIT_SUCCESS){
				this.jobLog.setStatusCode(JobRuntimeStatus.SUCCESS.getCode());
			}else{
				this.jobLog.setStatusCode(JobRuntimeStatus.FAIL.getCode());
			}
		} catch (Exception e) {
			this.exitCode = -1;
			this.jobLog.setStatusCode(JobRuntimeStatus.FAIL.getCode());
			e.printStackTrace();
		} finally {
			ShellFileUtil.deleteShellFile(this.shellFileFullName);
			if(stdReader != null){
				try {stdReader.close();} catch (IOException e) {e.printStackTrace();}
			}
			
			if(errReader != null){
				try {errReader.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	
	public int getExitCode(){
		return this.exitCode;
	}

	public JobLog getJobLog() {
		return jobLog;
	}

	public JobProperties getJob() {
		return this.jobProp;
	}
	
	public void shutdown() {
		synchronized(this){
			this.halted = true;
			if(this.process != null){
				this.process.destroy();
			}
			ShellFileUtil.deleteShellFile(this.shellFileFullName);
		}
	}
	
	private String getExecCommand(){
		StringBuilder builder = new StringBuilder(POINT + File.separatorChar + shellFileFullName);
		for(int i = 0, len = this.jobProp.getParamList().size(); i < len; i++){
			builder.append(SPACE);
			builder.append(this.jobProp.getParamList().get(i));
		}
		return builder.toString();
	}
	
	private String readLog(BufferedReader reader) throws Exception{
		StringBuilder builder = new StringBuilder();
		String line = null;
		while((line = reader.readLine()) != null){
			builder.append(line);
			builder.append("\n");
		}
		return builder.toString();
	}
	
}
