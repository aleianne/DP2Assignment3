package it.polito.dp2.NFV.sol3.service.ResourceRootClasses;

import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.VnfDao;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

    private static Logger logger = Logger.getLogger(CatalogResource.class.getName());
    private ObjectFactory objFactory = new ObjectFactory();
    public CatalogResource() {
    }

    /*
     * GET operation performed on the catalog resource, return back the list of all VNFs available into the NFV system
     */
    @GET
    @ApiOperation(value = "get the catalog", notes = "return to client the entire catalog of VNF that are available into web service")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Produces(MediaType.APPLICATION_XML)
    public Response getCatalog() {
        CatalogResourceService catalogServer = new CatalogResourceService();
        CatalogType catalogXmlElement = catalogServer.getCatalog();

        if (catalogXmlElement.getFunction().isEmpty()) {
            logger.log(Level.INFO, "the catalog of VNF in the NFV system is empty");
            return Response.noContent().build();
        }

        return Response.ok(objFactory.createFunctions(catalogXmlElement), MediaType.APPLICATION_XML).build();
    }

    /*
     * return a single vnf function
     */
    @GET
    @Path("/{vnfId}")
    @ApiOperation(value = "get a single VNF", notes = "get a single virtual node function")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not Found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    @Produces(MediaType.APPLICATION_XML)
    public Response getVNF(@PathParam("vnfId") String vnfId) {
        CatalogResourceService catalogServer = new CatalogResourceService();
        FunctionType function = catalogServer.getFunction(vnfId);

        if (function == null) {
            logger.log(Level.SEVERE, "the virtual function " + vnfId + " doesn't exist");
            throw new NotFoundException();
        }

        return Response.ok(objFactory.createFunction(function), MediaType.APPLICATION_XML).build();
    }
}
