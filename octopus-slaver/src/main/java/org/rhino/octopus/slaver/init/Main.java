package org.rhino.octopus.slaver.init;

import org.rhino.octopus.base.constants.ConfConstants;
import org.rhino.octopus.base.log.LoggerRegister;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.listener.local.LocalListener;
import org.rhino.octopus.slaver.listener.remote.RemoteListener;
import org.rhino.octopus.slaver.register.SlaverRegister;


public class Main {
	
	private static final String LOG_PATH = "/slaver";
	
	private static final String ERROR_LOG_FILE = "/error_log";
	private static final String DEBUG_LOG_FILE = "/debug_log";

	public static void main(String[] args) {
		
		try{
			
			/**
			 * 初始化日志打印工具
			 */
			String logTopPath = ConfConstants.getLogTopPath();
			LoggerRegister.register("error", LoggerRegister.LEVEL_ERROR, logTopPath + LOG_PATH + ERROR_LOG_FILE);
			LoggerRegister.register("debug", LoggerRegister.LEVEL_DEBUG, logTopPath + LOG_PATH + DEBUG_LOG_FILE);
			
			/**
			 * 启动上下文，加载配置
			 */  
			SlaverContext.getInstance().init();
			
			/**
			 * 把Slaver注册到zk
			 */
			SlaverRegister.getInstance().open();
			
			/**
			 * 启动本地服务监听器
			 */
			LocalListener localListener= LocalListener.getInstance();
			localListener.open();
			new Thread(localListener).start();
			
			/**
			 * 启动远程调用服务监听器
			 */
			RemoteListener remoteListener = RemoteListener.getInstance();
			remoteListener.open();
			new Thread(remoteListener).start();
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
}