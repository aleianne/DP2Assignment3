package it.polito.dp2.NFV.sol3.service.ResourceRootClasses;

import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/hosts")
@Api(value = "/host")
public class HostResource {
	
	public HostResource() {}
	
	private HostResourceService hostServer;
	private ObjectFactory objFactory = ObjectFactoryManager.getObjectFactory();
	
	/*
	 *  this request method return a hosts data type
	 */
	@GET
	@ApiOperation(	value = "get the hosts collection", notes = "return all host that are available inside the web service")
    @ApiResponses(	value = {
    		@ApiResponse(code = 201, message = "OK"),
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getHosts() {
		HostsType hostsXmlElement = new HostsType();
		hostServer = new HostResourceService();
		List<ExtendedHostType> hostList = hostServer.getAllAvailableHost();
		
		if(hostList.isEmpty()) {
			return Response.noContent().build();
		} else {
			for(ExtendedHostType extendedHost: hostList) {
				hostsXmlElement.getHost().add((HostType) extendedHost);
			}
			JAXBElement<HostsType> hostResponse = objFactory.createHosts(hostsXmlElement);
			return Response.ok(hostResponse, MediaType.APPLICATION_XML).build();
		}
	}
	
	/*
	 * request a single host 
	 */
	@GET
	@Path("{hostId}")
	@ApiOperation(	value = "get a single host", notes = "return to the client the representation of a single host")
    @ApiResponses(	value = {
    		@ApiResponse(code = 201, message ="OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getHost(@PathParam("{hostId}") String hostname) {
		hostServer = new HostResourceService();
		ExtendedHostType host = hostServer.getSingleHost(hostname);
		
		if(host == null) {
			throw new NotFoundException();
		} else {
			JAXBElement<HostType> hostResponse = objFactory.createHost((HostType) host);
			return Response.ok(hostResponse, MediaType.APPLICATION_XML).build();									// return a host XML representation
		}
	}
	
	
	/*
	 * get all the nodes allocated into a specific host
	 */
	@GET
	@Path("{hostId}/nodes")
	@ApiOperation(	value = "get all the nodes", notes = "return to the client the entire collection of nodes allocated on the host")
    @ApiResponses(	value = {
    		@ApiResponse(code = 201, message = "OK"),
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getNodeAllocated(@PathParam("{hostId}") String hostname) {
		hostServer = new HostResourceService();
		ExtendedHostType host = hostServer.getSingleHost(hostname);
		
		if(host == null) {
			throw new NotFoundException();
		} else {
			NodesType xmlNodes = new NodesType();
			List<RestrictedNodeType> nodeList = xmlNodes.getNode();
			nodeList.addAll( hostServer.getAllocatedNodes(host));
			
			if(nodeList.isEmpty()) {
				return Response.noContent().build();
			}
			
			JAXBElement<NodesType> hostResponse = objFactory.createNodes(xmlNodes);
			return Response.ok(hostResponse, MediaType.APPLICATION_XML).build();									// return a host XML representation
		}
	}
}
