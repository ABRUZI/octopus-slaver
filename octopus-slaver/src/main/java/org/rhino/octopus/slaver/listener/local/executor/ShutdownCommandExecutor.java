package org.rhino.octopus.slaver.listener.local.executor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.listener.local.LocalListener;
import org.rhino.octopus.slaver.listener.remote.RemoteListener;
import org.rhino.octopus.slaver.register.SlaverRegister;

public class ShutdownCommandExecutor implements LocalCommandExecutor {

	public ShutdownCommandExecutor(){
	}
	
	@Override
	public void execute() throws OctopusException {
		
		Timer timer = new Timer();
		
		timer.schedule(new TimerTask(){

			@Override
			public void run() {
				try{
					LocalListener.getInstance().close();
					RemoteListener.getInstance().close();
					SlaverRegister.getInstance().close();
				}catch(Exception e){
					e.printStackTrace();
				}
				
				System.exit(0);
			}
			
		}, new Date(), 10000L);
	}

}
