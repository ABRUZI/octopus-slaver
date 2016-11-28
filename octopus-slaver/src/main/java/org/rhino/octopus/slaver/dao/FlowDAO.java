package org.rhino.octopus.slaver.dao;

import java.util.ArrayList;
import java.util.List;

import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.base.model.job.JobProperties;
import org.rhino.octopus.base.model.line.Line;
import org.springframework.stereotype.Repository;

@Repository("flowDAO")
public class FlowDAO {

	
	public Flow queryFlowById(String flowId){
		
		
		Flow flow = new Flow();
		
		List<JobProperties> jobList = new ArrayList<JobProperties>();
		flow.setJobList(jobList);
		
		JobProperties firstJob = new JobProperties();
		firstJob.setId("1");
		firstJob.setName("起点Job");
		firstJob.setFatal(false);
		
		JobProperties judgetJob = new JobProperties();
		judgetJob.setId("2");
		judgetJob.setName("判断Job");
		judgetJob.setFatal(false);
		judgetJob.setJudge(true);
		
		JobProperties leftJob = new JobProperties();
		leftJob.setId("3");
		leftJob.setName("左Job");
		leftJob.setFatal(false);
		
		JobProperties rightJob = new JobProperties();
		rightJob.setId("4");
		rightJob.setName("右Job");
		rightJob.setFatal(false);
		
		jobList.add(firstJob);
		jobList.add(judgetJob);
		jobList.add(leftJob);
		jobList.add(rightJob);
		
		List<Line> lineList = new ArrayList<Line>();
		
		Line line1 = new Line();
		line1.setId("a");
		line1.setSourceJobId("1");
		line1.setTargetJobId("2");
		
		Line line2 = new Line();
		line2.setId("b");
		line2.setSourceJobId("2");
		line2.setTargetJobId("3");
		line2.setCondition(1);
		
		Line line3 = new Line();
		line3.setId("c");
		line3.setSourceJobId("2");
		line3.setTargetJobId("4");
		line3.setCondition(0);
		
		lineList.add(line1);
		lineList.add(line2);
		lineList.add(line3);
		
		flow.setId("123");
		flow.setLineList(lineList);
		
		return flow;
	}
}
