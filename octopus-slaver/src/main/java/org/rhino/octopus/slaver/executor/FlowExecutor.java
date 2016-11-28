package org.rhino.octopus.slaver.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.rhino.octopus.base.configuration.OctopusConfiguration.ConfigurationItem;
import org.rhino.octopus.base.configuration.Property;
import org.rhino.octopus.base.model.flow.Flow;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowExecutor{
	
	private static final Logger logger = LoggerFactory.getLogger(FlowExecutor.class);

	private static FlowExecutor instance;
	
	private ExecutorService pool;
	
	private Map<String, FlowRunner> runnerCache;
	
	private FlowExecutor(){
		int num = 100;
		try{
			Property prop = SlaverContext.getInstance().getConfiguration().getProperty(ConfigurationItem.SLAVER_PARALLEL_NUMBER);
			num = Integer.valueOf(prop.getValue());
			this.runnerCache = new HashMap<String, FlowRunner>();
		}catch(Exception e){
			logger.error("can not cast parameter " + ConfigurationItem.SLAVER_PARALLEL_NUMBER.getName() + " as Integer", e);
			logger.error("use default value : " + num);
		}
		this.pool = Executors.newFixedThreadPool(num);
	}

	public static synchronized FlowExecutor getInstance(){
		if(instance == null){
			instance = new FlowExecutor();
		}
		return instance;
	}

	public void execute(Flow flow){
		FlowRunner runner = new FlowRunner(flow);
		this.runnerCache.put(flow.getId(), runner);
		this.pool.submit(runner);
	}
	
	public void stop(String flowId){
		FlowRunner runner = this.runnerCache.get(flowId);
		if(runner != null){
			this.removeCache(flowId);
			runner.stop();
		}
	}
	
	public synchronized void removeCache(String flowId){
		this.runnerCache.remove(flowId);
	}

}
