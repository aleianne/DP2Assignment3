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

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.ServiceException;

public class HostDao {

	private static ConcurrentMap<String, ExtendedHostType> hostMap = new ConcurrentHashMap<String, ExtendedHostType>();
	
	static private HostDao hostDao = new HostDao();
	
	public static HostDao getInstance() {
		return hostDao;
	}
	
	/* 
	 * insertion of data inside the database
	 * insertion of data inside the local hashmap
	 */
	public void createHost(List<ExtendedHostType> hostList) throws ServiceException {
		Neo4jServiceManager neo4jXMLclient = Neo4jServiceManager.getInstance();
		
		Node neo4jNode = new Node();
		Property neo4jProperty = new Property();
		Labels hostLabel = new Labels();
		
		// declarate a new property inside the neo4jnode
		neo4jNode.setProperties(new Properties());
		neo4jNode.getProperties().getProperty().add(neo4jProperty);
		
		// set the value of the label as Host
		hostLabel.getLabel().add("host");
		
		for(ExtendedHostType host: hostList) {
			hostMap.put(host.getHostname(), host);
			
			// set the neo4j node property a and forward it
			neo4jNode.getProperties().getProperty().get(0).setName("name");
			neo4jNode.getProperties().getProperty().get(0).setValue(host.getHostname());
			neo4jXMLclient.postNode(neo4jNode, hostLabel);
		}
	}
	
	/*
	 * read the entire collection of data stored locally in the hashmap
	 */
	public Collection<ExtendedHostType> readAllHosts() {
		return hostMap.values();
	}
	
	/*
	 * read a single host starting from his hostname
	 */
	public ExtendedHostType readHost(String hostname) {
		return hostMap.get(hostname);
	}
	
	/*
	 * update a single host
	 * update of the list of deployed node
	 * update total allocated Storage and Memory
	 */
	public void updateHost(String hostname, FunctionType functionToBeDeployed) {
		// interrogate the database in order to obtain the host to be update
		ExtendedHostType queryResultHost = hostMap.get(hostname);
		if(queryResultHost == null) {
			throw new InternalServerErrorException();
		}
		
		// update the storage and memory value inside the host
		int nodeMem = functionToBeDeployed.getRequiredMemory().intValue();
		int nodeStorage = functionToBeDeployed.getRequiredStorage().intValue();
		
		int availableStorage = queryResultHost.getStorageUsed().intValue();
		int availableMemory = queryResultHost.getMemoryUsed().intValue();
		
		// update the host resource information
		queryResultHost.setMemoryUsed(BigInteger.valueOf(availableMemory + nodeMem));
		queryResultHost.setStorageUsed(BigInteger.valueOf(availableStorage + nodeStorage));
	}
	
	public void updateHost(String hostname, NodeType nodeToBeDeployed) {
		// interrogate the database in order to obtain the host to be update
		ExtendedHostType queryResultHost = hostMap.get(hostname);
		if(queryResultHost == null) {
			throw new InternalServerErrorException();
		}
		
		ExtendedHostType.DeployedNodes deployedNodes = queryResultHost.getDeployedNodes();
		DeployedNodeType node = new DeployedNodeType();
		node.setNodeName(nodeToBeDeployed.getName());
		
		// if the list of deployed into the host in empty create it
		if(deployedNodes == null) {
			deployedNodes = new ExtendedHostType.DeployedNodes();
			deployedNodes.getNode().add(node);
			queryResultHost.setDeployedNodes(deployedNodes);
		} else {
			deployedNodes.getNode().add(node);
		}
	}
	
	/*public void updateHostDeployedNode(String hostName, String nodeId) {
		ExtendedHostType targetHost = hostMap.get(hostName);
		
		ExtendedHostType.DeployedNode newDeployedNode = new ExtendedHostType.DeployedNode();
		newDeployedNode.setNodeName(nodeId);
		
		targetHost.getDeployedNode().add(newDeployedNode);
	}
	*/
	public void deleteHost(ExtendedHostType host) {}
	
	/*
	 * return a collection of hosts that satidfy the constraint of memory and storage
	 */
	public Collection<ExtendedHostType> queryHost(int minStorage, int minMemory) {
		// apply the filter predicate to the hashmap
		Map<String, ExtendedHostType> hostSet = hostMap.entrySet()
				.stream()
				.filter(p -> p.getValue().getAvailableMemory().intValue() >= minMemory && p.getValue().getAvailableStorage().intValue() >= minStorage)
				.collect(Collectors.toMap(p-> p.getKey(), p-> p.getValue()));
				
		return hostSet.values();
	}
}
