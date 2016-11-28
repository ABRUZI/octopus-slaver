package org.rhino.octopus.slaver.listener;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.rhino.octopus.slaver.listener.local.SlaverLocalService;

public class ClientSimulator {
	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT = 1234;
	public static final int TIMEOUT = 30000;
	
	public static void main(String[] args){
		TTransport transport = null;
		try {
			transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
			TProtocol protocol = new TBinaryProtocol(transport);
			SlaverLocalService.Client client = new SlaverLocalService.Client(
					protocol);
			transport.open();
			client.execute("shutdown");
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			if (null != transport) {
				transport.close();
			}
		}
	}
}
