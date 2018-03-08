package it.polito.dp2.NFV.sol3.client1;

import java.util.Calendar;

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
	
	public RestrictedNodeType postNode(RestrictedNodeType node, String nffgId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/nodes").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(node));
			
			checkResponse(serverResponse);														// check the response of the server
			RestrictedNodeType responseNode = serverResponse.readEntity(RestrictedNodeType.class);
			
			return responseNode;
			
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
	
	public NffgsInfoType getGraphs(Calendar date) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			
			NffgsInfoType responseGraph = serverResponse.readEntity(NffgsInfoType.class);
			return responseGraph;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	public NodesType getHostNode(String hostname) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + hostname + "/nodes").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			
			NodesType responseHost = serverResponse.readEntity(NodesType.class);
			return responseHost;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	public CatalogType getCatalog() throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/catalog").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			
			CatalogType responseCatalog = serverResponse.readEntity(CatalogType.class);
			return responseCatalog;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	public FunctionType getFunction(String vnfName) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + vnfName + "/nodes").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);													// check the response of the server
			
			FunctionType responseVnf = serverResponse.readEntity(FunctionType.class);
			return responseVnf;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}

	public ConnectionType getConnection(String host1, String host2) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/connections/" + host1 + "/" + host2).build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			
			ConnectionType responseConnection = serverResponse.readEntity(ConnectionType.class);
			return responseConnection;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	public ExtendedHostType getHost(String hostId) throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + hostId).build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			ExtendedHostType responseHost = serverResponse.readEntity(ExtendedHostType.class);
			
			return responseHost;
			
		} catch(ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
			client.close();
			throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
		} catch(NullPointerException npe) {
			client.close();
			throw new ServiceException("exception: " + npe.getMessage());
		}
	}
	
	public HostsType getHosts() throws ServiceException {
		try {
			serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts").build())
					.request()
					.accept(MediaType.APPLICATION_XML)
					.get();
			
			checkResponse(serverResponse);														// check the response of the server
			
			HostsType responseHosts = serverResponse.readEntity(HostsType.class);
			return responseHosts;
			
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
