package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.NFV.lab3.ServiceException;

public class Neo4jForwarder {

	private static Client client;
	
	private static ConcurrentMap<String, String> neo4jNodeMap = new ConcurrentHashMap<String, String>();
	private static ConcurrentMap<String, String> neo4jRelMap = new ConcurrentHashMap<String, String> ();
	
	private static String simpleXMLURL;
	
	private static Neo4jForwarder forwarder = new Neo4jForwarder();
	
	public Neo4jForwarder() {
		client = ClientBuilder.newClient();
		
		String neo4jURL = System.getProperty("it.polito.dp2.NFV.lab3.Neo4JSimpleXMLURL");
		simpleXMLURL = neo4jURL.concat("/data");
	}
	
	public static Neo4jForwarder getInstance() {
		return forwarder;
	}

	// return the ID of the node received by Neo4jSimpleXML
	public void sendNode(Node node, Labels nodeLabels) throws ServiceException {
		try {
			
			Response nodePostResponse = client.target(UriBuilder.fromUri(simpleXMLURL).path("/node").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(node));
			
			checkResponse(nodePostResponse);														// check the response of the server
				
			Node nodeResponse = nodePostResponse.readEntity(Node.class);							// convert the response into a Node class instance
			String nodeID = nodeResponse.getId();	
			
			Response labelPostResponse = client.target(UriBuilder.fromUri(simpleXMLURL).path("/node/" + nodeID + "/labels").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(nodeLabels));
			
			checkResponse(labelPostResponse);
			
			// put the response into the map
			neo4jNodeMap.put(node.getProperties().getProperty().get(0).getValue(), nodeID);
				
		} catch(ResponseProcessingException | IllegalStateException e) {
			throw new ServiceException("Neo4JSimpleXML client raised an exception: " + e.getMessage());
		}
	}
	
	// return the id of the relationship received byneo4jSimpleXML
	public void sendRelationship(Relationship relationship) throws ServiceException {
		try {
			
			String srcNodeID = neo4jNodeMap.get(relationship.getSrcNode());
			String destNodeID = neo4jNodeMap.get(relationship.getDstNode());
			
			if (srcNodeID == null || destNodeID == null) 
				throw new ServiceException("the source node and the destination node are empty!" + relationship.getSrcNode() + " " + relationship.getDstNode());
			
			relationship.setSrcNode(srcNodeID);
			relationship.setDstNode(destNodeID);
			
			Response relPostResponse = client.target(UriBuilder.fromUri(simpleXMLURL).path("node/" + srcNodeID + "/relationships").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(relationship));
			
			checkResponse(relPostResponse);
			
			Relationship relationshipRes = relPostResponse.readEntity(Relationship.class);
			
			// put the response into the hashmap
			//neo4jRelMap.put(relationship.getName(), relationshipRes.getId());

		} catch(ResponseProcessingException pe) {
			throw new ServiceException("Neo4JSimpleXML client raised an exception: " + pe.getMessage());
		} 
	}
	
	
	public Nodes getReachableHost(String nodeId) throws ServiceException {
		try {
			Response res = client.target(UriBuilder.fromUri(simpleXMLURL)
					.path("/node/" + nodeId)
					.queryParam("nodeLabel", "Host")
					.build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();

			checkResponse(res);
			
			Nodes reachableHosts = res.readEntity(Nodes.class);
			return reachableHosts;
			
		} catch(ResponseProcessingException rpe) {
			throw new ServiceException("Neo4jSimpleXML client raised an exception: " + rpe.getMessage());
		}
	}
	
	private void checkResponse(Response res) throws ServiceException {
		Response.StatusType resStatus = res.getStatusInfo();
		
		if (!(resStatus.getStatusCode() == 200 || resStatus.getStatusCode() == 204 || resStatus.getStatusCode() == 201)) {
			throw new ServiceException("Neo4jSimpleXML server returned an error: " + resStatus.getStatusCode() + " " + resStatus.getReasonPhrase());
		}
	}
}
