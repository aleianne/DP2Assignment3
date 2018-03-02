package it.polito.dp2.NFV.sol3.service.DaoClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.Exceptions.GraphNotFoundException;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.NoNodeException;
import it.polito.dp2.NFV.lab3.ServiceException;

public class GraphDao {

	private static ConcurrentMap<String, NffgGraphType> graphMap = new ConcurrentHashMap<String, NffgGraphType>();
	private static ConcurrentMap<String, RestrictedNodeType> nodeIDMap = new ConcurrentHashMap<String, RestrictedNodeType>();
	//private static Map<String, ExtendedLinkType> linkIDMap = new HashMap<String, ExtendedLinkType>();
	
	private static AtomicInteger linkCounter = new AtomicInteger(0);
	private static AtomicInteger nodeCounter = new AtomicInteger(0);
	private static AtomicInteger nffgCounter = new AtomicInteger(0);
	
	private static final String nodeBaseName = "Node-";
	private static final String linkBaseName = "Link-";
	private static final String nffgBaseName = "Nffg-";
	
	private Neo4jServiceManager neo4jXMLclient;
	
	private static GraphDao graphDao = new GraphDao();
	
	public static GraphDao getInstance() {
		return graphDao;
	}
	
	/*
	 * allocate the graph into the system and put it into the neo4j database and into the hashmap
	 */
	public String createNffg(NffgGraphType newNffg) throws ServiceException {
		neo4jXMLclient = Neo4jServiceManager.getInstance();
		
		List<RestrictedNodeType> graphNodeList = newNffg.getNodes().getNode();
		List<ExtendedLinkType> graphLinkList = newNffg.getLinks().getLink();
		Map<String, String> nameResolverMap = new HashMap<String, String>();
		
		// create the nffg name
		String nffgName = nffgBaseName .concat(Integer.toString(nffgCounter.incrementAndGet()));
		newNffg.setNffgName(nffgName);
		
		// update the list of nodes
		for(RestrictedNodeType node: graphNodeList) {
			String newNodeName = nodeBaseName.concat(Integer.toString(nodeCounter.incrementAndGet()));
			String oldNodeName = node.getName();
			nameResolverMap.put(oldNodeName, newNodeName);
			
			node.setNfFg(nffgName);
			node.setName(newNodeName);
			
			// put the node into the map 
			nodeIDMap.put(newNodeName, node);
		}
		
		// update the list of links
		for(ExtendedLinkType link: graphLinkList) {
			String linkName = linkBaseName.concat(Integer.toString(linkCounter.incrementAndGet()));
			String oldDestNodeName = link.getDestinationNode();
			String oldSrcNodeName = link.getSourceNode();
						
			link.setDestinationNode(nameResolverMap.get(oldDestNodeName));
			link.setSourceNode(nameResolverMap.get(oldSrcNodeName));
			link.setLinkName(linkName);
		}
		
		graphMap.put(newNffg.getNffgName(), newNffg);
		
		/*
		 *  forward the graph to the remote database
		 */
		Node neo4jNode = new Node();	
		Labels nodeLabel = new Labels();
		Property nodeProperty = new Property();
		Relationship neo4jRelationship = new Relationship();
		
		// declare a new property inside the neo4jNode
		neo4jNode.setProperties(new Properties());
		neo4jNode.getProperties().getProperty().add(nodeProperty);
		
		nodeLabel.getLabel().add("Node");
		nodeLabel.getLabel().add("node-name");
		
		for(RestrictedNodeType node: graphNodeList) {
			// create a neo4j node for the forwarding ;
			neo4jNode.getProperties().getProperty().get(0).setName("name");
			neo4jNode.getProperties().getProperty().get(0).setValue(node.getName());
			
			// modify the second label that represent the name of the node
			String nodeName = nodeLabel.getLabel().get(1);
			nodeName = node.getName();
			neo4jXMLclient.postNode(neo4jNode, nodeLabel);
			
			// set the relationship info between the node and the host
			neo4jRelationship.setDstNode(node.getHostname());
			neo4jRelationship.setSrcNode(node.getName());
			neo4jRelationship.setType("AllocatedOn");
			neo4jXMLclient.postRelationship(neo4jRelationship);
		}
		
		for(ExtendedLinkType link: graphLinkList) {
			// set the link information between two nodes
			neo4jRelationship.setDstNode(link.getDestinationNode());
			neo4jRelationship.setSrcNode(link.getSourceNode());
			neo4jRelationship.setType("ForwardTo");
			neo4jXMLclient.postRelationship(neo4jRelationship);
		}
		
		return nffgName;
	}
	
	/*
	 * read a single graph given the nffgId
	 */
	public NffgGraphType readGraph(String nffgId) {
		return graphMap.get(nffgId);
	}
	
	/*
	 * read all the nffg that are stored into the database
	 */
	public Collection<NffgGraphType> readAllGraph() {
		return graphMap.values();
	}
	
	/*
	 * update the list of nodes insied the graph, locally and into the database
	 * if the 
	 */
	public void updateGraph(String nffgId, RestrictedNodeType newNode) throws ServiceException, GraphNotFoundException  {
		NffgGraphType queryResultGraph = graphMap.get(nffgId);
		
		if(queryResultGraph == null) {
			throw new GraphNotFoundException();
		} else {
		
			neo4jXMLclient = Neo4jServiceManager.getInstance();
			String nodeName = nodeBaseName;
			nodeName.concat(Integer.toString(nodeCounter.incrementAndGet()));
				
			// genetate a new node name
			newNode.setName(nodeName);
				
			// create a ne4j node for the forwarding
			Node neo4jNode = new Node();
			Property nodeProperty = new Property();
			Labels nodeLabels = new Labels();
			
			// synchronize here the query result graph
			synchronized(queryResultGraph) {
			
				nodeProperty.setName("name");
				nodeProperty.setValue(newNode.getName());
					
				nodeLabels.getLabel().add("Node");
				nodeLabels.getLabel().add(nffgId);
					
				neo4jNode.setProperties(new Properties());
				neo4jNode.getProperties().getProperty().add(nodeProperty);
					
				queryResultGraph.getNodes().getNode().add(newNode);
			
			}
			
			// forward the node to the database
			neo4jXMLclient.postNode(neo4jNode, nodeLabels);
				
			// forward the relationship between the node and the host where is allocated
			Relationship neo4jRelationship = new Relationship();
			neo4jRelationship.setDstNode(newNode.getHostname());
			neo4jRelationship.setSrcNode(newNode.getName());
			neo4jRelationship.setType("FrowardTo");
			neo4jXMLclient.postRelationship(neo4jRelationship);
		}
	}
	
	/*
	 * insert into the graph specified by the nffgId a new link
	 */
	public void updateGraph(String nffgId, ExtendedLinkType newLink) throws ServiceException, LinkAlreadyPresentException, InternalServerErrorException, GraphNotFoundException, NoNodeException {
		NffgGraphType queryResultGraph = graphMap.get(nffgId);
		
		if(queryResultGraph == null) {
			throw new InternalServerErrorException("the graph " + nffgId + " doesn't exist");
		} else {
			String linkName = linkBaseName;
			
			// synchronize the access to the single graph where the link should allocated
			synchronized(queryResultGraph) {
			
				List<ExtendedLinkType> graphLinkList = queryResultGraph.getLinks().getLink();
				List<RestrictedNodeType> GraphNodeList = queryResultGraph.getNodes().getNode();
				
				// filter the list in order to obtain all the links that have a specified source node and a specified destination node
				Predicate<ExtendedLinkType> linkPredicate = p-> p.getSourceNode() == newLink.getDestinationNode() && p.getSourceNode() == newLink.getSourceNode();
				
				// filter the list of nodes inside the graph, check if there are the nodes specified
				Predicate<RestrictedNodeType> nodePredicate = p-> p.getName() == newLink.getDestinationNode();
				Predicate<RestrictedNodeType> nodePredicate2 = p-> p.getName() == newLink.getSourceNode();
				
				if(GraphNodeList.stream().filter(nodePredicate).findFirst().get() == null
						|| GraphNodeList.stream().filter(nodePredicate2).findFirst().get() == null) 
					
					throw new NoNodeException();
				
				if(graphLinkList.stream().filter(linkPredicate).findFirst().get() == null && !newLink.isOverwrite()) 
					throw new LinkAlreadyPresentException();
	
				// update the list of links
				newLink.setLinkName(linkName.concat(Integer.toString(linkCounter.incrementAndGet())));
				queryResultGraph.getLinks().getLink().add(newLink);
		
			}
			
			// forward the relationship that represent the link in Neo4j Database
			Relationship neo4jRelationship = new Relationship();
			neo4jRelationship.setDstNode(newLink.getDestinationNode());
			neo4jRelationship.setSrcNode(newLink.getSourceNode());
			neo4jRelationship.setType("FrowardTo");	
			neo4jXMLclient.postRelationship(neo4jRelationship);
		}
	}
	
	/*
	 *  this method is not implemented due to the fact that the assignment doesn't require it
	 */
	public void deleteGraph() {}
	
	
	/*
	 *  query the map in order to get the node correspondent to the name
	 */
	public RestrictedNodeType queryGraph(String nodename) {
		return nodeIDMap.get(nodename);
	}
}
