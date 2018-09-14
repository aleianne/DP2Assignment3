package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.AllocationException;

public class GraphAllocator {

    private static Logger logger = Logger.getLogger(GraphAllocator.class.getName());
    private Map<String, Integer> targetHostMap;
    private Map<Integer, String> reverseTargetHostMap;
    private Map<String, List<FunctionType>> vnfAllocatedIntoHostMap;

    public GraphAllocator() {
        vnfAllocatedIntoHostMap = new HashMap<String, List<FunctionType>>();
        // has been used an hashmap instead of a list because there isn't an order of node allocation
        reverseTargetHostMap = new HashMap<Integer, String> ();
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
         * in those arrays at the same position there is a correspondence between the node and his virtual function
         */

        int index = 0;
        int usedStorage, usedMemory, totalVNF;
        List<FunctionType> vnfAllocatedList = new ArrayList<FunctionType>();
        List<FunctionType> vnfAllocatedIntoHostList;

        for (RestrictedNodeType node : nodeList) {
            String hostname = node.getHostname();

            if (hostname != null) {
                ExtendedHostType host = hostDao.readHost(hostname);
                logger.log(Level.INFO, "try to allocate function n: " + index + " " + vnfList.get(index).getName() + " on host " + hostname);

                if (host != null) {
                    usedStorage = host.getStorageUsed().intValue();
                    usedMemory = host.getMemoryUsed().intValue();
                    totalVNF = host.getTotalVNFallocated().intValue();

                    vnfAllocatedIntoHostList = vnfAllocatedIntoHostMap.get(hostname);
                    if (vnfAllocatedIntoHostList != null) {
                        for (FunctionType virtualFunction : vnfAllocatedIntoHostList) {
                            totalVNF++;
                            usedMemory += virtualFunction.getRequiredMemory().intValue();
                            usedStorage += virtualFunction.getRequiredStorage().intValue();
                        }
                    }

                    if (totalVNF < host.getMaxVNF().intValue() &&
                            usedMemory + vnfList.get(index).getRequiredMemory().intValue() <= host.getAvailableMemory().intValue() &&
                            usedStorage + vnfList.get(index).getRequiredStorage().intValue() <= host.getAvailableStorage().intValue()) {

                        // insert the function into the map of the allocated host
                        updateAllocatedVnfMap(vnfAllocatedIntoHostList, vnfList.get(index), hostname);

                        // insert the vnf into the list of element ot be removed and add the hostname into the list of target
                        vnfAllocatedList.add(vnfList.get(index));
                        reverseTargetHostMap.put(index, hostname);
                    }
                }
            }
            index++;
        }
        // delete the function that has been selected from the list
        for (FunctionType function : vnfAllocatedList) {
            vnfList.remove(function);
        }
    }


    /*
     *  find the best suitable host using a best fit descreasing algorithm
     */
    public void findBestHost(List<FunctionType> vnfList, List<ExtendedHostType> hostList) throws AllocationException {
        int minVnfAllocated, numAllocatedNodes, usedMemory, usedStorage;
        ExtendedHostType targetHost;
        List<FunctionType> allocatedVnfList;
        int index = 0;

        minVnfAllocated = numAllocatedNodes = usedMemory = usedStorage = 0;

        /*
         *  implements a custom compare method for the sorting operation
         *  performed before the algorithm application
         */
        Collections.sort(hostList, (Comparator<ExtendedHostType>) (host1, host2) -> {
            int host1AllocatedVNF = host1.getTotalVNFallocated().intValue();
            int host2AllocatedVNF = host2.getTotalVNFallocated().intValue();

            return Integer.compare(host1AllocatedVNF, host2AllocatedVNF);
        });

        for (FunctionType virtualFunction : vnfList) {
            minVnfAllocated = Integer.MAX_VALUE;
            targetHost = null;

            for (ExtendedHostType host : hostList) {
                // initialize the resource value variables
                numAllocatedNodes = host.getTotalVNFallocated().intValue();
                usedMemory = host.getMemoryUsed().intValue();
                usedStorage = host.getStorageUsed().intValue();

                // search the data into the hashMap
                allocatedVnfList = vnfAllocatedIntoHostMap.get(host.getHostname());
                if (allocatedVnfList != null) {
                    for (FunctionType allocatedVnf : allocatedVnfList) {
                        numAllocatedNodes++;
                        usedMemory += allocatedVnf.getRequiredMemory().intValue();
                        usedStorage += allocatedVnf.getRequiredStorage().intValue();
                    }
                }

                // check if the host can contain such node
                if (numAllocatedNodes < host.getMaxVNF().intValue() &&
                        usedMemory + virtualFunction.getRequiredMemory().intValue() <= host.getAvailableMemory().intValue() &&
                        usedStorage + virtualFunction.getRequiredStorage().intValue() <= host.getAvailableStorage().intValue()) {

                    if (numAllocatedNodes < minVnfAllocated)
                        targetHost = host;
                }
            }

            // put the data into the map
            if (targetHost == null) {
                logger.log(Level.INFO, "Impossible to find a suitable host for vnf " + virtualFunction.getType());
                throw new AllocationException();
            } else {
                allocatedVnfList = vnfAllocatedIntoHostMap.get(targetHost.getHostname());
                // update the allocated vnf map
                updateAllocatedVnfMap(allocatedVnfList, virtualFunction, targetHost.getHostname());
                // set the hostname into the target list
                reverseTargetHostMap.put(index, targetHost.getHostname());
            }

            // update index
            index++;
        }
    }

    /*
     * this function allocate the list of nodes passed as parameter into the host
     */
    public void allocateGraph(List<RestrictedNodeType> nodeList, HostDao hostDao) throws InternalServerErrorException {
        // insert into the host the virtual function allocated
        for (String hostname : vnfAllocatedIntoHostMap.keySet()) {
            List<FunctionType> vnfAllocated = vnfAllocatedIntoHostMap.get(hostname);

            if (vnfAllocated == null) {
                logger.log(Level.SEVERE, "inconsistent state, the host doesn't contain any node");
                logger.log(Level.SEVERE, "return error 500");
                throw new InternalServerErrorException();
            }

            // for each function that has been selected the suitable host by the algorithm
            // update the storage and memory used, finally add the deployed node into the host
            int index = 0;
            for (FunctionType virtualFunction : vnfAllocated) {
                hostDao.updateHost(hostname, virtualFunction);
                //hostDao.updateHost(targetHostMap.get(index), nodeList.get(index));
            }
        }

        int index = 0;
        for (RestrictedNodeType node : nodeList) {
            hostDao.updateHost(reverseTargetHostMap.get(index), nodeList.get(index));
            logger.log(Level.INFO, "the target host is " + reverseTargetHostMap.get(index) + " the node is " + nodeList.get(index));
            index++;
        }
    }

    public void changeHostnameValueInGraph(List<RestrictedNodeType> nodeList) {
        int index = 0;

        for (RestrictedNodeType node : nodeList) {
            // insert into the node the information about the target host
            node.setHostname(reverseTargetHostMap.get(index));
            index++;
        }
    }


    private void updateAllocatedVnfMap(List<FunctionType> allocatedVnfList, FunctionType virtualFunction, String hostname) {
        if (allocatedVnfList == null) {
            List<FunctionType> functionList = new ArrayList<FunctionType>();
            functionList.add(virtualFunction);
            vnfAllocatedIntoHostMap.put(hostname, functionList);
        } else {
            allocatedVnfList.add(virtualFunction);
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
