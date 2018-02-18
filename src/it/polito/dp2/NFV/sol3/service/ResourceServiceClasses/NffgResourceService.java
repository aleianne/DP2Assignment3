package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;
import javax.xml.datatype.DatatypeConfigurationException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;

public class NffgResourceService {
	
	private static Logger logger = Logger.getLogger(NffgResourceService.class.getName());
	
	public NffgResourceService() {
		
	}

	// return all the nffgs inside the system
	public NffgsInfoType getAllNffgs() {
		GraphDao graphDao = GraphDao.getInstance();
		NffgsInfoType nffgsInfos = new NffgsInfoType();
		
		synchronized(graphDao) {
			List<NffgGraphType> graphList = new ArrayList<NffgGraphType> (graphDao.readAllGraph());
			
			for(NffgGraphType graph: graphList) {
				NffgsInfoType.NffgInfo nffgInfo = new NffgsInfoType.NffgInfo();
				
				nffgInfo.setNffgName(graph.getNffgName());
				nffgInfo.setDeployDate(graph.getDeployDate());
				nffgsInfos.getNffgInfo().add(nffgInfo);
			}
			return nffgsInfos;
		}
	}
	
	// interrogate the DB in order to obtain the graph
	public NffgGraphType getSingleNffg(String nffgId) {
		return GraphDao.getInstance().readGraph(nffgId);
	}
	
	// return null if there aren't any nffg
	public NffgsInfoType selectNffgs(Date date)  {
		NffgsInfoType nffgsInfos = new NffgsInfoType();
		
		// get the list of graph deployed into the system
		List<NffgGraphType> graphList = new ArrayList<NffgGraphType> (GraphDao.getInstance().readAllGraph());
		
		if(graphList.isEmpty()) 
			return null;
		
		for(NffgGraphType graph: graphList) {
			NffgsInfoType.NffgInfo nffgInfo = new NffgsInfoType.NffgInfo();
			
			if(DateConverter.compareXmlGregorianCalendar(graph.getDeployDate(), date)) {
				nffgInfo.setNffgName(graph.getNffgName());
				nffgInfo.setDeployDate(graph.getDeployDate());
				nffgsInfos.getNffgInfo().add(nffgInfo);
			}
		}
		return nffgsInfos;
	}
	
	public NffgGraphType getGraph(String nffgId) {
		return GraphDao.getInstance().readGraph(nffgId);	
	}
	
	// synchronize the access to the hostDao in order to avoid race condition 
	public String deployNewNffgGraph(NffgGraphType newGraph) throws ServiceException, AllocationException {
		FunctionType nodeFunction;
		List<FunctionType> vnfList = new ArrayList<FunctionType> ();
		List<NodeType> nodeList = newGraph.getNodes().getNode();
		
		HostDao hostDao = HostDao.getInstance();
	
		// add into the function list the VNF of the specified node
		for(NodeType node: nodeList) {
			nodeFunction = VnfDao.getInstance().readVnf(node.getVNF());
			if(nodeFunction == null) {
				logger.log(Level.SEVERE, "the function specified is not available");
				throw new ServiceException();
			}
	
			// add the VNF into the list of function to be deployed
			vnfList.add(nodeFunction);
		}
		
		logger.log(Level.INFO, "try to allocate the function into the system");
		if(nodeList.size() != vnfList.size()) {
			logger.log(Level.SEVERE, "nodeList and vnfList are not of the same size");
			throw new InternalServerErrorException("server encourred in a problem");
		}
		
		GraphAllocator allocator = new GraphAllocator();
		synchronized(hostDao) {
			List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType> (hostDao.readAllHosts());
			allocator.findSelectedHost(vnfList, nodeList, hostDao);
			
			// if the vnf are already allocated don't do anything
			if(!vnfList.isEmpty()) {
				allocator.findBestHost(vnfList, hostList);
				
			}
			
			// update the resource information about each single host
			allocator.allocateGraph(nodeList, hostDao);
		}
		
		try {
			// add the date of the deploy
			newGraph.setDeployDate(DateConverter.getCurrentXmlDate());
		} catch(DatatypeConfigurationException dce) {
			newGraph.setDeployDate(null);
		}
		
		// insert the new graph into the DB
		String nffgName = GraphDao.getInstance().createNffg(newGraph);
		
		// update the hosts with the name of the node 
		synchronized(hostDao) {
			allocator.updateHost(nodeList, hostDao);
		}
		
		return nffgName;	
}

	
	/*private ExtendedHostType searchHost(FunctionType nodeFunction) throws AllocationException {
		ExtendedHostType targetHost = null;
		int reqStorage = nodeFunction.getRequiredStorage().intValue();
		int reqMemory = nodeFunction.getRequiredMemory().intValue();
		
		Set<ExtendedHostType> selectedHost = (Set<ExtendedHostType>) hostDao.queryHost(reqStorage, reqMemory);
		
		if(!selectedHost.isEmpty()) {
			int minNumberOfNodesDeployed = 0;
			
			// find the host that have the minimum number of deployed network node
			for(ExtendedHostType host: selectedHost) {
				if(minNumberOfNodesDeployed > host.getTotalDeployedNode().intValue() &&
						host.getTotalDeployedNode().intValue() < host.getMaxVNF().intValue()) {
	
					targetHost = host;
					minNumberOfNodesDeployed = host.getTotalDeployedNode().intValue();
				}
			}
			
			if(targetHost == null) {
				logger.log(Level.WARNING, "for the specified node is not possible to be deployed into an host");
				throw new AllocationException();
			} else {
				return targetHost;
			}
	
		} else {
			logger.log(Level.WARNING, "for the specified node is not possible to be deployed into an host");
			throw new AllocationException();
		}	
		
	}*/
}
