package it.polito.dp2.NFV.sol3.service.DaoClasses;


import it.polito.dp2.NFV.sol3.service.ServiceXML.ConnectionType;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionDao {

	static private ConnectionDao connDao = new ConnectionDao();
	private Map<StringPair, ConnectionType> connectionMap = new HashMap<StringPair, ConnectionType>();
	
	public static ConnectionDao getInstance() {
		return connDao;
	}
	
	/*
	 * create a new connection starting from a list of xml connection representation
	 */
	public void createConnection(List<ConnectionType> connectionList) {
		// put all the connections inside the list 
		for(ConnectionType conn: connectionList) {
			String host1 = conn.getHostname1();
			String host2 = conn.getHostname2();
			StringPair newStringPair = new StringPair(host1, host2);
			connectionMap.put(newStringPair, conn);
		}
	}
	
	/*
	 * store into the map a single connection 
	 */
	public void createConnection(ConnectionType newConnection) {
		String host1 = newConnection.getHostname1();
		String host2 = newConnection.getHostname2();
		StringPair newStringPair = new StringPair(host1, host2);
		connectionMap.put(newStringPair, newConnection);
	}
	
	/*
	 * return a collection of all the connection stored into the hashmap
	 */
	public Collection<ConnectionType> readAllConnections() {
		return connectionMap.values();
	}
	
	/*
	 * read a single connection from the hashmap
	 */
	public ConnectionType readConnection(String host1, String host2) {
		StringPair newStringPair = new StringPair(host1, host2);
		return connectionMap.get(newStringPair);
	}
	
	public void updateConnection() {}
	
	public void deleteConnection() {}
}
