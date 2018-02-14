package it.polito.dp2.NFV.sol3.service.DaoClasses;



import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;


import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.polito.dp2.NFV.lab3.ServiceException;

public class HostDao {

	private static ConcurrentMap<String, HostType> hostMap = new ConcurrentHashMap<String, HostType>();
	
	static private HostDao hostDao = new HostDao();
	
	public static HostDao getInstance() {
		return hostDao;
	}
	
	/* 
	 * insertion of data inside the database
	 * insertion of data inside the local hashmap
	 */
	public void createHost(List<HostType> hostList) throws ServiceException {
		Neo4jServiceManager neo4jXMLclient = Neo4jServiceManager.getInstance();
		
		Node neo4jNode = new Node();
		Property neo4jProperty = new Property();
		Labels hostLabel = new Labels();
		
		// declarate a new property inside the neo4jnode
		neo4jNode.setProperties(new Properties());
		neo4jNode.getProperties().getProperty().add(neo4jProperty);
		
		// set the value of the label as Host
		hostLabel.getLabel().add("host");
		
		for(HostType host: hostList) {
			hostMap.put(host.getHostname(), host);
			
			// set the neo4j node property a and forward it
			neo4jNode.getProperties().getProperty().get(0).setName("name");
			neo4jNode.getProperties().getProperty().get(0).setValue(host.getHostname());
			neo4jXMLclient.sendNode(neo4jNode, hostLabel);
		}
	}
	
	/*
	 * read the entire collection of data stored locally in the hashmap
	 */
	public Collection<HostType> readAllHosts() {
		return hostMap.values();
	}
	
	/*
	 * read a single host starting from his hostname
	 */
	public HostType readHost(String hostname) {
		return hostMap.get(hostname);
	}
	
	/*
	 * update a single host
	 * update of the list of deployed node
	 * update total allocated Storage and Memory
	 */
	public void updateHostDetail(String hostId, VnfType functionToBeDeployed, boolean allocate) {
		
		// get the host from the map
		HostType resultHost = hostMap.get(hostId);
		
		/*if(resultHost == null) 
			// the host doesn't exist
			return false;*/
		
		// update the storage and memory value inside the host
		int nodeMem = functionToBeDeployed.getRequiredMemory().intValue();
		int nodeStorage = functionToBeDeployed.getRequiredStorage().intValue();
		
		int availableStorage = resultHost.getAvailableStorage().intValue();
		int availableMemory = resultHost.getAvailableMemory().intValue();
		
		if(allocate) {
			resultHost.setAvailableMemory(BigInteger.valueOf(availableMemory - nodeMem));
			resultHost.setAvailableStorage(BigInteger.valueOf(availableStorage - nodeStorage));
		} else {
			resultHost.setAvailableMemory(BigInteger.valueOf(availableMemory + nodeMem));
			resultHost.setAvailableStorage(BigInteger.valueOf(availableStorage + nodeStorage));
		}
	}
	
	public void updateHostDeployedNode(String hostName, String nodeId) {
		HostType targetHost = hostMap.get(hostName);
		
		HostType.DeployedNode newDeployedNode = new HostType.DeployedNode();
		newDeployedNode.setNodeName(nodeId);
		
		targetHost.getDeployedNode().add(newDeployedNode);
	}
	
	public void deleteHost(HostType host) {}
	
	/*
	 * return a collection of hosts that satidfy the constraint of memory and storage
	 */
	public Collection<HostType> queryHost(int minStorage, int minMemory) {
				
		// apply the filter predicate to the hashmap
		Map<String, HostType> hostSet = hostMap.entrySet()
				.stream()
				.filter(p -> p.getValue().getAvailableMemory().intValue() >= minMemory && p.getValue().getAvailableStorage().intValue() >= minStorage)
				.collect(Collectors.toMap(p-> p.getKey(), p-> p.getValue()));
				
		return hostSet.values();
	}
}
