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

@Path("/connections")
@Api(value = "/connections")
public class ConnectionResource {

	private ConnectionDao connDao;
	
	private ObjectFactory objFactory = ObjectFactoryManager.getObjectFactory();
	private static Logger logger = Logger.getLogger(ConnectionResource.class.getName()); 
	
	public ConnectionResource() {}

	/*
	 * GET operation performed on the connections resoure retrieve the connection performance of the link between two host
	 */
	@GET
	@Path("/{host1Id}/{host2Id}")
	@ApiOperation(	value = "get connnection performance", notes = "return the connection performace parameters between two host into the system")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getConnections(@PathParam("host1Id") String host1, @PathParam("host2Id") String host2) {
		if(host1 == null || host2 == null)  {
			logger.log(Level.WARNING, "one of the parameters is null", new Object[] {host1, host2} );
			throw new BadRequestException();
		}
		ConnectionResourceService connectionService = new ConnectionResourceService();
		ConnectionType connectionXmlElement= connectionService.getConnection(host1, host2);
		if(connectionXmlElement == null) {
			throw new NotFoundException();
		} else {
			JAXBElement<ConnectionType> connectionElement = objFactory.createConnection(connectionService.getConnection(host1, host2));
			return Response.ok(connectionElement, MediaType.APPLICATION_XML).build();
		}	
	}
	
}
