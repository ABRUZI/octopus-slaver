package org.rhino.octopus.slaver.local;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.rhino.octopus.base.configuration.OctopusConfiguration;
import org.rhino.octopus.base.configuration.OctopusConfiguration.ConfigurationItem;
import org.rhino.octopus.base.configuration.Property;
import org.rhino.octopus.base.constants.ConfConstants;
import org.rhino.octopus.slaver.listener.local.SlaverLocalService;

public class LocalCommand {

	private static final String COMMON_CONF_PATH = "/octopus-common.xml";
	
	private static final String SLAVER_CONF_PATH = "/octopus-slaver.xml";
	
	private static final String LOCAL_IP = "localhost";

	public static void main(String[] args) {
		TTransport transport = null;
		try {
			String confPath = ConfConstants.getConfPath();
			OctopusConfiguration configuration =new OctopusConfiguration(new String[]{confPath + COMMON_CONF_PATH, confPath + SLAVER_CONF_PATH});
			Property portProp = configuration.getProperty(ConfigurationItem.SLAVER_LOCAL_LISTENER_PORT);
			transport = new TSocket(LOCAL_IP, Integer.parseInt(portProp.getValue()), 30000);
			TProtocol protocol = new TBinaryProtocol(transport);
			SlaverLocalService.Client client = new SlaverLocalService.Client(
					protocol);
			transport.open();
			client.execute(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != transport) {
				transport.close();
			}
		}
	}

}
