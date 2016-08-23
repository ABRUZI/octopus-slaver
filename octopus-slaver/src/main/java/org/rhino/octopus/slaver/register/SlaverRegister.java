package org.rhino.octopus.slaver.register;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.rhino.octopus.base.configuration.OctopusConfiguration;
import org.rhino.octopus.base.configuration.Property;
import org.rhino.octopus.base.constants.RegistConstants;
import org.rhino.octopus.base.exception.OctopusException;
import org.rhino.octopus.slaver.context.SlaverContext;

public class SlaverRegister {
	
	private static SlaverRegister instance = new SlaverRegister();
	
	private ZooKeeper zk;
	
	private SlaverRegister(){}
	
	public static SlaverRegister getInstance(){
		return instance;
	}
	
	public void open()throws OctopusException{
		try {
			OctopusConfiguration configuration = SlaverContext.getInstance().getConfiguration();
			Property zookeeperProp = configuration.getProperty(OctopusConfiguration.ConfigurationItem.ZOO_KEEPER);
			this.zk = new ZooKeeper(zookeeperProp.getValue(), 3000, null);
			while(this.zk.getState() != ZooKeeper.States.CONNECTED){
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			this.createNodeIfNotExists(RegistConstants.ROOT_REGIST_NODE, CreateMode.PERSISTENT);
			this.createNodeIfNotExists(RegistConstants.SLAVERS_REGIST_NODE, CreateMode.PERSISTENT);
			String curNodeName = RegistConstants.getSlaveNode();
			this.createNodeIfNotExists(curNodeName, CreateMode.EPHEMERAL);
			
		} catch (Exception e) {
			throw new OctopusException(e);
		}
	}
	
	private void createNodeIfNotExists(String nodeName, CreateMode mode)throws Exception{
		Stat nodeStat = this.zk.exists(nodeName, false);
		if(nodeStat == null){
			this.zk.create(nodeName, nodeName.getBytes(), Ids.OPEN_ACL_UNSAFE, mode);
		}
	}
	
	
	public void close()throws OctopusException{
		if(this.zk != null){
			try {
				this.zk.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
}
