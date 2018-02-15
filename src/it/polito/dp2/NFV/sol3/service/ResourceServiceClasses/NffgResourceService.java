package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.*;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonValue.ValueType;
import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;

public class NffgResourceService {

	private HostDao hostDao;
	private GraphDao graphDao;
	private VnfDao catalogDao;
	
	private static Logger logger = Logger.getLogger(NffgResourceService.class.getName());
	
	public NffgResourceService() {
		// instantiate all the dao object that are used to accomplish the operation of deploy/read of the nffg
		hostDao = HostDao.getInstance();
		graphDao = GraphDao.getInstance();
		catalogDao = VnfDao.getInstance();
	}
	
	/* method used for the nffgs resource */
	
	// return all the nffgs inside the system
	public List<NffgType> getNffgs() {
		List<NffgType> nffgList = new ArrayList<NffgType>(nffgDao.readNffgs());		
		return nffgList;
	}
	
	// interrogate the DB in order to obtain the graph
	public NffgType getNffg(String nffgId) {
		//NffgType nffg = nffgDao.queryNffg(nffgId);
		return nffg;
	}
	
	public List<NffgType> selectNffgs(String date)  {
		//synchronized(nffgDao) {
				
		//}
	}
	
	/* method used for the graph resource */
	
	public NffgType getGraph(String nffgId) {
		return graphDao.readGraph(nffgId);	
	}
	
	// synchronize the access to the hostDao in order to avoid race condition 
	public String deployNewNffgGraph(NffgType newGraph) throws ServiceException, AllocationException {
		FunctionType nodeFunction;
		List<FunctionType> vnfList = new ArrayList<FunctionType> ();
		List<NodeType> nodeList = newGraph.getNodes().getNode();
		
		HostDao hostDao = HostDao.getInstance();
	
		// synchronize the access to the the host interface during the allocation phas	
		for(NodeType node: nodeList) {
			nodeFunction = catalogDao.readVnf(node.getVNF());
			if(nodeFunction == null) {
				logger.log(Level.SEVERE, "the function specified is not available");
				throw new ServiceException();
			}
					
			vnfList.add(nodeFunction);
				
			/*if(nodeFunction != null) {	
			// query the database in order to obtain the host 
			if(!newGraphDeployer.findSuitableHost(nodeFunction, node)) {
			// update the host deleting the node info
			newGraphDeployer.rollback();
			logger.log(Level.WARNING, "impossible to find an host for the network node", nodeFunction);
			throw new AllocationException();
			} else 
			logger.log(Level.INFO, "host find correclty!");
			} else 
			logger.log(Level.WARNING, "the VNF of the specified node is not defined in the catalog of this web-service", node.getVNF());*/
			
		}
		
		try {
			GraphAllocator allocator = new GraphAllocator();
			synchronized(hostDao) {
				List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType> (hostDao.readAllHosts());
				
				//TODO change the host map 
				allocator.findSelectedHost(vnfList, nodeList);
				allocator.findBestHost(vnfList, hostList);
			}
			// create a new graph in the database
			GraphDao.getInstance().createGraph(newGraph);
			
			
			// TODO update the host with the name of the nodes
			
			synchronized(hostDao) {
				allocator.allocateGraph(nodeList, hostDao);
			}
			
		} catch(Exception e) {
			
		}
		
		
		
						
		/*try {
			// create a new nffg
			String nffgName = newGraphDeployer.createNewNffg();
			newGraph.setNffgId(nffgName);
				
			// create a new graph 
			newGraphDeployer.addNewGraphToDB(newGraph);
				
			List<String> nodeNameList = new ArrayList<String>();
			for(NodeType node: newGraph.getNodes().getNode()) {
				nodeNameList.add(node.getName());
			}
				
			// update the host with the node id 
			newGraphDeployer.commit(nodeNameList);
				
			return newGraph.getNffgId();
				
		} catch(ServiceException se) {
			newGraphDeployer.rollback();
			throw se;
		}	*/	
			
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
