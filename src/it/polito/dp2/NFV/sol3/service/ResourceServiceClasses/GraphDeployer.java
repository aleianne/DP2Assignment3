package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;
public class GraphDeployer {

	private HostDao hostDao;
	private NffgDao nffgDao;
	private GraphDao graphDao;
	
	List<String> targetHosts; 
	List<VnfType> targetFunctions;
	List<String> targetNodeName;
	
	private static Logger logger = Logger.getLogger(GraphDeployer.class.getName());
	
	public GraphDeployer() {
		hostDao = HostDao.getInstance();
		nffgDao = NffgDao.getInstance();
		graphDao = GraphDao.getInstance();
		
		targetHosts = new ArrayList<String>();
		targetFunctions = new ArrayList<VnfType>();
	}
	
	public boolean findSuitableHost(VnfType functionToBeDeployed, NodeType newNode) {
		int minNode;
		HostType hostFound = null;
		
		int reqStorage = functionToBeDeployed.getRequiredStorage().intValue();
		int reqMemory = functionToBeDeployed.getRequiredMemory().intValue();
		
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
			for(HostType host: selectedHost) 
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
	}
	
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
	}
}
