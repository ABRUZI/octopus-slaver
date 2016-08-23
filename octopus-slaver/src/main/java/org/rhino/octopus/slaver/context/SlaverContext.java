package org.rhino.octopus.slaver.context;

import org.rhino.octopus.base.configuration.OctopusConfiguration;
import org.rhino.octopus.base.constants.ConfConstants;
import org.rhino.octopus.base.exception.OctopusException;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SlaverContext {
	
	private static final String COMMON_CONF_PATH = "/octopus-common.xml";
	
	private static final String SLAVER_CONF_PATH = "/octopus-slaver.xml";
	
	private ClassPathXmlApplicationContext application;
	
	private static SlaverContext instance = new SlaverContext();
	
	private OctopusConfiguration configuration;
	
	private boolean isInit;
	
	private SlaverContext(){}
	
	public synchronized void init()throws OctopusException{
		if(this.isInit == false){
			this.application = new ClassPathXmlApplicationContext("classpath*:/org/rhino/octopus/slaver/config/spring/applicationContext.xml");
			String confPath = ConfConstants.getConfPath();
			this.configuration =new OctopusConfiguration(new String[]{confPath + COMMON_CONF_PATH, confPath + SLAVER_CONF_PATH});
			isInit = true;
		}
	}
	
	public static SlaverContext getInstance()throws OctopusException{
		return instance;
	}
	
	public Object getBean(String beanId){
		return this.application.getBean(beanId);
	}
	
	public OctopusConfiguration getConfiguration(){
		return this.configuration;
	}
}
