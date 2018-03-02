package it.polito.dp2.NFV.sol3.service.ResourceRootClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.Exceptions.GraphNotFoundException;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.NoNodeException;
import it.polito.dp2.NFV.lab3.ServiceException;

/*
 * root class for the nffg resource
 */
@Path("/nffgs")
@Api(value = "/nffgs")
public class NffgsResource {

	private NffgResourceService nffgServer;
	
	private static ObjectFactory objFactory = ObjectFactoryManager.getObjectFactory();
	private static Logger logger = Logger.getLogger(NffgsResource.class.getName());
	
	/*
	 * this method receive, inside the request body, the XML representaion of the nffg
	 * to be deployed. The server should check if is it possible to allocate this new graph 
	 * and return the response to the client.
	 */
	@POST
    @ApiOperation(	value = "create a new nffg", notes = "deploy a new nffg into the infrastructure network")
    @ApiResponses(	value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Forbidden"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public Response postNewNffg(JAXBElement<NffgGraphType> reqBodyNffg) {
		try {
			
			if(reqBodyNffg != null)
				logger.log(Level.INFO, "the request body is empty");
			
			// get the nffg graph type xml instance from the req body
			NffgGraphType nffgXmlElement = reqBodyNffg.getValue();								
			if(nffgXmlElement != null) {
				nffgServer = new NffgResourceService();
				
				// put the graph into the database and then return it to the client
				nffgServer.deployNewNffgGraph(nffgXmlElement);							
				JAXBElement<NffgGraphType> xmlResponse = objFactory.createNffg(nffgXmlElement);
				return Response.ok(xmlResponse, MediaType.APPLICATION_XML).build();
			} else {
				logger.log(Level.SEVERE, "empty xml represeantion of graph to be deployed");
				throw new BadRequestException();
			}
			
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception: " + se.getMessage());
			throw new InternalServerErrorException();
		} catch(AllocationException ae) {
			logger.log(Level.SEVERE, "impossible to deploy the graph");
			throw new ForbiddenException();
		}
	}
	
	
	/*
	 * return the entire collection of nffgs that are saved inside the server
	 * or only those that are deployed before the date passed as parameter, if present
	 */
	@GET 
    @ApiOperation(	value = "get nffgs collections", notes = "return the xml representation of the nffg graphs deployed into the system")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getNffgs(@QueryParam("date") Date inputDate) {
		JAXBElement<NffgsInfoType> nffgsXmlElement;
		nffgServer = new NffgResourceService();

		if(inputDate == null) {
			// return all the nffg infos in the server
			nffgsXmlElement = objFactory.createNffgs(nffgServer.getAllNffgs());
		} else {
			// select only the nffg that are deployed before the date
			NffgsInfoType resultValue = nffgServer.selectNffgs(inputDate);
			
			// if the nffgs resource is empty return 204 No Content error
			if(resultValue == null) 
				return Response.noContent().build();
			else {
				nffgsXmlElement = objFactory.createNffgs(resultValue);
			}
		}
		// return the response 
		return Response.ok(nffgsXmlElement, MediaType.APPLICATION_XML).build();
	}
	
	/*
	 * this method is mapped with /nffgs/{id} resource and return a xml data type 
	 * that represent the graph deployed into the system
	 */
	@GET
	@Path("{nffgId}")
	@ApiOperation(	value = "get a single nffg", notes = "return the xml representation of the nffg graphs deployed into the system")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getNffg(@PathParam("nffgId") String nffgId) {
		if(nffgId == null) {
			logger.log(Level.SEVERE, "the nffgId parameter received by the server id null");
			throw new NotFoundException();
		}
		// interrogate the database to find the nffg graph
		nffgServer = new NffgResourceService();
		NffgGraphType queryResultNffg = nffgServer.getSingleNffg(nffgId);												// query the database to obtain the nffg that correspond to the ID
	
		if(queryResultNffg != null) { 
			
			// synchronize the access to the graph in order to prevent change during the GET operation
			synchronized(queryResultNffg)  {
				JAXBElement<NffgGraphType> nffgXmlElement = objFactory.createNffg(queryResultNffg);
				return Response.ok(nffgXmlElement, MediaType.APPLICATION_XML).build();
			}
			
		} else {
			logger.log(Level.SEVERE, "the resource requested by the client doesn't exist");
			throw new NotFoundException();
		}
	}
	
	/*
	 * this operation is not necessary for the assignment
	 */
	@DELETE
	@Path("{nffgId}")
    @ApiOperation(	value = "delete a nffg", notes = "delete an nffg and his graph")
    @ApiResponses(	value = {
    		@ApiResponse(code = 501, message = "Not Implemented"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	public void deleteNffgs() {
		throw new ServiceUnavailableException();
	}
	
	/*
	 * return the XML representation of a deployed graph 
	 */
	/*@GET 
	@Path("{nffgId}")
	@ApiOperation(	value = "get a nffg", notes = "return the xml representation of the graph associated with the nf-fg")
	@ApiResponses(	value = {
	    @ApiResponse(code = 200, message = "OK"),
	    @ApiResponse(code = 204, message = "No Body"),
	    @ApiResponse(code = 404, message = "Not Found"),
	    @ApiResponse(code = 500, message = "Internal Server Error")
	   })
	@Produces(MediaType.APPLICATION_XML)
	public Response getGraph(@PathParam("nffgId") String nffgId) {
		if(nffgId != null) {
			GraphType graph = nffgServer.getGraph(nffgId);
			if(graph != null) {
				JAXBElement<GraphType> graphXmlElement = objFactory.creteGraph(graph);
				return Response.ok(graphXmlElement, MediaType.APPLICATION_XML).build();
			} else 
				return Response.noContent().build();
		} else {
			logger.log(Level.SEVERE, "the resource requested by the client doesn't exist");
			throw new NotFoundException();
		}
	}*/
	
	
	/*
	 * post a new node inside the specified nffg
	 */
	@POST
	@Path("{nffgId}/nodes")
	@ApiOperation(	value = "create a node", notes = "delete a nf-fg node")
	@ApiResponses(	value = {
	    @ApiResponse(code = 201, message = "Created"),
	    @ApiResponse(code = 404, message = "Forbidden"),
	    @ApiResponse(code = 500, message = "Internal Server Error")
	    })
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public Response createNewNode(JAXBElement<NodeType> reqBodyNode, @PathParam("nffgId") String nffgId) {
		try {
			if(nffgId == null) 
				throw new NotFoundException();
			if(reqBodyNode == null) 
				logger.log(Level.INFO, "the  request body is empty");
			
			NodeType nodeXmlElement = reqBodyNode.getValue();
			if(nodeXmlElement != null) {
				NodeResourceService nodeServer = new NodeResourceService();
				nodeServer.addNode(nffgId, nodeXmlElement);
				JAXBElement<NodeType> resBodyNode = objFactory.createNode(nodeXmlElement);
				return Response.ok(resBodyNode, MediaType.APPLICATION_XML).build();
			} else {
				logger.log(Level.SEVERE, "the xml element is null");
				throw new BadRequestException();
			}
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception" + se.getMessage());
			throw new InternalServerErrorException();
		} catch(AllocationException ae) {
			logger.log(Level.SEVERE, "impossible to allocate all the nodes");
			throw new ForbiddenException();
		} catch(GraphNotFoundException gne) {
			logger.log(Level.SEVERE, "graph not found", nffgId);
			throw new ForbiddenException();
		}
	}
		
	/*
	* this operation is mapped to a java method but is not necessary for the assignment
	 */
	@DELETE
	@Path("{nffgId}/nodes/{nodeId}")
	@ApiOperation( value = "delete a node", notes = "delete a nf-fg node")
	@ApiResponses( value = {
		@ApiResponse(code = 200, message = "Not Implemented"),
		@ApiResponse(code = 404, message = "Not Found"),
		@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public void deleteNode(@PathParam("nodeId") String nodeId, @PathParam("nffgId") String nffgId) {
		throw new ServiceUnavailableException();
	}
	
	
	/*
	 * return all the reachable host starting from the node id
	 */
	@GET 
	@Path("{nffgId}/nodes/{nodeId}/reachableHost")
    @ApiOperation(	value = "get all the reachable hosts ", notes = "get the hosts that are reachable from the specified node")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 204, message = "No Body"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getReachableNodes(@PathParam("nffgId") String nffgId, @PathParam("nodeId") String nodeId) {
		try {
			if(nodeId == null || nffgId == null) {
				logger.log(Level.SEVERE, "the resource id passed are null");
				throw new InternalServerErrorException();
			}
		
			NodeResourceService nodeServer = new NodeResourceService();
			
			HostsType reachableHosts = new HostsType();
			reachableHosts.getHost().add((HostType) nodeServer.getReachableHost(nodeId));
			if(reachableHosts.getHost().isEmpty()) {
				return Response.noContent().build();
			} else {
				JAXBElement<HostsType> hostsXmlElement = objFactory.createHosts(reachableHosts);
				return Response.ok(hostsXmlElement, MediaType.APPLICATION_XML).build();
			}
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception: " + se.getMessage());
			throw new InternalServerErrorException();
		}
	}
	
	/*
	 * create a new link inside the nf-fg graph
	 */
	@POST
	@Path("{nffgId}/links")
	@ApiOperation(	value = "create a new link", notes = "create a new link inside the graph")
	@ApiResponses(	value = {
	    @ApiResponse(code = 201, message = "Created"),
	    @ApiResponse(code = 404, message = "Not Found"),
	    @ApiResponse(code = 403, message = "Forbidden"),
	    @ApiResponse(code = 500, message = "Internal Server Error")
	  })
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public Response createNewLink(JAXBElement<ExtendedLinkType> reqBodyNode, @PathParam("nffgId") String nffgId) {
		try {
			LinkResourceService linkServer = new LinkResourceService();
			
			if(nffgId == null) 
				throw new ForbiddenException();
			
			if(reqBodyNode == null) 
				logger.log(Level.INFO, "the request Body si null");
			
			ExtendedLinkType link = reqBodyNode.getValue();
			if(link != null) {
				linkServer.addLink(nffgId, link);
				JAXBElement<ExtendedLinkType> xmlLinkElement = objFactory.createLink(link);
				return Response.ok(xmlLinkElement, MediaType.APPLICATION_XML).build();
			} else {
				logger.log(Level.SEVERE, "there isn't any type of data inside the request element");
				throw new BadRequestException();
			}
			
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception " + se.getMessage());
			throw new InternalServerErrorException();
		} catch(NoNodeException | LinkAlreadyPresentException ae) {
			throw new ForbiddenException();
		} catch(GraphNotFoundException gfe) {
			logger.log(Level.SEVERE, "the graph specified doesn't exist");
			throw new NotFoundException();
		}
	}
		
	/*
	 * this operation is mapped to a java method but is not necessary for the assignment
	 */
	@DELETE
	@Path("{nffgId}/links/{linkId}")
	@ApiOperation( value = "delete a link", notes = "delete a nf-fg link")
	@ApiResponses( value = {
		@ApiResponse(code = 200, message = "Not Implemented"),
		@ApiResponse(code = 404, message = "Not Found"),
		@ApiResponse(code = 500, message = "Internal Server Error")
		})
	public void deleteLink(@PathParam("linkId") String linkId, @PathParam("nffgId") String nffgId, @PathParam("nodeId") String nodeId) {
		throw new ServiceUnavailableException();
	}

}
