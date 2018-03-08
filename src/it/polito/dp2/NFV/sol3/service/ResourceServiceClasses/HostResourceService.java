package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.*;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.ServiceException;

public class HostResourceService {
	
	private HostDao hostDao;
	
	public HostResourceService() {
		hostDao = HostDao.getInstance();
	}
	
	/*
	 * return all the host available into the system, use a monitor to
	 * synchronize the acces to the hostDao
	 */
	public List<ExtendedHostType> getAllAvailableHost() {
		synchronized(hostDao) {
			List<ExtendedHostType> availableHost = new ArrayList<ExtendedHostType>(hostDao.readAllHosts());
			return availableHost;
		}
	}
	
	public List<RestrictedNodeType> getAllocatedNodes(String hostname) throws InternalServerErrorException {
		List<RestrictedNodeType> nodeList = new ArrayList<RestrictedNodeType> ();
		
		// perform a query on the database and get the node correspondent to the name
		ExtendedHostType host = HostDao.getInstance().readHost(hostname);
		// if the database doesn't not contain the host return null
		if(host == null) 
			return null;
		
		for(DeployedNodeType deployed_node: host.getDeployedNodes().getNode()) {
			RestrictedNodeType node = GraphDao.getInstance().queryGraph(deployed_node.getNodeName());
			if(node == null) 
				throw new InternalServerErrorException();
			
			nodeList.add(node);
		}
		
		return nodeList; 
	} 
	
	/*
	 * return only the host that satisfy the latency and throughput constraint 
	 */
	public List<ExtendedHostType> queryAvailableHost(int minMemory, int minStorage) {
		synchronized(hostDao) {
			List<ExtendedHostType> resultList = new ArrayList<ExtendedHostType>(hostDao.queryHost(minStorage, minMemory));
			return resultList;
		}
	}
	
	/*
	 * search a single host element into the database
	 */
	public ExtendedHostType getSingleHost(String hostname) {
		synchronized(hostDao) {
			return hostDao.readHost(hostname);
		}
	}
	
	/*
	 * used only at server startup because laod in memory the list of the host that 
	 * are available to the client
	 */
	public void setHostsAtStartup(List<ExtendedHostType> hostList) throws ServiceException {
		if(hostList.isEmpty()) 
			throw new ServiceException("the list of host to be loaded in memory is empty");
		
		hostDao.createHost(hostList);
	}
}
