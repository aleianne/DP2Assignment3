package it.polito.dp2.NFV.sol3.service.ResourceRootClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.Nodes;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;

import java.rmi.ServerException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;

/*
 * root class for the nffg resource
 */
@Path("/nffgs")
@Api(value = "/nffgs")
public class NffgsResource {

	public NffgsResource() {}
	
	private NffgResourceService nffgServer = new NffgResourceService();
	
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
	public String postNewNffg(JAXBElement<GraphType> reqBodyGraph) {
		try {
			
			if(reqBodyGraph != null) {													
				GraphType graphTobeDeployed = reqBodyGraph.getValue();								// create a new nodesType element
			
				if(graphTobeDeployed != null) {
					return nffgServer.deployNewNffgGraph(graphTobeDeployed);						// return the the nffgID
				} else {
					logger.log(Level.SEVERE, "empty xml represeantion of graph to be deployed");
					throw new InternalServerErrorException();
				}
				
			} else {
				logger.log(Level.SEVERE, "server received an empty request body");
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
	 * return the entire collection of nffg that are saved inside the server
	 */
	@GET 
	@Path("reachableHost/{nodeId}")
    @ApiOperation(	value = "get reachable nodes", notes = "get the nodes that are reachable from the specified node")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public JAXBElement<HostsType> getReachableNodes(@PathParam("nodeId") String nodeId) {
		try {
			
			if(nodeId == null) 
				throw new InternalServerErrorException();
			
			HostsType reachableHosts = new HostsType();
			reachableHosts.getHost().addAll(nffgServer.getReachableHost(nodeId));
			
			return objFactory.createHosts(reachableHosts);
			
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception: " + se.getMessage());
			throw new InternalServerErrorException();
		}
	}
	
	/*
	 * return the entire collection of nffgs that are saved inside the server
	 */
	@GET 
    @ApiOperation(	value = "get nffgs collections", notes = "return the xml representation of the nffg graphs deployed into the system")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public JAXBElement<NffgsType> getNffgs(@QueryParam("date") String date) {
		
	
		if(date == null) {
			logger.log(Level.INFO, "return all the nffg stored in the system");
			
			// create a new nffg element and put inside the list of retrivied nffgs
			NffgsType nffgsElement = objFactory.createNffgsType();
			// get the list of all nffg in the system
			List<NffgType> nffgList = nffgServer.getNffgs();
			
			if(nffgList.isEmpty()) 
				logger.log(Level.WARNING, "server returned an empty response becasue no one nffg has been found");
			
			// add the list returned into the XML representation of the response
			nffgsElement.getNffg().addAll(nffgList);
			
			return objFactory.createNffgs(nffgsElement);
		} else {
			
			
			
			
		}
		
		
		
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
    		@ApiResponse(code = 204, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public JAXBElement<NffgType> getNffg(@PathParam("nffgId") String nffgId) {
		
		if(nffgId == null) {
			logger.log(Level.SEVERE, "the nffgId parameter received by the server id null");
			throw new NotFoundException();
		}
		
		NffgType nffgFound = nffgServer.getNffg(nffgId);												// query the database to obtain the nffg that correspond to the ID
		if(nffgFound != null) {
			return objFactory.createNffg(nffgFound);
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
	@GET 
	@Path("{nffgId}/graph")
	@ApiOperation(	value = "get a graph", notes = "return the xml representation of the graph associated with the nf-fg")
	@ApiResponses(	value = {
	    @ApiResponse(code = 200, message = "OK"),
	    @ApiResponse(code = 204, message = "No Body"),
	    @ApiResponse(code = 404, message = "Not Found"),
	    @ApiResponse(code = 500, message = "Internal Server Error")
	   })
	@Produces(MediaType.APPLICATION_XML)
	public JAXBElement<GraphType> getGraph(@PathParam("nffgId") String nffgId) {
		
		GraphType graph = nffgServer.getGraph(nffgId);
		if(graph != null) {
			return objFactory.createGraph(graph);
		} else {
			logger.log(Level.SEVERE, "the resource requested by the client doesn't exist");
			throw new NotFoundException();
		}
	}
	
	
	/*
	 * post a new node inside the specified nffg
	 */
	@POST
	@Path("{nffgId}/graph/nodes")
	@ApiOperation(	value = "create a node", notes = "delete a nf-fg node")
	@ApiResponses(	value = {
	    @ApiResponse(code = 200, message = "Created"),
	    @ApiResponse(code = 204, message = "No Body"),
	    @ApiResponse(code = 404, message = "Forbidden"),
	    @ApiResponse(code = 500, message = "Internal Server Error")
	    })
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public String createNewNode(JAXBElement<NodeType> reqBodyNode, @PathParam("nffgId") String nffgId) {
		try {
			
			if(nffgId != null && reqBodyNode != null){
				NodeType node = reqBodyNode.getValue();
				if(node != null) {
					
					return nffgServer.addNode(nffgId, node);
					
				} else {
					logger.log(Level.SEVERE, "the xml element is null");
					throw new InternalServerErrorException();
				}
			} else {						
				// if the parameter is null throw a new excption
				logger.log(Level.SEVERE, "the parameter received by the server are null", new Object[] {reqBodyNode, nffgId});
				throw new InternalServerErrorException();
			}
			
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception" + se.getMessage());
			throw new InternalServerErrorException();
		} catch(AllocationException ae) {
			throw new ForbiddenException();
		}
	}
		
	/*
	* this operation is mapped to a java method but is not necessary for the assignment
	 */
	@DELETE
	@Path("{nffgId}/graph/nodes/{nodeId}")
	@ApiOperation( value = "delete a node", notes = "delete a nf-fg node")
	@ApiResponses( value = {
		@ApiResponse(code = 200, message = "Deleted"),
		@ApiResponse(code = 404, message = "Not Found"),
		@ApiResponse(code = 500, message = "Internal Server Error")
	})
	public void deleteNode(@PathParam("nodeId") String nodeId, @PathParam("nffgId") String nffgId) {
		throw new ServiceUnavailableException();
	}
	
	/*
	 * create a new link inside the nf-fg graph
	 */
	@POST
	@Path("{nffgId}/graph/links")
	@ApiOperation(	value = "create a new link", notes = "create a new link inside the graph")
	@ApiResponses(	value = {
	    @ApiResponse(code = 200, message = "Created"),
	    @ApiResponse(code = 204, message = "No Body"),
	    @ApiResponse(code = 404, message = "Forbidden"),
	    @ApiResponse(code = 500, message = "Internal Server Error")
	  })
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_XML)
	public String createNewLink(JAXBElement<LinkType> reqBodyNode, @PathParam("nffgId") String nffgId) {
		try {
			
			if(nffgId != null && reqBodyNode != null) {
				LinkType link = reqBodyNode.getValue();
				if(link != null) {
				
					return nffgServer.addLink(nffgId, link);
		
				} else {
					logger.log(Level.SEVERE, "there isn't any type of data inside the request element");
					throw new InternalServerErrorException();
				}
			} else {
				logger.log(Level.SEVERE, "the parameter received by the server are null", new Object[] {reqBodyNode, nffgId});
				throw new InternalServerErrorException();
			}
			
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception " + se.getMessage());
			throw new InternalServerErrorException();
		} catch(AllocationException ae) {
			throw new ForbiddenException();
		}
	}
		
	/*
	 * this operation is mapped to a java method but is not necessary for the assignment
	 */
	@DELETE
	@Path("{nffgId}/graph/links/{linkId}")
	@ApiOperation( value = "delete a link", notes = "delete a nf-fg link")
	@ApiResponses( value = {
		@ApiResponse(code = 200, message = "Deleted"),
		@ApiResponse(code = 404, message = "Not Found"),
		@ApiResponse(code = 500, message = "Internal Server Error")
		})
	public void deleteLink(@PathParam("linkId") String linkId, @PathParam("nffgId") String nffgId) {
		throw new ServiceUnavailableException();
	}

}
