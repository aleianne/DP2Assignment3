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
	
	public NffgResourceService() {}

	// return the infos relative to all the nffgs deployed into the system
	// get all the graph from the database and put the deploy-date and the graph-name into a list of nffgsInfoType
	public NffgsInfoType getAllNffgs() {
		GraphDao graphDao = GraphDao.getInstance();
		NffgsInfoType nffgsInfos = new NffgsInfoType();
		
		List<NffgGraphType> graphList = new ArrayList<NffgGraphType> (graphDao.readAllGraph());
			
		// add the date and the graph name into the list of nffg information
		for(NffgGraphType graph: graphList) {
			NffgsInfoType.NffgInfo nffgInfo = new NffgsInfoType.NffgInfo();
			nffgInfo.setNffgName(graph.getNffgName());
			nffgInfo.setDeployDate(graph.getDeployDate());
			nffgsInfos.getNffgInfo().add(nffgInfo);
		}
		
		return nffgsInfos;
	}
	
	// interrogate the DB in order to obtain the graph
	public NffgGraphType getSingleNffg(String nffgId) {
		return GraphDao.getInstance().readGraph(nffgId);
	}
	
	// return a null value if the list of deployde date is empty
	public NffgsInfoType selectNffgs(Date date)  {
		NffgsInfoType nffgsInfos = new NffgsInfoType();
		GraphDao graphDao = GraphDao.getInstance();
	
		// get the list of graph deployed into the system
		List<NffgGraphType> graphList = new ArrayList<NffgGraphType> (graphDao.readAllGraph());
			
		if(graphList.isEmpty()) 
			return null;
			
		// add the relevant info of the deployd nffg into the nffgsInfo xml data structure in xml
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
	
	// synchronize the access to the hostDao in order to avoid race condition 
	public String deployNewNffgGraph(NffgGraphType newGraph) throws ServiceException, AllocationException {
		FunctionType nodeFunction;
		List<FunctionType> vnfList = new ArrayList<FunctionType> ();
		List<NodeType> nodeList = newGraph.getNodes().getNode();
		String nffgName;
		
		HostDao hostDao = HostDao.getInstance();
		GraphDao graphDao = GraphDao.getInstance();
	
		// for each node add the correspondent vnf to be deployded into the list of vnf
		for(NodeType node: nodeList) {
			// get the correspondent vnf from the database
			nodeFunction = VnfDao.getInstance().readVnf(node.getVNF());
			if(nodeFunction == null) {
				logger.log(Level.SEVERE, "the function specified is not available");
				throw new ServiceException();
			}
			vnfList.add(nodeFunction);
		}
		
		logger.log(Level.INFO, "try to allocate the function into the system");
		
		if(nodeList.size() != vnfList.size()) {
			logger.log(Level.SEVERE, "nodeList and vnfList are not of the same size");
			throw new InternalServerErrorException("server encourred in a problem");
		}
		
		// instantiate a new graph deployer
		GraphAllocator allocator = new GraphAllocator();
		
		synchronized(hostDao) {
			List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType> (hostDao.readAllHosts());
			allocator.findSelectedHost(vnfList, nodeList, hostDao);
			
			// if the vnf are already allocated don't do anything
			if(!vnfList.isEmpty()) 
				allocator.findBestHost(vnfList, hostList);
			
			// update the resource information about each single host
			allocator.allocateGraph(nodeList, hostDao);
			
			try {
				// add the date of the deploy
				newGraph.setDeployDate(DateConverter.getCurrentXmlDate());
			} catch(DatatypeConfigurationException dce) {
				newGraph.setDeployDate(null);
			}
			
			nffgName = GraphDao.getInstance().createNffg(newGraph);
			
			// update the hosts with the name of the node 
			allocator.updateHost(nodeList, hostDao);
		}
		return nffgName;	
	}

}
