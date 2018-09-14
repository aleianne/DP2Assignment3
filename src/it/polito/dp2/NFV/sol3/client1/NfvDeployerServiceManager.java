package it.polito.dp2.NFV.sol3.client1;

import java.net.URI;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.ws.Service;

import io.swagger.models.Xml;
import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.NoNodeException;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NfvDeployerServiceManager {

    private static Logger logger = Logger.getLogger(NfvDeployerServiceManager.class.getName());

    private static final String url = "http://localhost:8080/NfvDeployer/rest/";
    private Client client;
    private String serviceURL;
    private Response serverResponse;

    public NfvDeployerServiceManager() {
        client = ClientBuilder.newClient();
        serviceURL = System.getProperty("it.polito.dp2.NFV.lab3.URL");

        if (serviceURL == null)
            serviceURL = url;

    }

    public NffgGraphType postNffg(NffgGraphType nffg) throws ServiceException, AllocationException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(nffg));

            //checkResponse(serverResponse);

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 201:
                    return serverResponse.readEntity(NffgGraphType.class);
                case 400:
                    throw new ServiceException("the graph forwarded is not recogniezed by the server");
                case 404:
                    throw new AllocationException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public RestrictedNodeType postNode(RestrictedNodeType node, String nffgId) throws ServiceException, AllocationException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/" + nffgId + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(node));

            // TODO da sistemare
            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 201:
                    return serverResponse.readEntity(RestrictedNodeType.class);
                case 404:
                    throw new AllocationException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public ExtendedLinkType postLink(ExtendedLinkType link, String nffgId) throws ServiceException, NoNodeException,
            LinkAlreadyPresentException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/" + nffgId + "/links").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(link));

            //checkResponse(serverResponse);

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 201:
                    return serverResponse.readEntity(ExtendedLinkType.class);
                case 404:
                    throw new ServiceException("the nffg doesn't exists");
                case 409:
                    throw new LinkAlreadyPresentException();
                case 403:
                    throw new NoNodeException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public NffgGraphType getGraph(String nffgId) throws ServiceException, NotFoundException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/" + nffgId).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch(serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(NffgGraphType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public NffgsInfoType getGraphs(String date) throws ServiceException {
        try {

            URI uri = null;

            if (date == null)
                uri = UriBuilder.fromUri(serviceURL).path("nffgs").build();
            else
                uri = UriBuilder.fromUri(serviceURL).path("nffgs").queryParam("date", date).build();

            serverResponse = client.target(uri)
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            //checkResponse(serverResponse);

            switch(serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(NffgsInfoType.class);
                case 204:
                    throw new ServiceException("server returned a response without any content");
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public NodesType getHostNode(String hostname) throws ServiceException, NotFoundException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("hosts/" + hostname + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(NodesType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

            //checkResponse(serverResponse);
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public CatalogType getCatalog() throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("catalog").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(CatalogType.class);
                case 204:
                    throw new ServiceException("server returned a response without any content");
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

            //throw new ServiceException("impossible to retrieve the catalog");
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public NodesType getGraphNodes(String nffgId) throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/" + nffgId + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(NodesType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

            //checkResponse(serverResponse);
            //return serverResponse.readEntity(NodesType.class);
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("nfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public LinksType getGraphLinks(String nffgId) throws ServiceException, NotFoundException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/" + nffgId + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(LinksType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

        } catch (ResponseProcessingException | IllegalStateException | IllegalArgumentException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public RestrictedNodeType getNodeIntoGraph(String nffgId, String nodeName) throws ServiceException, NotFoundException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("nffgs/" + nffgId + "/nodes/" + nodeName).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(RestrictedNodeType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public FunctionType getFunction(String vnfName) throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("hosts/" + vnfName + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(FunctionType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public ConnectionType getConnection(String host1, String host2) throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("connections/" + host1 + "/" + host2).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            checkResponse(serverResponse);
            return serverResponse.readEntity(ConnectionType.class);
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public ConnectionsType getConnections() throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("connections").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(ConnectionsType.class);
                case 204:
                    // todo non so se va bene o meno
                    throw new ServiceException();
                case 500:
                    throw new ServiceException();
                default:
                    throw new ServiceException();
            }

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public ExtendedHostType getHost(String hostId) throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("hosts/" + hostId).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(ExtendedHostType.class);
                case 404:
                    throw new NotFoundException();
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    public HostsType getHosts() throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("hosts").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            switch (serverResponse.getStatusInfo().getStatusCode()) {
                case 200:
                    return serverResponse.readEntity(HostsType.class);
                case 500:
                    throw new ServiceException("server cannot fulfill the request");
                default:
                    throw new ServiceException("unknown status code");
            }

            //checkResponse(serverResponse);
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }


    private void checkResponse(Response res) throws ServiceException {
        Response.StatusType resStatus = res.getStatusInfo();
        if (resStatus.getStatusCode() >= 400 && resStatus.getStatusCode() <= 599)
            throw new ServiceException("Neo4jSimpleXML server returned an error: " + resStatus.getStatusCode() + " " + resStatus.getReasonPhrase());
    }

}
