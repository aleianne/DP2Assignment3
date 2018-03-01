package it.polito.dp2.NFV.sol3.client1;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import io.swagger.models.Xml;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NfvDeployerServiceManager {
	
	Client client;
	String serviceURL;
	Response serverResponse;
	
	public NfvDeployerServiceManager() {
		client = ClientBuilder.newClient();
		serviceURL = System.getProperty("it.polito.dp2.NFV.lab3.URL");
	}

	public NffgGraphType postNffg(NffgGraphType nffg) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(nffg));
			
			checkResponse(serverResponse);														// check the response of the server
			return serverResponse.readEntity(NffgGraphType.class);
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		} 
	}
	
	public String postNode(NodeType node, String nffgId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/nodes").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(node));
			
			checkResponse(serverResponse);														// check the response of the server
			NodeType responseNode = serverResponse.readEntity(NodeType.class);
			
			return responseNode.getName();
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		} 
	}
	
	public ExtendedLinkType postLink(ExtendedLinkType link, String nffgId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/links").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(link));
			
			checkResponse(serverResponse);														// check the response of the server
			ExtendedLinkType responseLink = serverResponse.readEntity(ExtendedLinkType.class);
			
			return responseLink;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		} 
	}
	
	public HostType getHost(String hostId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + hostId).build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			HostType responseHost = serverResponse.readEntity(HostType.class);
			
			return responseHost;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	public NffgGraphType getGraph(String nffgId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId).build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			NffgGraphType responseHost = serverResponse.readEntity(NffgGraphType.class);
			
			return responseHost;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
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
