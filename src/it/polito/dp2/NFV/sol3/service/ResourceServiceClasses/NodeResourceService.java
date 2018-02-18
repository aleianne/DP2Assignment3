package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.DaoClasses.GraphDao;
import it.polito.dp2.NFV.sol3.service.DaoClasses.HostDao;
import it.polito.dp2.NFV.sol3.service.DaoClasses.VnfDao;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.Node;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.Nodes;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NodeResourceService {
	
	private static Logger logger = Logger.getLogger(NodeResourceService.class.getName());
	
	public String addNode(String graphId, NodeType newNode) throws ServiceException, AllocationException, InternalServerErrorException {
		
		/*
		 * in this method use the same class used for the graph allocation 
		 * so crete a list with only one element and a list 
		 */
		
		HostDao hostDao = HostDao.getInstance();
		FunctionType virtualFunction = VnfDao.getInstance().readVnf(newNode.getName());
		
		if(virtualFunction == null) 
			throw new AllocationException();
				
		// create the list of node and vnf used during annotation
		List<FunctionType> vnfList = new ArrayList<FunctionType> ();
		List<NodeType> nodeList = new ArrayList<NodeType> ();
		nodeList.add(newNode);

		GraphAllocator allocator = new GraphAllocator();
		
		if(vnfList.size() != nodeList.size()) {
			logger.log(Level.SEVERE, "nodeList and vnfList are not of the same size");
			throw new InternalServerErrorException("server encourred into an error");
		}
		
		// synchronize the access to the host DAO interface
		synchronized(hostDao) {
			List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType> (hostDao.readAllHosts());
			allocator.findSelectedHost(vnfList, nodeList, hostDao);
			
			if(!vnfList.isEmpty()) {
				allocator.findBestHost(vnfList, hostList);
			}
			
			allocator.allocateGraph(nodeList, hostDao);
		}
			
		// create a new graph in the database
		GraphDao.getInstance().updateGraph(graphId, newNode);
			
			
		// TODO update the host with the name of the nodes
		synchronized(hostDao) {
			allocator.updateHost(nodeList, hostDao);
		}
		
		return nodeList.get(0).getName();
		
		/*GraphType targetGraph = graphDao.readGraph(graphId);
		
		if(targetGraph == null) {
			logger.log(Level.SEVERE, "the graph doesn't exist in the system", graphId);
			throw new AllocationException();
		}
		
		
		FunctionType nodeFunction = catalogDao.readVnf(newNode.getVNF());
	
		if(nodeFunction == null) {
			logger.log(Level.WARNING, "doesn't exist in the system a virtual function that corresponds to the function of the node", new Object[] {newNode});
			throw new AllocationException();
		}
			
		// search the suitable node for the node
		synchronized(hostDao) {
			int reqStorage = nodeFunction.getRequiredStorage().intValue();
			int reqMemory = nodeFunction.getRequiredMemory().intValue();
			
			if(newNode.getHostname() != null) {
				HostType host = hostDao.readHost(newNode.getHostname());
				if(host.getAvailableMemory().intValue() > reqStorage && host.getAvailableStorage().intValue() > reqMemory) {
					// if the host can contain this function 
					targetHost = host;
				} else {
					//if the host cannot contain this function 
					targetHost = searchHost(nodeFunction);
				}
				
			} else {
				// if the client does not specified a target host
				targetHost = searchHost(nodeFunction);
			} 
		
			hostDao.updateHostDetail(targetHost.getHostname(), nodeFunction, true);
			
			synchronized(graphDao) {
				graphDao.updateGraph(graphId, newNode);
			}
			
			hostDao.updateHostDeployedNode(targetHost.getHostname(), newNode.getName());
		}

		return newNode.getName();*/
	}

	
	public void getNode(String nodeId) {
		
	}

	public List<HostType> getReachableHost(String nffgId) throws ServiceException {
		Neo4jServiceManager neo4jClient = new Neo4jServiceManager();
		Nodes receivedNodes = neo4jClient.getReachableHost(nffgId);
		List<HostType> hostList = new ArrayList<HostType>();
		
		// elaborate the response of the neo4j service
		for(Node newNode: receivedNodes.getNode()) {
			HostType newHost = HostDao.getInstance().readHost(newNode.getProperties().getProperty().get(0).getValue());
			hostList.add(newHost);
		}
		return hostList;
	}
}
