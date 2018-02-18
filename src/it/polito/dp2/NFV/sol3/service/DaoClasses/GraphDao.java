package it.polito.dp2.NFV.sol3.service.DaoClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
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
import it.polito.dp2.NFV.lab3.ServiceException;

public class GraphDao {

	private static ConcurrentMap<String, NffgGraphType> graphMap = new ConcurrentHashMap<String, NffgGraphType>();
	private static Map<String, NodeType> nodeIDMap = new HashMap<String, NodeType>();
	private static Map<String, ExtendedLinkType> linkIDMap = new HashMap<String, ExtendedLinkType>();
	
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
	
	/* DAO methods */
	
	/*
	 * allocate the graph into the system and put it into the neo4j database and into the hashmap
	 */
	public String createNffg(NffgGraphType newNffg) throws ServiceException {
		neo4jXMLclient = Neo4jServiceManager.getInstance();
		
		List<NodeType> nodeList = newNffg.getNodes().getNode();
		List<ExtendedLinkType> linkList = newNffg.getLinks().getLink();
		Map<String, String> nameResolverMap = new HashMap<String, String>();
		
		// create the nffg name
		String nffgName = nffgBaseName .concat(Integer.toString(nffgCounter.incrementAndGet()));
		
		newNffg.setNffgName(nffgName);
		
		// update the list of nodes
		for(NodeType node: nodeList) {
			String newNodeName = nodeBaseName.concat(Integer.toString(nodeCounter.incrementAndGet()));
			String oldNodeName = node.getName();
			nameResolverMap.put(oldNodeName, newNodeName);
			
			node.setNfFg(newNffg.getNffgName());
			node.setName(newNodeName);
		}
		
		// update the list of links
		for(ExtendedLinkType link: linkList) {
			String linkName = linkBaseName.concat(Integer.toString(linkCounter.incrementAndGet()));
			String oldDestNodeName = link.getDestinationNode();
			String oldSrcNodeName = link.getSourceNode();
						
			link.setDestinationNode(nameResolverMap.get(oldDestNodeName));
			link.setSourceNode(nameResolverMap.get(oldSrcNodeName));
			link.setLinkName(linkName);
		}
		
		graphMap.put(newNffg.getNffgName(), newNffg);
		
		// forward all the information about the graph to Neo4jSimpleXML
		Node neo4jNode = new Node();	
		Labels nodeLabel = new Labels();
		Property nodeProperty = new Property();
		Relationship neo4jRelationship = new Relationship();
		
		// declare a new property inside the neo4jNode
		neo4jNode.setProperties(new Properties());
		neo4jNode.getProperties().getProperty().add(nodeProperty);
		
		nodeLabel.getLabel().add("Node");
		nodeLabel.getLabel().add("node-name");
		
		for(NodeType node: nodeList) {
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
		
		for(ExtendedLinkType link: linkList) {
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
	
	public Collection<NffgGraphType> readAllGraph() {
		return graphMap.values();
	}
	
	/*
	 * update the list of nodes insied the graph, locally and into the database
	 */
	public boolean updateGraph(String nffgId, NodeType newNode) throws ServiceException {
		NffgGraphType queryResultGraph = graphMap.get(nffgId);
		
		if(queryResultGraph == null) {
			return false;
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
				
			nodeProperty.setName("name");
			nodeProperty.setValue(newNode.getName());
				
			nodeLabels.getLabel().add("Node");
			nodeLabels.getLabel().add(nffgId);
				
			neo4jNode.setProperties(new Properties());
			neo4jNode.getProperties().getProperty().add(nodeProperty);
				
			queryResultGraph.getNodes().getNode().add(newNode);
			neo4jXMLclient.postNode(neo4jNode, nodeLabels);
				
			// set the link information between two nodes
			Relationship neo4jRelationship = new Relationship();
			neo4jRelationship.setDstNode(newNode.getHostname());
			neo4jRelationship.setSrcNode(newNode.getName());
			neo4jRelationship.setType("FrowardTo");
			neo4jXMLclient.postRelationship(neo4jRelationship);
				
			return true;
		}
	}
	
	// TODO to be changed
	
	/*
	 * update the list of link
	 */
	public void updateGraph(String nffgId, ExtendedLinkType newLink) throws ServiceException, LinkAlreadyPresentException {
		NffgGraphType queryResult = graphMap.get(nffgId);
		
		if(queryResult == null) {
			throw new InternalServerErrorException();
		} else {
			String linkName = linkBaseName;
			
			// TODO check if the link is already in the database
			// filter the list in orde to obtain all the links that have a specified source node and a specified destination node
			List<ExtendedLinkType> graphLinkList = queryResult.getLinks().getLink();
			Predicate<ExtendedLinkType> linkPredicate = p-> p.getSourceNode() == newLink.getDestinationNode() && p.getSourceNode() == newLink.getSourceNode();
			
			if(graphLinkList.stream().filter(linkPredicate).findFirst().get() == null && !newLink.isOverwrite()) 
				throw new LinkAlreadyPresentException();

			// update the list of links
			newLink.setLinkName(linkName.concat(Integer.toString(linkCounter.incrementAndGet())));
			queryResult.getLinks().getLink().add(newLink);
			
			// create a neo4j relationship for the forwarding
			Relationship neo4jRelationship = new Relationship();
			neo4jRelationship.setDstNode(newLink.getDestinationNode());
			neo4jRelationship.setSrcNode(newLink.getSourceNode());
			neo4jRelationship.setType("FrowardTo");	
			neo4jXMLclient.postRelationship(neo4jRelationship);
		}
	}
	
	public void deleteGraph() {}
}
