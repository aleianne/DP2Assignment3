package it.polito.dp2.NFV.sol3.service.ResourceRootClasses;

import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.VnfDao;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path("/catalog")
@Api(value = "/catalog")
public class CatalogResource {

	public CatalogResource() {}

	private ObjectFactory objFactory = new ObjectFactory();

	/*
	 * GET operation performed on the catalog resource give back the list of all the VNFs available into the NFV system
	 */
	@GET
    @ApiOperation(	value = "get the catalog", notes = "return to the client the entire catalog of VNF that are available into the web service")
    @ApiResponses(	value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 500, message = "Internal Server Error")
    	})
	@Produces(MediaType.APPLICATION_XML)
	public Response getCatalog() {
		CatalogResourceService catalogServer = new CatalogResourceService();
		CatalogType catalogXmlElement = catalogServer.getCatalog();
		if(catalogXmlElement.getFunction().isEmpty()) {
			return Response.noContent().build();
		} else {
			JAXBElement<CatalogType> catalogElement = objFactory.createFunctions(catalogXmlElement);
			return Response.ok(catalogElement, MediaType.APPLICATION_XML).build();
		}
	}
}
