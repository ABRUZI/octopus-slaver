package org.rhino.octopus.slaver.executor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.rhino.octopus.base.constants.JobRuntimeStatus;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.slaver.executor.constants.ExecutorConstants;
import org.rhino.octopus.slaver.model.JobLog;

public abstract class JobExecutor implements Runnable {

	private static final int EXIT_SUCCESS  = 0;
	
	protected JobProperties jobProp;

	private String flowParams;
	
	private String result;
	
	private volatile JobLog jobLog;

	public JobExecutor(JobProperties jobProp, JobLog jobLog, String flowParams) {
		this.jobProp = jobProp;
		this.flowParams = flowParams;
		this.jobLog = jobLog;
	}

	@Override
	public void run() {
		this.jobLog.setStatusCode(JobRuntimeStatus.RUNNING.getCode());
		Runtime runtime = Runtime.getRuntime();
		
		BufferedWriter writer = null;
		BufferedReader stdReader = null;
		BufferedReader errReader = null;
		try {
			String command = this.getExecCommand();
			Process process = runtime.exec(command);
			writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			writer.write(this.flowParams);
			writer.close();
			stdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			int code = process.waitFor();
			this.jobLog.setErrLog(readLog(errReader));
			this.jobLog.setSysLog(this.readLog(stdReader));
			this.result = this.createResult();
			if(code == EXIT_SUCCESS){
				this.jobLog.setStatusCode(JobRuntimeStatus.SUCCESS.getCode());
			}else{
				this.jobLog.setStatusCode(JobRuntimeStatus.FAIL.getCode());
			}
		} catch (Exception e) {
			this.jobLog.setStatusCode(JobRuntimeStatus.FAIL.getCode());
			e.printStackTrace();
		} finally {
			if(stdReader != null){
				try {stdReader.close();} catch (IOException e) {e.printStackTrace();}
			}
			
			if(errReader != null){
				try {errReader.close();} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	

	public JobLog getJobLog() {
		return jobLog;
	}

	public JobProperties getJob() {
		return this.jobProp;
	}

	public String getResult() {
		return result;
	}
	
	
	protected abstract String getExecCommand() throws OctopusException;
	
	
	
	private String createResult(){
		
		String res = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.jobLog.getSysLog().getBytes())));
		String line = null;
		try {
			String key = this.createResultKey();
			while((line = reader.readLine()) != null){
				if(line.startsWith(key)){
					res = line.split(ExecutorConstants.EQUALS)[1];
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	private String createResultKey(){
		StringBuilder builder = new StringBuilder();
		builder.append(ExecutorConstants.LOG_PREFIX);
		builder.append(this.jobProp.getClsName());
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
