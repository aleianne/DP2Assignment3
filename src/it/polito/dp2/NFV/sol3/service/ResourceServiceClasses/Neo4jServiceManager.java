package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.Exceptions.ServiceException;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;


public class Neo4jServiceManager {

    private static Client client;

    private static ConcurrentMap<String, String> neo4jNodeMap = new ConcurrentHashMap<String, String>();
    private static ConcurrentMap<String, String> neo4jRelMap = new ConcurrentHashMap<String, String>();

    private static final String labelValue = "Host";
    private static final String labelUrlProperty = "nodeLabel";

    private static String simpleXmlURL;

    private static Neo4jServiceManager serviceManager = new Neo4jServiceManager();

    private Response serverResponse;

    public Neo4jServiceManager() {
        client = ClientBuilder.newClient();
        if (System.getProperty("it.polito.dp2.NFV.lab3.Neo4JSimpleXMLURL") == null) {
            simpleXmlURL = "http://localhost:8080/Neo4JSimpleXML/rest/data";
        } else {
            simpleXmlURL = System.getProperty("it.polito.dp2.NFV.lab3.Neo4JSimpleXMLURL").concat("/data");
        }
    }

    public static Neo4jServiceManager getInstance() {
        return serviceManager;
    }

    // return the ID of the node received by Neo4jSimpleXML
    public void postNode(Node node, Labels nodeLabels) throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(simpleXmlURL).path("/node").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(node));

            int resCode = serverResponse.getStatusInfo().getStatusCode();

            if (resCode == 400 || resCode == 500)
                throw new ServiceException("Neo4jSimpleXml returned an error, response status code: " + resCode);

            Node nodeResponse = serverResponse.readEntity(Node.class);                              // convert the response into a Node class instance
            String nodeID = nodeResponse.getId();

            Response labelPostResponse = client.target(UriBuilder.fromUri(simpleXmlURL).path("/node/" + nodeID + "/labels").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(nodeLabels));

            resCode = labelPostResponse.getStatusInfo().getStatusCode();

            if (resCode == 400 || resCode == 500 || resCode == 404)
                throw new ServiceException("Neo4jSimpleXml returned an error, response status code: " + resCode);

            neo4jNodeMap.put(node.getProperties().getProperty().get(0).getValue(), nodeID);
        } catch (ResponseProcessingException | IllegalArgumentException | IllegalStateException e) {
            throw new ServiceException("Neo4JSimpleXML client raised an exception: " + e.getMessage());
        }
    }

    // return the id of the relationship received by neo4jSimpleXML
    public void postRelationship(Relationship relationship) throws ServiceException {
        try {
            String srcNodeID = neo4jNodeMap.get(relationship.getSrcNode());
            String destNodeID = neo4jNodeMap.get(relationship.getDstNode());

            if (srcNodeID == null || destNodeID == null)
                throw new ServiceException("the source node and the destination node are empty!" + relationship.getSrcNode() + " " + relationship.getDstNode());

            relationship.setSrcNode(srcNodeID);
            relationship.setDstNode(destNodeID);

            serverResponse = client.target(UriBuilder.fromUri(simpleXmlURL).path("/node/" + srcNodeID + "/relationships").build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .post(Entity.xml(relationship));

            int resCode = serverResponse.getStatusInfo().getStatusCode();

            if (resCode == 400 || resCode == 500)
                throw new ServiceException("Neo4jSimpleXml returned an error, response status code: " + resCode);

        } catch (ResponseProcessingException | IllegalStateException | IllegalArgumentException e) {
            throw new ServiceException("Neo4JSimpleXML client raised an exception: " + e.getMessage());
        }
    }

    public Nodes getReachableHost(String nodeId) throws ServiceException {
        try {
            serverResponse = client.target(UriBuilder.fromUri(simpleXmlURL)
                    .path("/node/" + nodeId)
                    .queryParam(labelUrlProperty, labelValue)
                    .build())
                    .request()
                    .accept(MediaType.APPLICATION_XML)
                    .get();

            int resCode = serverResponse.getStatusInfo().getStatusCode();

            if (resCode == 400 || resCode == 404 || resCode == 500)
                throw new ServiceException("Neo4jSimpleXml returned an error, response status code: " + resCode);

            return serverResponse.readEntity(Nodes.class);
        } catch (ResponseProcessingException rpe) {
            throw new ServiceException("Neo4jSimpleXML client raised an exception: " + rpe.getMessage());
        }
    }

//    private void checkResponse(Response res) throws ServiceException {
//        Response.StatusType resStatus = res.getStatusInfo();
//        if (resStatus.getStatusCode() != 200 &&
//                resStatus.getStatusCode() != 204 &&
//                resStatus.getStatusCode() != 201) {
//            throw new ServiceException("Neo4jSimpleXML server returned an error: " + resStatus.getStatusCode() + " " + resStatus.getReasonPhrase());
//        }
//    }
}
