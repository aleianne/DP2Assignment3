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

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;

public class NffgResourceService {

	private HostDao hostDao;
	private NffgDao nffgDao;
	private GraphDao graphDao;
	private VnfDao catalogDao;
	
	private static Logger logger = Logger.getLogger(NffgResourceService.class.getName());
	
	public NffgResourceService() {
		// instantiate all the dao object that are used to accomplish the operation of deploy/read of the nffg
		hostDao = HostDao.getInstance();
		nffgDao = NffgDao.getInstance();
		graphDao = GraphDao.getInstance();
		catalogDao = VnfDao.getInstance();
	}
	
	/* method used for the nffgs resource */
	
	/*
	 * return all the nffg deployed into the system
	 */
	public List<NffgType> getNffgs() {
		List<NffgType> nffgList = new ArrayList<NffgType>(nffgDao.readNffgs());		
		return nffgList;
	}
	
	/*
	 * return the nffg given the nffg ID
	 */
	public NffgType getNffg(String nffgId) {
		NffgType nffg = nffgDao.queryNffg(nffgId);
		return nffg;
	}
	
	public List<Nffgs> selectNffgs(String date)  {
		
		
		synchronized(nffgDao) {
			
			
			
		}
		
	}
	
	/* method used for the graph resource */
	
	public GraphType getGraph(String nffgId) {
		return graphDao.readGraph(nffgId);	
	}
	
	public String deployNewNffgGraph(GraphType newGraph) throws ServiceException, AllocationException {
		// synchronize this method in order to avoid 
		// the deployment of differente network nodes into the same host
		
		VnfType nodeFunction;
		GraphDeployer newGraphDeployer = new GraphDeployer();
		
		List<NodeType> nodeList = newGraph.getNodes().getNode();
		
		// synchronize the access to the the host interface during the allocation phase
		synchronized(hostDao) {								
			
			// for each node in the list try to find a suitable host
			for(NodeType node: nodeList) {
				nodeFunction = catalogDao.readVnf(node.getVNF());
							
				if(nodeFunction != null) {	
					// query the database in order to obtain the host 
					if(!newGraphDeployer.findSuitableHost(nodeFunction, node)) {
						// update the host deleting the node info
						newGraphDeployer.rollback();
						logger.log(Level.WARNING, "impossible to find an host for the network node", nodeFunction);
						throw new AllocationException();
					} else {
						logger.log(Level.INFO, "host find correclty!");
					}		
				} else {
					logger.log(Level.WARNING, "the VNF of the specified node is not defined in the catalog of this web-service", node.getVNF());
				}
			}
						
			try {
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
			}				
						
		} // release the lock on the hostDao object			
	}

	public String addNode(String graphId, NodeType newNode) throws ServiceException, AllocationException {
		HostType targetHost = null;
		GraphType targetGraph = graphDao.readGraph(graphId);
		
		if(targetGraph == null) {
			logger.log(Level.SEVERE, "the graph doesn't exist in the system", graphId);
			throw new AllocationException();
		}
			
		VnfType nodeFunction = catalogDao.readVnf(newNode.getVNF());
	
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
			
			// update the graph in memory and send it to Neo4j
			graphDao.updateGraph(graphId, newNode);
			
			hostDao.updateHostDeployedNode(targetHost.getHostname(), newNode.getName());
		}

		return newNode.getName();
	}

	public String addLink(String graphId, LinkType newLink) throws ServiceException, AllocationException {
		
		GraphType graph = graphDao.readGraph(graphId);
		
		if(graph == null) {
			logger.log(Level.WARNING, "there isn't a correspondence between the nffgId and a graph in the system", new Object[] {graphId});
			throw new AllocationException();	
		}
		
		List<LinkType> graphLinkList = graph.getLinks().getLink();
		
		// filter the list in order to check if the link is already in the graph
		Predicate<LinkType> linkPredicate = p-> p.getSourceNode() == newLink.getDestinationNode() && p.getSourceNode() == newLink.getSourceNode();
		
		// update only if is necessary
		if(graphLinkList.stream().filter(linkPredicate).findFirst().get() == null ) {
			graphDao.updateGraph(graphId, newLink);
		} else if(newLink.isOverwrite()){
			graphDao.updateGraph(graphId, newLink);
		} else {
			logger.log(Level.INFO, "the link is already inside the graph");
		}
		
		return newLink.getLinkName();	
	}

	/* method used to get all the reachable host */
	
	public List<HostType> getReachableHost(String nffgId) throws ServiceException {
		Neo4jForwarder neo4jForwarder = new Neo4jForwarder();
		Nodes receivedNodes = neo4jForwarder.getReachableHost(nffgId);
		
		List<HostType> hostList = new ArrayList<HostType>();
		
		for(Node newNode: receivedNodes.getNode()) {
			HostType newHost = hostDao.readHost(newNode.getProperties().getProperty().get(0).getValue());
			hostList.add(newHost);
		}
		
		return hostList;
	}

	private HostType searchHost(VnfType nodeFunction) throws AllocationException {
		HostType targetHost = null;
		int reqStorage = nodeFunction.getRequiredStorage().intValue();
		int reqMemory = nodeFunction.getRequiredMemory().intValue();
		
		Set<HostType> selectedHost = (Set<HostType>) hostDao.queryHost(reqStorage, reqMemory);
		
		if(!selectedHost.isEmpty()) {
			int minNumberOfNodesDeployed = 0;
			
			// find the host that have the minimum number of deployed network node
			for(HostType host: selectedHost) {
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
		
	}
}
