package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
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
		NffgsInfoType nffgsInfos = new NffgsInfoType();
		
		List<NffgGraphType> graphList = new ArrayList<NffgGraphType> (GraphDao.getInstance().readAllGraphs());
			
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
	public NffgsInfoType selectNffgs(String date)  {
		NffgsInfoType nffgsInfos = new NffgsInfoType();
		DateConverter dateConverter = new DateConverter();
	
		// get the list of graph deployed into the system
		List<NffgGraphType> graphList = new ArrayList<NffgGraphType> (GraphDao.getInstance().readAllGraphs());
			
		if(graphList.isEmpty()) {
			logger.log(Level.INFO, "server doesn't contain any nffg");
			return null;
		}
			
		// add the relevant info of the deployd nffg into the nffgsInfo xml data structure in xml
		for(NffgGraphType graph: graphList) {
			NffgsInfoType.NffgInfo nffgInfo = new NffgsInfoType.NffgInfo();
				
			try {
				Calendar inputDate = dateConverter.convertCalendar(date);
				Calendar graphDate = dateConverter.fromXMLGregorianCalendar(graph.getDeployDate());
				if(dateConverter.compareCalendar(graphDate, inputDate)) {
					nffgInfo.setNffgName(graph.getNffgName());
					nffgInfo.setDeployDate(graph.getDeployDate());
					nffgsInfos.getNffgInfo().add(nffgInfo);
				}
			} catch(DatatypeConfigurationException se) {
				logger.log(Level.SEVERE, "impossible to convert the Calendar instance to an xml gregorian calendar instance: return all the nffgd in the system");
				return getAllNffgs();
			}
			
		}
		return nffgsInfos;
	}
	
	// synchronize the access to the hostDao in order to avoid race condition 
	public void deployNewNffgGraph(NffgGraphType newGraph) throws ServiceException, AllocationException, BadRequestException{
		FunctionType nodeFunction;
		List<FunctionType> vnfList = new ArrayList<FunctionType> ();
		List<RestrictedNodeType> nodeList = newGraph.getNodes().getNode();
		String nffgName;
		
		HostDao hostDao = HostDao.getInstance();
		GraphDao graphDao = GraphDao.getInstance();
		
		// if the node list of the graph is empty return an exception
		if(nodeList.isEmpty()) {
			logger.log(Level.SEVERE, "the graph received by the server doesn't contain any node");
			throw new BadRequestException();
		}
	
		// for each node add the correspondent vnf to be deployded into the list of vnf
		for(RestrictedNodeType node: nodeList) {
			nodeFunction = VnfDao.getInstance().readVnf(node.getVNF());
			if(nodeFunction == null) {
				logger.log(Level.SEVERE, "the function specified is not available");
				throw new BadRequestException();
			}
			vnfList.add(nodeFunction);
		}
		
		if(nodeList.size() != vnfList.size()) {
			logger.log(Level.SEVERE, "nodeList and vnfList are not of the same size");
			throw new InternalServerErrorException();
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
				DateConverter dateConverter = new DateConverter();
				newGraph.setDeployDate(dateConverter.getCurrentXmlDate());
			} catch(DatatypeConfigurationException dce) {
				logger.log(Level.WARNING, "impossible to create a new XML Gregorian Calendar: " + dce.getMessage());
				newGraph.setDeployDate(null);
			}
			
			// update a new host into the database
			GraphDao.getInstance().createNffg(newGraph);
			allocator.updateHost(nodeList, hostDao);
		}	
	}

}
