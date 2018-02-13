package it.polito.dp2.NFV.sol3.service.ResourceRootClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.ConnectionDao;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
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

@Path("/connection")
@Api(value = "/connection")
public class ConnectionResource {

	private ConnectionDao connDao;
	
	private ObjectFactory objFactory = ObjectFactoryManager.getObjectFactory();
	private static Logger logger = Logger.getLogger(ConnectionResource.class.getName()); 
	
	public ConnectionResource() {}

	/*
	 * return the connection between the host1 and host2
	 */
	@GET
	@ApiOperation(	value = "get the connnection parameter", notes = "return the connection parameters between two host into the system")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getConnections(@QueryParam("host1") String host1, @QueryParam("host2") String host2) {
		if(host1 == null || host2 == null)  {
			logger.log(Level.WARNING, "one of the parameters is null", new Object[] {host1, host2} );
			throw new BadRequestException();
		}
		ConnectionResurceService connectionService = new ConnectionResourceService();
		ConnectionType connectionXmlElement= connectionService.getConnection(host1, host2);
		if(connectionXmlElement == null) {
			return Response.noContent().build();
		} else {
			JAXBElement<ConnectionType> connectionElement = objFactory.createConnection(connectionService.getConnection(host1, host2));
			return Response.ok(connectionElement, MediaType.APPLICATION_XML).build();
		}	
	}
	
}
