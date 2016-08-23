package org.rhino.octopus.slaver.executor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.rhino.octopus.base.model.job.IExecJob;
import org.rhino.octopus.base.model.job.IJudgeJob;
import org.rhino.octopus.slaver.executor.constants.ExecutorConstants;

public class JobRunner {

	
	
	private static final String BREAK_LINE  = "\n";
	
	public static void main(String[] args) {
		int exitCode = 0;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			String clsName = args[0];
			
			StringBuilder paramBuilder = new StringBuilder();
			
			String line = null;
			while((line = reader.readLine()) != null && line.length() != 0){
				paramBuilder.append(line);
				paramBuilder.append(BREAK_LINE);
			}
			String param = paramBuilder.toString();
			Class<?> cls = JobRunner.class.getClassLoader().loadClass(clsName);
			IExecJob job = (IExecJob)cls.getConstructor().newInstance();
			job.preparation(param);
			job.execution();
			job.finalization();
			if(isJudgeJob(job)){
				String result = ((IJudgeJob)job).getResult();
				String output = createResult(clsName, result);
				System.out.println();
				System.out.println(output);
			}
		} catch (Throwable e) {
			exitCode = -1;
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.exit(exitCode);
	}
	
	private static boolean isJudgeJob(IExecJob job){
		return job instanceof IJudgeJob;
	}
	
	private static String createResult(String clsName, String result){
		StringBuilder builder = new StringBuilder();
		builder.append(ExecutorConstants.LOG_PREFIX);
		builder.append(clsName);
		builder.append(ExecutorConstants.EQUALS);
		builder.append(result);
		return builder.toString();
	}
}
