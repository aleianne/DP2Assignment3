package it.polito.dp2.NFV.sol3.client2;

import it.polito.dp2.NFV.lab3.*;
import it.polito.dp2.NFV.sol3.ServiceXML.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBElement;
import java.net.URI;

public class NfvDeployerServiceManager {

    private static final String url = "http://localhost:8080/NfvDeployer/rest";
    private Client client;
    private String serviceURL;
    private Response serverResponse;
    private ObjectFactory objectFactory;

    protected NfvDeployerServiceManager() {
        client = ClientBuilder.newClient();
        serviceURL = System.getProperty("it.polito.dp2.NFV.lab3.URL");

        if (serviceURL == null)
            serviceURL = url;

        objectFactory = new ObjectFactory();
    }

    protected NffgGraphType postNffg(NffgGraphType nffg) throws ServiceException, AllocationException {
        try {

            JAXBElement<NffgGraphType> xmlElement = objectFactory.createNffg(nffg);

            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(xmlElement));

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(NffgGraphType.class);
//                case 400:
//                    throw new ServiceException("the graph forwarded is not recognized by the server");
//                case 404:
//                    throw new AllocationException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (NoNodeException ue) {
                throw new AllocationException();
            } catch (LinkAlreadyPresentException | UnknownEntityException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(NffgGraphType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected RestrictedNodeType postNode(RestrictedNodeType node, String nffgId) throws ServiceException, AllocationException {
        try {

            JAXBElement<RestrictedNodeType> xmlNode = objectFactory.createNode(node);

            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(xmlNode));

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(RestrictedNodeType.class);
//                case 400:
//                    throw new ServiceException("the node forwarded isn't recognized by the server");
//                case 404:
//                    throw new AllocationException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (UnknownEntityException ue) {
                throw new AllocationException();
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(RestrictedNodeType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected ExtendedLinkType postLink(ExtendedLinkType link, String nffgId) throws ServiceException, NoNodeException,
            LinkAlreadyPresentException, UnknownEntityException {

        try {
            JAXBElement<ExtendedLinkType> xmlLink = objectFactory.createLink(link);

            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/links").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(xmlLink));
//
//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(ExtendedLinkType.class);
//                case 400:
//                    throw new ServiceException("link not recognized by the server");
//                case 404:
//                    throw new ServiceException("the nffg doesn't exists");
//                case 409:
//                    throw new LinkAlreadyPresentException();
//                case 403:
//                    throw new NoNodeException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            checkResponse(serverResponse.getStatusInfo().getStatusCode());
            return serverResponse.readEntity(ExtendedLinkType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected NffgGraphType getGraph(String nffgId) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch(serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(NffgGraphType.class);
//                case 404:
//                    throw new UnknownEntityException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(NffgGraphType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected NffgsInfoType getGraphs(String date) throws ServiceException {
        try {

            URI uri = null;

            if (date == null)
                uri = UriBuilder.fromUri(serviceURL).path("/nffgs").build();
            else
                uri = UriBuilder.fromUri(serviceURL).path("/nffgs").queryParam("date", date).build();

            serverResponse = client.target(uri)
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch(serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(NffgsInfoType.class);
//                case 204:
//                    throw new ServiceException("server returned a response without any content");
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }


            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | UnknownEntityException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(NffgsInfoType.class);


        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected NodesType getHostNode(String hostname) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + hostname + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(NodesType.class);
//                case 404:
//                    throw new UnknownEntityException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(NodesType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected CatalogType getCatalog() throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/catalog").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(CatalogType.class);
//                case 204:
//                    throw new ServiceException("server returned a response without any content");
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | UnknownEntityException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(CatalogType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected NodesType getGraphNodes(String nffgId) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(NodesType.class);
//                case 404:
//                    throw new UnknownEntityException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(NodesType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("nfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected LinksType getGraphLinks(String nffgId) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(LinksType.class);
//                case 404:
//                    throw new NotFoundException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(LinksType.class);

        } catch (ResponseProcessingException | IllegalStateException | IllegalArgumentException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected RestrictedNodeType getNodeIntoGraph(String nffgId, String nodeName) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/nffgs/" + nffgId + "/nodes/" + nodeName).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(RestrictedNodeType.class);
//                case 404:
//                    throw new NotFoundException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(RestrictedNodeType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected FunctionType getFunction(String vnfName) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + vnfName + "/nodes").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(FunctionType.class);
//                case 404:
//                    throw new NotFoundException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(FunctionType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected ConnectionType getConnection(String host1, String host2) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/connections/" + host1 + "/" + host2).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(ConnectionType.class);
//                case 404:
//                    throw new ServiceException();
//                case 500:
//                    throw new ServiceException();
//                default:
//                    throw new ServiceException();
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(ConnectionType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected ConnectionsType getConnections() throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/connections").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(ConnectionsType.class);
//
//                case 500:
//                    throw new ServiceException();
//                default:
//                    throw new ServiceException();
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | UnknownEntityException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(ConnectionsType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected ExtendedHostType getHost(String hostId) throws ServiceException, UnknownEntityException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts/" + hostId).build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(ExtendedHostType.class);
//                case 404:
//                    throw new NotFoundException();
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }

            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(ExtendedHostType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }

    protected HostsType getHosts() throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(serviceURL).path("/hosts").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

//            switch (serverResponse.getStatusInfo().getStatusCode()) {
//                case 200:
//                    return serverResponse.readEntity(HostsType.class);
//                case 500:
//                    throw new ServiceException("server cannot fulfill the request");
//                default:
//                    throw new ServiceException("unknown status code");
//            }


            try {
                checkResponse(serverResponse.getStatusInfo().getStatusCode());
            } catch (LinkAlreadyPresentException | UnknownEntityException | NoNodeException e) {
                throw new ServiceException();
            }

            return serverResponse.readEntity(HostsType.class);

        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("NfvDeployer client raised an exception: " + e.getMessage());
        }
    }


    private void checkResponse(int statusCode) throws ServiceException, LinkAlreadyPresentException,
            NoNodeException, UnknownEntityException {
        switch (serverResponse.getStatusInfo().getStatusCode()) {
            case 200:
                return;
            case 201:
                return;
            case 204:
                // todo non so se va bene o meno
                throw new ServiceException();
            case 400:
                throw new ServiceException("wrong format");
            case 404:
                throw new UnknownEntityException();
            case 409:
                throw new LinkAlreadyPresentException();
            case 403:
                throw new NoNodeException();
            case 500:
                throw new ServiceException("server cannot fulfill the request");
            default:
                throw new ServiceException("unknown status code");
        }
    }

//    private void checkResponse(Response res) throws ServiceException {
//        Response.StatusType resStatus = res.getStatusInfo();
//        if (resStatus.getStatusCode() >= 400 && resStatus.getStatusCode() <= 599)
//            throw new ServiceException("Neo4jSimpleXML server returned an error: " + resStatus.getStatusCode() + " " + resStatus.getReasonPhrase());
//    }

}
