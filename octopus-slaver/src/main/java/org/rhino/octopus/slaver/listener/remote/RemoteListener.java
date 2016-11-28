package org.rhino.octopus.slaver.listener.remote;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.rhino.octopus.base.configuration.OctopusConfiguration.ConfigurationItem;
import org.rhino.octopus.base.configuration.Property;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.base.remote.SlaverRemoteService;
import org.rhino.octopus.slaver.context.SlaverContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteListener implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(RemoteListener.class);

	private static RemoteListener instance = new RemoteListener();
	
	private TServer server;
	
	private RemoteListener(){}
	
	public static RemoteListener getInstance(){
		return instance;
	}
	
	
	public void open()throws OctopusException{
		
		try{
			logger.debug("初始化远程服务监听器");
			Property remotePortProp = SlaverContext.getInstance().getConfiguration().getProperty(ConfigurationItem.SLAVER_REMOTE_LISTENER_PORT);
			TProcessor tprocessor = new SlaverRemoteService.Processor<SlaverRemoteService.Iface>(
					new SlaveRemoteServiceImpl());
			TServerSocket serverTransport = new TServerSocket(Integer.parseInt(remotePortProp.getValue()));
			TThreadPoolServer.Args ttpsArgs = new TThreadPoolServer.Args(
					 serverTransport);
			ttpsArgs.processor(tprocessor);
			ttpsArgs.protocolFactory(new TBinaryProtocol.Factory());
			this.server = new TThreadPoolServer(ttpsArgs);
			logger.debug("远程服务监听器初始化完毕");
		}catch(Exception e){
			logger.error("Init remote service listener failed", e);
			throw new OctopusException(e);
		}
	}
	
	
	public void close(){
		this.server.stop();
	}

	@Override
	public void run() {
		this.server.serve();
	}
}
