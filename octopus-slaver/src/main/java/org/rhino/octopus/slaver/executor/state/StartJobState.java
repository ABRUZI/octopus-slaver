package org.rhino.octopus.slaver.executor.state;

import java.util.ArrayList;
import java.util.List;

import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.base.model.line.Line;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.executor.JobExecutor;
import org.rhino.octopus.slaver.service.JobLogService;
import org.springframework.util.CollectionUtils;

/**
 * @author 王铁
 */
public class StartJobState implements IState{
	
	private Controller controller;
	
	public StartJobState(Controller controller){
		this.controller = controller;
	}

	@Override
	public void execute() throws OctopusException {
		List<JobExecutor> todoList =  this.findAndRemoveStartJobsFromWaitingList();
		if(CollectionUtils.isEmpty(todoList) && CollectionUtils.isEmpty(this.controller.getContext().getWaitingList())){
			if(CollectionUtils.isEmpty(this.controller.getContext().getExecutingList())){
				this.controller.getContext().getFlowLog().setStatus("");
				this.controller.setState(new StopState(this.controller));
			}
		}else{
			if(CollectionUtils.isEmpty(todoList) == false){
				for(int i = 0, len = todoList.size(); i < len ; i++){
					JobExecutor todoExecutor = todoList.get(i);
					new Thread(todoExecutor).start();
					this.controller.getContext().getExecutingList().add(todoExecutor);
					JobLogService logSvc = (JobLogService)SlaverContext.getInstance().getBean("jobLogService");
					logSvc.saveJobLog(todoExecutor.getJobLog());
				}
			}
			this.controller.setState(new JudgeRunningJobState(this.controller));
		}
		
	}

	private List<JobExecutor> findAndRemoveStartJobsFromWaitingList() {
		
		
		List<JobExecutor> finishedList = this.findFinishedList();
		List<JobExecutor> todoList = new ArrayList<JobExecutor>();
			
		List<JobExecutor> waitingList = this.controller.getContext().getWaitingList();
		for(int i = waitingList.size() - 1; i >= 0; i--){
			JobExecutor waitingExecutor = waitingList.get(i);
			if(this.canBeExecute(waitingExecutor, finishedList)){
				todoList.add(waitingExecutor);
				waitingList.remove(i);
			}
		}
		
		return todoList;
	}
	
	private boolean canBeExecute(JobExecutor waitingExecutor, List<JobExecutor> finishedList){
		
		List<Line> concernedLineList = this.findLineListByTargetJob(waitingExecutor.getJob());
		
		boolean can = false;
		if(CollectionUtils.isEmpty(finishedList)){
			if(CollectionUtils.isEmpty(concernedLineList)){
				can = true;
			}
		}else{
			
			boolean allCan = true;
			for(int i = 0 , len = concernedLineList.size(); i < len; i++){
				Line line = concernedLineList.get(i);
				allCan = allCan & this.shouldLineBeActivate(line, finishedList);
			}
			
			can = allCan;
		}
		
		return can;
	}
	
	private boolean shouldLineBeActivate(Line line, List<JobExecutor> finishedList){
 		boolean should = false;
		for(int i = 0, len = finishedList.size(); i < len; i++){
			JobExecutor jobExecutor = finishedList.get(i);
			JobProperties jobProps = jobExecutor.getJob();
			if(jobProps.getId().equals(line.getSourceJobId())){
				if(jobExecutor.getJob().isJudge()){
					if(jobExecutor.getExitCode() == line.getCondition()){
						should = true;
						break;
					}
				}else{
					should = true;
					break;
				}
			}
		}
		
		return should;
	}
	
	private List<Line> findLineListByTargetJob(JobProperties jobProps){
		
		List<Line> allLineList = this.controller.getContext().getLineList();
		List<Line> concernedLineList = new ArrayList<Line>();
		for(int i = 0, len = allLineList.size(); i < len; i++){
			Line line = allLineList.get(i);
			if(line.getTargetJobId().equals(jobProps.getId())){
				concernedLineList.add(line);
			}
		}
		return concernedLineList;
	}
	
	private List<JobExecutor> findFinishedList(){
		
		List<JobExecutor> finishedList = new ArrayList<JobExecutor>();
		List<JobExecutor> successList = this.controller.getContext().getSuccessList();
		List<JobExecutor> failedList = this.controller.getContext().getFailedList();
		
		finishedList.addAll(successList);
		finishedList.addAll(failedList);
		
		return finishedList;
	}

}
