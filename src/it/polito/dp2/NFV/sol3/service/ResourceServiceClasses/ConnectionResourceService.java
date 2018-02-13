package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import java.util.ArrayList;
import java.util.List;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.ConnectionDao;

public class ConnectionResourceService {

	private ConnectionDao connDao;
	
	public ConnectionResourceService() {
		connDao = ConnectionDao.getInstance();
	}
	
	public void setConnectionsAtStartup(List<ConnectionType> connList) {
		for(ConnectionType connection: connList) {
			connDao.createConnection(connection);
		}
	}
	
	public List<ConnectionType> getConnections() {
		List<ConnectionType> connList = new ArrayList<ConnectionType>(connDao.readAllConnections());
		return connList;
	}
	
	public ConnectionType getConnection(String host1, String host2) {
		return connDao.readConnection(host1, host2);
	}
}
