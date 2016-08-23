package org.rhino.octopus.slaver.init;

import org.rhino.octopus.slaver.context.SlaverContext;
import org.rhino.octopus.slaver.listener.local.LocalListener;
import org.rhino.octopus.slaver.listener.remote.RemoteListener;
import org.rhino.octopus.slaver.register.SlaverRegister;


public class Main {

	public static void main(String[] args) {
		
		try{
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