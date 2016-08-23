package org.rhino.octopus.slaver.listener.local;

import org.apache.thrift.TException;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.listener.local.executor.EvictCommandExecutor;
import org.rhino.octopus.slaver.listener.local.executor.LocalCommandExecutor;
import org.rhino.octopus.slaver.listener.local.executor.ShutdownCommandExecutor;

public class SlaverLocalServiceImpl implements SlaverLocalService.Iface{
	
	private static final String SHUTDOWN_CMD = "shutdown";
	
	private static final String EVICT_CMD = "evict";
	
	private static final String UNKNOWN_COMMAND = "未知的命令";
	
	private static final String COMMAND_SUCCESS = "执行完毕";
	
	private static final String COMMAND_FAIL = "执行失败";
	
	@Override
	public String execute(String command) throws TException {
		LocalCommandExecutor executor = this.getCommandExecutor(command);
		String res = UNKNOWN_COMMAND;
		if(executor != null){
			try{
				executor.execute();
				res = COMMAND_SUCCESS;
			}catch(OctopusException e){
				res = COMMAND_FAIL;
			}
		}
		return res;
	}
	
	private LocalCommandExecutor getCommandExecutor(String command){
		
		if(SHUTDOWN_CMD.equalsIgnoreCase(command)){
			return new ShutdownCommandExecutor();
		}else if(EVICT_CMD.equalsIgnoreCase(command)){
			return new EvictCommandExecutor();
		}
		
		return null;
	}

}
