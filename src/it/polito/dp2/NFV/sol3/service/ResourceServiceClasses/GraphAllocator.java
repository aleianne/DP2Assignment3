package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.HostType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.VnfType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
	private Map<String, List<VnfType>> allocatedNodeMap;
	
	private static Logger logger = Logger.getLogger(GraphAllocator.class.getName());
	
	public GraphAllocator() {
		allocatedNodeMap = new HashMap<String, List<VnfType>> ();
		targetHostMap = new HashMap<Integer, String> ();
	}
	
	/*public boolean findSuitableHost(VnfType functionToBeDeployed, NodeType newNode) {
		int minNode;
		int reqStorage = functionToBeDeployed.getRequiredStorage().intValue();
		int reqMemory = functionToBeDeployed.getRequiredMemory().intValue();
		
		HostType hostFound = null;
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
		
		Set<HostType> selectedHost = new HashSet<HostType>(hostDao.queryHost(reqStorage, reqMemory));
		
		if(!selectedHost.isEmpty()) {
			minNode = Integer.MAX_VALUE;
			
			// find the host that have the minimum number of deployed network node
			for(HostType host: selectedHost) {
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

	
	public void findSelectedHost(List<VnfType> vnfList, List<NodeType> nodeList, HostDao hostDao) {
		
		/* 
		 * in this arrays at the same position there is a correspondance between the node and is virtual function
		 */
		
		int index = 0;
		int usedStorage, usedMemory, totalVNF;
		List<Integer> indexList = new ArrayList<Integer> ();
		
		for(nodeType node: nodeList) {
			String hostname = node.getHostname();
			
			if(hostname != null) {
				HostType host = hostDao.readHost(hostname);
				
				if(host != null) {
					usedStorage = host.getUsedStorage().intValue();
					usedMemory = host.getUsedMemory().intValue();
					totalVNF = host.getTotalVNFallocated().intValue();
					
					List<VnfType> vnfAllocatedList = allocatedNodeMap.get(hostname);
					if(vnfAllocatedList != null) {
						for(VnfType virtulFunction: vnfAllocatedList) {
							totalVNF++;
							usedMemory += vnfAllocatedList.getRequiredMemory().intValue();
							usedStorage += vnfAllocatedList.getRequiredStorage().gtintValue();
						}
					}
					
					if(totalVNF < host.getMaxVnf() &&
							usedMemory + vnfList.get(index).getRequiredMemory().intValue() <= host.getAvailableMemory().intValue() &&
							usedStorage + vnfList.get(index).getRequiredStorage().getIntaValue() <= host.getAvaialbleStorage().intValue()) {
						
						if(vnfAllocatedList != null) {
							vnfAllocatedList.add(vnfList.get(index));
						} else {
							allocatedNodeMap.put(hostname, new ArrayList<VnfType>(vnfList.get(index)));
						}
						// insert the vnf into the list of elment ot be removed and add the hostname into the list of target
						indexList.add(index);
						targetHostMap.put(index, hostname);
					}
				}
			}
			index++;
		}
		// delete the allocated function from the list
		for(Integer i: indexList) {
			vnfList.remove(i.intValue());
		}
	}
	
	public void findBestHost(List<VnfType> vnfList, List<HostType> hostList) throws AllocationException{
		int minVnfAllocated, numAllocatedNodes, usedMemory, usedStorage;
		HostType targetHost;
		List<VnfType> allocatedVnfList;
		int index = 0;
		
		// TODO order the list 
		Collection.sort(hostList, new Comparator() {
			
		});
		
		for(VnfType virtualFunction: vnfList) {
			minVnfAllocated = Integer.MAX_VALUE;
			targetHost = null;
			
			for(HostType host: hostList) {
				// initialize the resource value variables
				numAllocatedNodes += host.getTotaVNFallocated().intValue();
				usedMemory += host.getAvailableeStorage().intValue();
				usedStorage += host.getAvailableMemory().intValue();
				
				// search the data into the hashMap
				allocatedVnfList = allocatedNodeMap.get(host.getName());
				if(vnfList != null) {
					for(VnfType allocatedVnf: allocatedVnfList) {
						numAllocatedNodes++;
						usedMemory += allocatedVnf.getUsedMemory().intValue();
						usedStorage += allocatedVnf.getUsedStorage().intValue();
					}
				}
				
				// check if the host can contain such node
				if(numAllocatedNodes < host.getMaxVNF().intValue() &&
						usedMemory + virtualFunction.getRequiredMemory().intValue() <= host.getAvailableMemory() &&
						usedStorage + virtualFunction.getRequiredStorage().intValue() <= host.getAvailableStorage().intValue()) {
					
						if(numAllocatedNodes < minVnfAllocated) 
							targetHost = host;
						
				}
			}
		
			// put the data into the map 
			if(targetHost == null) 
				throw new AllocationException("Impossible to find a suitable host for vnf " + virtualFunction.getType());
			else {
				allocatedVnfList = allocatedNodeMap.get(targetHost.getName());
				if(allocatedVnfList == null) {
					allocatedNodeMap.put(targetHost.getName(), new ArrayList<VnfType> (virtualFunction));
				} else {
					allocatedVnfList.add(virtualFunction);
				}
				// set the hostname into the target list 
				targetHostMap.put(index, targetHost.getName());
			}
			
			// update the index 
			index++;
		}
	}
	
	public void allocateGraph(List<NodeType> nodeList, HostDao hostDao) throws InternalServerErrorException {
		int index = 0;
		HostType host;
		
		int usedMemory, usedStorage;
		int totalVNF;
		
		for(NodeType node: nodeList) {
			String hostname = targetHostMap.get(index);
			host = hostDao.readHost(hostname);
			
			if(host == null) {
				throw new InternalServerErrorException();
			}
			
			List<VnfType> vnfAllocated = allocatedNodeMap.get(hostname);
			if(vnfAllocated == null) 
				throw new InternalServerErrorException();
			
			for(VnfType virtualFunction: vnfAllocated) {
				usedMemory += virtualFunction.getRequiredMemory().intValue();
				usedStorage += virtualFunction.getRequiredStorage().intValue();
			}
		
			// update the host
			host.setUsedMemory(usedMemory + host.getUsedMemory().intValue());
			host.setUsedStorage(usedStorage + host.getUsedStorage().intValue());
			host.setTotalVNFallocated(vnfAllocated.length() + host.getTotalVNFallocated().intValue());
			
			// update the node
			node.setHostname(hostname);
			
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
