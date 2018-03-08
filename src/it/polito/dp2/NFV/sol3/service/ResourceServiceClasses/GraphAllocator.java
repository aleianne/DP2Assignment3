package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;

public class GraphAllocator {
	
	private Map<Integer, String> targetHostMap;
	private Map<String, List<FunctionType>> allocatedNodeMap;
	
	private static Logger logger = Logger.getLogger(GraphAllocator.class.getName());
	
	public GraphAllocator() {
		allocatedNodeMap = new HashMap<String, List<FunctionType>> ();
		targetHostMap = new HashMap<Integer, String> ();
	}
	
	/*public boolean findSuitableHost(FunctionType functionToBeDeployed, RestrictedNodeType newNode) {
		int minNode;
		int reqStorage = functionToBeDeployed.getRequiredStorage().intValue();
		int reqMemory = functionToBeDeployed.getRequiredMemory().intValue();
		
		ExtendedHostType hostFound = null;
		// if the node have an hostname use it, if is it possible
		if(newNode.getHostname() != null) {
			hostFound= hostDao.readHost(newNode.getHostName());
			if(hostFound != null &&  hostFound.getRequiredStorage() > reqStorage && hostFound.getRequiredMemory() > reqMemory) {
				// update the host that has been found by the server
				targetHosts.add(hostFound.getHostname());
				targetFunctions.add(functionToBeDeployed);
				newNode.setHostname(hostFound.getHostname());
				return true;
			}
		}
		
		Set<ExtendedHostType> selectedHost = new HashSet<ExtendedHostType>(hostDao.queryHost(reqStorage, reqMemory));
		
		if(!selectedHost.isEmpty()) {
			minNode = Integer.MAX_VALUE;
			
			// find the host that have the minimum number of deployed network node
			for(ExtendedHostType host: selectedHost) {
				if(minNode > host.getTotalDeployedNode().intValue() &&
						host.getTotalDeployedNode().intValue() < host.getMaxVNF().intValue()) {
	
					hostFound = host;
					minNode = host.getTotalDeployedNode().intValue();
				}
			}
			
			// if hostFound is null no one host has been found 
			if(hostFound == null) {
				logger.log(Level.INFO, "no suitable host");
				return false;
			} else {
				// update the host that has been found by the server
				targetHosts.add(hostFound.getHostname());
				targetFunctions.add(functionToBeDeployed);
				newNode.setHostname(hostFound.getHostname());
				return true;
			}
		} 
		// if the database query return an empty set return false
		logger.log(Level.INFO, "the set is empty");
		return false;
	}*/

	/*
	 *  this method search if the host specified into the node is available 
	 */
	public void findSelectedHost(List<FunctionType> vnfList, List<RestrictedNodeType> nodeList, HostDao hostDao) {
		
		/* 
		 * in those arrays at the same position there is a correspondance between the node and is virtual function
		 */
		
		int index = 0;
		int usedStorage, usedMemory, totalVNF;
		List<FunctionType> vnfAllocatedList = new ArrayList<FunctionType> ();
		List<FunctionType> hostVnfAllocatedList;
		
		for(RestrictedNodeType node: nodeList) {
			String hostname = node.getHostname();
			
			logger.log(Level.INFO, "try to allocate function n: " + index + " " + vnfList.get(index).getName() + " on host " + hostname);
			
			if(hostname != null) {
				ExtendedHostType host = hostDao.readHost(hostname);
				
				if(host != null) {
					usedStorage = host.getStorageUsed().intValue();
					usedMemory = host.getMemoryUsed().intValue();
					totalVNF = host.getTotalVNFallocated().intValue();
					
					hostVnfAllocatedList = allocatedNodeMap.get(hostname);
					if(hostVnfAllocatedList != null) {
						for(FunctionType virtualFunction: hostVnfAllocatedList) {
							totalVNF++;
							usedMemory += virtualFunction.getRequiredMemory().intValue();
							usedStorage += virtualFunction.getRequiredStorage().intValue();
						}
					}
					
					if(totalVNF < host.getMaxVNF().intValue() &&
							usedMemory + vnfList.get(index).getRequiredMemory().intValue() <= host.getAvailableMemory().intValue() &&
							usedStorage + vnfList.get(index).getRequiredStorage().intValue() <= host.getAvailableStorage().intValue()) {
						
						if(hostVnfAllocatedList != null) {
							hostVnfAllocatedList.add(vnfList.get(index));
						} else {
							List<FunctionType> functionList = new ArrayList<FunctionType>();
							functionList.add(vnfList.get(index));
							allocatedNodeMap.put(hostname, functionList);
						}
						// insert the vnf into the list of elment ot be removed and add the hostname into the list of target
						vnfAllocatedList.add(vnfList.get(index));
						targetHostMap.put(index, hostname);
					}
				}
			}
			index++;
		}
		// delete the allocated function from the list
		for(FunctionType function: vnfAllocatedList) {
			vnfList.remove(function);
		}
	}
	
	public void findBestHost(List<FunctionType> vnfList, List<ExtendedHostType> hostList) throws AllocationException{
		int minVnfAllocated, numAllocatedNodes, usedMemory, usedStorage;
		ExtendedHostType targetHost;
		List<FunctionType> allocatedVnfList;
		int index = 0;
		
		minVnfAllocated = numAllocatedNodes = usedMemory = usedStorage = 0;
		
		/*
		 *  implements a custom compare method for the sorting operation 
		 *  performend before the application of the best fit decreasing algorithm
		 */
		Collections.sort(hostList, new Comparator<ExtendedHostType>() {

			@Override
			public int compare(ExtendedHostType host1, ExtendedHostType host2) {			
				int host1AllocatedVNF  = host1.getTotalVNFallocated().intValue();
				int host2AllocatedVNF = host2.getTotalVNFallocated().intValue();
				
				if(host1AllocatedVNF == host2AllocatedVNF)  
					return 0;  
				else if(host1AllocatedVNF > host2AllocatedVNF)  
					return 1;  
				else  
					return -1;  
			}
		});
		
		for(FunctionType virtualFunction: vnfList) {
			minVnfAllocated = Integer.MAX_VALUE;
			targetHost = null;
			
			for(ExtendedHostType host: hostList) {
				// initialize the resource value variables
				numAllocatedNodes += host.getTotalVNFallocated().intValue();
				usedMemory += host.getAvailableStorage().intValue();
				usedStorage += host.getAvailableMemory().intValue();
				
				// search the data into the hashMap
				allocatedVnfList = allocatedNodeMap.get(host.getHostname());
				if(vnfList != null) {
					for(FunctionType allocatedVnf: allocatedVnfList) {
						numAllocatedNodes++;
						usedMemory += allocatedVnf.getRequiredMemory().intValue();
						usedStorage += allocatedVnf.getRequiredStorage().intValue();
					}
				}
				
				// check if the host can contain such node
				if(numAllocatedNodes < host.getMaxVNF().intValue() &&
						usedMemory + virtualFunction.getRequiredMemory().intValue() <= host.getAvailableMemory().intValue() &&
						usedStorage + virtualFunction.getRequiredStorage().intValue() <= host.getAvailableStorage().intValue()) {
					
						if(numAllocatedNodes < minVnfAllocated) 
							targetHost = host;
				}
			}
		
			// put the data into the map 
			if(targetHost == null) 
				throw new AllocationException("Impossible to find a suitable host for vnf " + virtualFunction.getType());
			else {
				allocatedVnfList = allocatedNodeMap.get(targetHost.getHostname());
				if(allocatedVnfList == null) {
					List<FunctionType> functionList = new ArrayList<FunctionType> ();
					functionList.add(virtualFunction);
					allocatedNodeMap.put(targetHost.getHostname(), functionList);
				} else {
					allocatedVnfList.add(virtualFunction);
				}
				// set the hostname into the target list 
				targetHostMap.put(index, targetHost.getHostname());
			}
			
			// update the index 
			index++;
		}
	}
	/*
	 * this function allocate the list of nodes passed as parameter into the host 
	 */
	public void allocateGraph(List<RestrictedNodeType> nodeList, HostDao hostDao) throws InternalServerErrorException {
		int index = 0;
		
		// insert into the host the virtual function allocated
		for(String hostname: allocatedNodeMap.keySet()) {
			
			/*String hostname = targetHostMap.get(index);
			host = hostDao.readHost(hostname);
			
			if(host == null) {
				throw new InternalServerErrorException();
			}
		
			// update the host
			host.setUsedMemory(usedMemory + host.getUsedMemory().intValue());
			host.setUsedStorage(usedStorage + host.getUsedStorage().intValue());
			host.setTotalVNFallocated(vnfAllocated.length() + host.getTotalVNFallocated().intValue());
			
			// update the node
			node.setHostname(hostname);*/
			
			List<FunctionType> vnfAllocated = allocatedNodeMap.get(hostname);
			if(vnfAllocated == null) {
				logger.log(Level.SEVERE, "inconsistent state, the host doesn't contain any node");
				throw new InternalServerErrorException();
			}
			
			for(FunctionType virtualFunction: vnfAllocated) {
				hostDao.updateHost(hostname, virtualFunction);
			}
		}
		
		
	}
	
	
	public void updateHost(List<RestrictedNodeType> nodeList, HostDao hostDao) {
		// insert into the host the deployed node
		int index = 0;
		for(RestrictedNodeType node: nodeList) {
			hostDao.updateHost(targetHostMap.get(index), node);
			index++;
		}
	}
	/*
	public void commit(List<String> nodeNameList) throws AllocationException {
		int index = 0;
		
		if(targetHosts.isEmpty()) 
			throw new AllocationException();
		
		for(String host: targetHosts) {
			hostDao.updateHostDeployedNode(host, nodeNameList.get(index)); 
		}
	}
	
	public void rollback() {
		int index = 0;
		
		// deallocate for each host the virtual function
		for(String hostname: targetHosts) {
			hostDao.updateHostDetail(hostname, targetFunctions.get(index), false);
		}
	}
	
	public String createNewNffg() {
		String nffgId = "Nffg";
		int nffgIdCounter;
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		

		nffgIdCounter = nffgDao.readNffgId();
		nffgId.concat(Integer.toString(nffgIdCounter));
		
		NffgType newNffg = new NffgType();
		newNffg.setName(nffgId);
		
		try {
			newNffg.setDeployTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(dateFormat.format(cal.getTime())));
		} catch (DatatypeConfigurationException e) {
			// impossible to covert the data
			newNffg.setDeployTime(null);
			e.printStackTrace();
		}
		
		nffgDao.createNffg(newNffg);
	
		return nffgId;
	}
	
	public void addNewGraphToDB(GraphType graph) throws ServiceException {
		graphDao.createGraph(graph);
	}*/
}
