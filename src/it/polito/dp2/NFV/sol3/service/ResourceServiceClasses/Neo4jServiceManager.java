package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import it.polito.dp2.NFV.lab3.ServiceException;

public class Neo4jServiceManager {

	private static Client client;
	
	private static ConcurrentMap<String, String> neo4jNodeMap = new ConcurrentHashMap<String, String>();
	private static ConcurrentMap<String, String> neo4jRelMap = new ConcurrentHashMap<String, String> ();
	
	private static String simpleXmlURL;
	
	private static Neo4jServiceManager serviceManager = new Neo4jServiceManager();
	
	private Response serverResponse;
	
	public Neo4jServiceManager() {
		client = ClientBuilder.newClient();
		simpleXmlURL = System.getProperty("it.polito.dp2.NFV.lab3.Neo4JSimpleXMLURL").concat("/data");
	}
	
	public static Neo4jServiceManager getInstance() {
		return serviceManager;
	}

	// return the ID of the node received by Neo4jSimpleXML
	public void postNode(Node node, Labels nodeLabels) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(simpleXmlURL).path("/node").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(node));
			
			checkResponse(serverResponse);														// check the response of the server
			Node nodeResponse = serverResponse.readEntity(Node.class);							// convert the response into a Node class instance
			String nodeID = nodeResponse.getId();	
			
			Response labelPostResponse = client.target(UriBuilder.fromUri(simpleXmlURL).path("/node/" + nodeID + "/labels").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(nodeLabels));
			
			checkResponse(labelPostResponse);
			neo4jNodeMap.put(node.getProperties().getProperty().get(0).getValue(), nodeID);
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("Neo4JSimpleXML client raised an exception: " + e.getMessage() + simpleXmlURL);
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	// return the id of the relationship received byneo4jSimpleXML
	public void postRelationship(Relationship relationship) throws ServiceException {
		try {
			String srcNodeID = neo4jNodeMap.get(relationship.getSrcNode());
			String destNodeID = neo4jNodeMap.get(relationship.getDstNode());
			
			if (srcNodeID == null || destNodeID == null) 
				throw new ServiceException("the source node and the destination node are empty!" + relationship.getSrcNode() + " " + relationship.getDstNode());
			
			relationship.setSrcNode(srcNodeID);
			relationship.setDstNode(destNodeID);
			
			serverResponse = client.target(UriBuilder.fromUri(simpleXmlURL).path("node/" + srcNodeID + "/relationships").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(relationship));
			
			checkResponse(serverResponse);
			serverResponse.close();
		} catch(ResponseProcessingException | IllegalStateException | IllegalArgumentException  e) {
			client.close();
			throw new ServiceException("Neo4JSimpleXML client raised an exception: " + e.getMessage());
		} 
	}
	
	public Nodes getReachableHost(String nodeId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(simpleXmlURL)
					.path("/node/" + nodeId)
					.queryParam("nodeLabel", "Host")
					.build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();

			checkResponse(serverResponse);
			Nodes reachableHosts = serverResponse.readEntity(Nodes.class);
			return reachableHosts;
		} catch(ResponseProcessingException rpe) {
			client.close();
			throw new ServiceException("Neo4jSimpleXML client raised an exception: " + rpe.getMessage());
		}
	}
	
	private void checkResponse(Response res) throws ServiceException {
		Response.StatusType resStatus = res.getStatusInfo();
		if (resStatus.getStatusCode() != 200 &&
				resStatus.getStatusCode() != 204 && 
				resStatus.getStatusCode() != 201) {
			
			client.close();
			throw new ServiceException("Neo4jSimpleXML server returned an error: " + resStatus.getStatusCode() + " " + resStatus.getReasonPhrase());
		}
	}
}
