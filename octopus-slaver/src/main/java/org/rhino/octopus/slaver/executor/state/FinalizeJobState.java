package org.rhino.octopus.slaver.executor.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rhino.octopus.base.constants.FlowRuntimeStatus;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.base.model.line.Line;
import org.rhino.octopus.slaver.executor.JobExecutor;
import org.springframework.util.CollectionUtils;

public class FinalizeJobState implements IState {

	
	private Controller controller;
	
	public FinalizeJobState(Controller controller){
		this.controller = controller;
	}
	
	@Override
	public void execute()throws OctopusException {
		
		boolean hasFatal = false;
		
		List<JobExecutor> failedList = this.controller.getContext().getFailedList();
		for(int i = 0, len = failedList.size(); i < len; i++){
			JobExecutor failedExecutor = failedList.get(i);
			if(failedExecutor.getJob().isFatal()){
				hasFatal = true;
				break;
			}
		}
		
		if(hasFatal == false){
			this.controller.setState(new StartJobState(this.controller));
			this.setNoNeedList();
		}else{
			this.controller.getContext().getFlowLog().setStatus(FlowRuntimeStatus.FAIL.getCode());
			this.controller.setState(new StopState(this.controller));
		}
	}
	
	/**
	 * 找到当前Flow中无需执行的任务,把他们从等待列表中移除加入到无需执行列表中
	 * 		注意：一个Exec类型的任务，如果配置为fatal = true，则无需考虑后续依赖的任务在其失败后是否需要执行，因为此时整个Flow执行都结束了。
	 * 			  而如果fatal = false 则无论它执行成功还是失败，后续依赖的任务都可以执行。所以，针对Exec类型的任务，在这个方法中不需要考虑
	 * 判断依据：
	 * 		1. 当一个Judge类型任务执行成功，该任务会返回一个result值，以该任务为source的连线的condition值与result值不同的target任务都为无需执行任务
	 * 		   注意：Judge类型任务的fatal都必须是true，一旦出现问题，整个flow就都被结束了。这个是整个Flow执行状态机的法则，所以不需要考虑Judge任务失败的情况
	 * 		2. 把获取到的无需执行的任务放到无需执行任务列表中
	 * 		3. 在等待队列中找到所有无需执行任务列表中的任务的全部子节点（直接和间接子节点），从等待队列中移除，然后放到无需执行队列中
	 * 		
	 */
	private void setNoNeedList(){
		/**
		 * 1
		 */
		List<JobExecutor> currentNoNeedList = this.getNoNeedJobListWhichDependsOnJudgeJobFromWaitingList();
		
		/**
		 * 2
		 */
		List<JobExecutor> noNeedList = this.controller.getContext().getNoNeedList();
		noNeedList.addAll(currentNoNeedList);
		
		
		/**
		 * 3
		 */
		for(int i = 0, len = noNeedList.size(); i < len; i++){
			JobExecutor jobExecutor = noNeedList.get(i);
			JobProperties job = jobExecutor.getJob();
			noNeedList.addAll(this.getAllSubJobsFromWaitingList(job));
		}
		
	}
	
	
	
	private List<JobExecutor> getAllSubJobsFromWaitingList(JobProperties job){
		List<JobExecutor> executorList = getJobExecutorListDependsOnJobFromWaitingList(job);
		if(CollectionUtils.isEmpty(executorList)){
			return executorList;
		}
		
		for(int i = 0; i < executorList.size(); i++){
			JobExecutor jobExecitor = executorList.get(i);
			executorList.addAll(this.getAllSubJobsFromWaitingList(jobExecitor.getJob()));
		}
		return executorList;
	}
	
	private List<JobExecutor> getNoNeedJobListWhichDependsOnJudgeJobFromWaitingList(){
		List<JobExecutor> successList = this.controller.getContext().getSuccessList();
		List<JobExecutor> jobList = new ArrayList<JobExecutor>();
		
		for(int i = 0, len = successList.size(); i < len; i++){
			JobExecutor jobExecutor = successList.get(i);
			int exitCode = jobExecutor.getExitCode();
			if(jobExecutor.getJob().isJudge()){
				List<JobExecutor> jobExecutorList = this.getJobExecutorListDependsOnJonMeanwhileConditionFaiedFromWaitingList(jobExecutor.getJob(), exitCode);
				jobList.addAll(jobExecutorList);
			}
		}
		
		return jobList;
	}
	
	
	/**
	 * 给定一个Job和一个Condition，在等待队列中找到依赖该Job并且condition不等于传入condition的JobExecutor集合
	 * 注意，此方法在获取到一个无需执行的Job后，会把该Job从WaitingList中移除
	 * @param job
	 * @param condition
	 * @return
	 */
	private List<JobExecutor> getJobExecutorListDependsOnJonMeanwhileConditionFaiedFromWaitingList(JobProperties job, int condition){
		
		List<Line> lineList = this.controller.getContext().getLineList();
		List<JobExecutor> waitingList = this.controller.getContext().getWaitingList();
		if(CollectionUtils.isEmpty(lineList) || CollectionUtils.isEmpty(waitingList)){
			return Collections.emptyList();
		}
		
		List<JobExecutor> jobExecutorList = new ArrayList<JobExecutor>();
		Set<String> ids = new HashSet<String>();
		for(int i = 0, len = lineList.size(); i < len; i++){
			Line line = lineList.get(i);
			if(job.getId().equals(line.getSourceJobId()) && condition == line.getCondition()){
				ids.add(line.getTargetJobId());
			}
		}
		
		for(int i = waitingList.size() - 1; i >= 0; i--){
			JobExecutor executor = waitingList.get(i);
			JobProperties waitingJob = executor.getJob();
			if(ids.contains(waitingJob.getId())){
				jobExecutorList.add(executor);
				/**
				 * 注意，此处为移除操作
				 */
				waitingList.remove(i);
			}
		}
		
		return jobExecutorList;
	}
	
	/**
	 * 给定Job，在等待队列中找到直接依赖它的JOB的集合
	 * 注意，此方法在确认一个Job满足条件后，会把该Job从WaitingList中移除
	 * @param job
	 * @return
	 */
	private List<JobExecutor> getJobExecutorListDependsOnJobFromWaitingList(JobProperties job){
		List<Line> lineList = this.controller.getContext().getLineList();
		List<JobExecutor> waitingList = this.controller.getContext().getWaitingList();
		if(CollectionUtils.isEmpty(lineList) || CollectionUtils.isEmpty(waitingList)){
			return Collections.emptyList();
		}
		
		List<JobExecutor> jobExecutorList = new ArrayList<JobExecutor>();
		Set<String> ids = new HashSet<String>();
		for(int i = 0, len = lineList.size(); i < len; i++){
			Line line = lineList.get(i);
			if(job.getId().equals(line.getSourceJobId())){
				ids.add(line.getTargetJobId());
			}
		}
		
		for(int i = waitingList.size() - 1; i >= 0; i--){
			JobExecutor executor = waitingList.get(i);
			JobProperties waitingJob = executor.getJob();
			if(ids.contains(waitingJob.getId())){
				jobExecutorList.add(executor);
				/**
				 * 注意，此处为移除操作
				 */
				waitingList.remove(i);
			}
		}
		
		return jobExecutorList;
	}
}
