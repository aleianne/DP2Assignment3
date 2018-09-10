package it.polito.dp2.NFV.sol3.service.DaoClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.Exceptions.GraphNotFoundException;
import it.polito.dp2.NFV.sol3.service.Exceptions.UnknowNodeException;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.NoNodeException;
import it.polito.dp2.NFV.lab3.ServiceException;

public class GraphDao {

    private static final String nodeBaseName = "Node";
    private static final String linkBaseName = "Link";
    //private static Map<String, ExtendedLinkType> linkIDMap = new HashMap<String, ExtendedLinkType>();
    private static final String nffgBaseName = "Nffg";
    private static final String nodeLabelName = "Node";
    private static final String hostRelationshipLabel = "AllocatedOn";
    private static final String nodeRelatioshipLabel = "ForwardTo";
    private static ConcurrentMap<String, NffgGraphType> graphMap = new ConcurrentHashMap<String, NffgGraphType>();
    private static ConcurrentMap<String, RestrictedNodeType> nodeIDMap = new ConcurrentHashMap<String, RestrictedNodeType>();
    private static AtomicInteger linkCounter = new AtomicInteger(0);
    private static AtomicInteger nodeCounter = new AtomicInteger(0);
    private static AtomicInteger nffgCounter = new AtomicInteger(0);
    private static GraphDao graphDao = new GraphDao();
    private Neo4jServiceManager neo4jXMLclient;

    public static GraphDao getInstance() {
        return graphDao;
    }

    /*
     * allocate the graph into the system and put it into the neo4j database and into the hashmap
     */
    public String createNffg(NffgGraphType newNffg) throws ServiceException {
        // this map convert the name given by the user into a name assigned by this server
        Map<String, String> nodeNameResolverMap = new HashMap<>();

        // create the nffg name
        String nffgName = nffgBaseName.concat(Integer.toString(nffgCounter.incrementAndGet()));
        newNffg.setNffgName(nffgName);

        // update the node list with the new name
        for (RestrictedNodeType node : newNffg.getNodes().getNode()) {
            String newNodeName = nodeBaseName.concat(Integer.toString(nodeCounter.incrementAndGet()));
            String oldNodeName = node.getName();
            nodeNameResolverMap.put(oldNodeName, newNodeName);

            node.setNfFg(nffgName);
            node.setName(newNodeName);

            nodeIDMap.put(newNodeName, node);
        }

        // update the list of links with the new name
        for (ExtendedLinkType link : newNffg.getLinks().getLink()) {
            String linkName = linkBaseName.concat(Integer.toString(linkCounter.incrementAndGet()));

            link.setDestinationNode(nodeNameResolverMap.get(link.getDestinationNode()));
            link.setSourceNode(nodeNameResolverMap.get(link.getSourceNode()));
            link.setLinkName(linkName);
        }

        graphMap.put(nffgName, newNffg);

        neo4jXMLclient = Neo4jServiceManager.getInstance();

        // forward the graph to the remote database
        Node neo4jNode = new Node();
        Labels nodeLabel = new Labels();
        Property nodeProperty = new Property();
        Relationship neo4jRelationship = new Relationship();

        // declare a new property inside the neo4jNode
        neo4jNode.setProperties(new Properties());
        neo4jNode.getProperties().getProperty().add(nodeProperty);

        nodeLabel.getLabel().add(nodeLabelName);

        for (RestrictedNodeType node : newNffg.getNodes().getNode()) {
            // create a neo4j node for the forwarding ;
            neo4jNode.getProperties().getProperty().get(0).setName("name");
            neo4jNode.getProperties().getProperty().get(0).setValue(node.getName());

            neo4jXMLclient.postNode(neo4jNode, nodeLabel);

            // set the relationship info between the node and the host
            neo4jRelationship.setDstNode(node.getHostname());
            neo4jRelationship.setSrcNode(node.getName());
            neo4jRelationship.setType(hostRelationshipLabel);
            neo4jXMLclient.postRelationship(neo4jRelationship);
        }

        for (ExtendedLinkType link : newNffg.getLinks().getLink()) {
            // set link information
            neo4jRelationship.setDstNode(link.getDestinationNode());
            neo4jRelationship.setSrcNode(link.getSourceNode());
            neo4jRelationship.setType(nodeRelatioshipLabel);
            neo4jXMLclient.postRelationship(neo4jRelationship);
        }

        return nffgName;
    }

    /*
     * read a single graph given the nffgId
     */
    public NffgGraphType readGraph(String nffgId) {
        return graphMap.get(nffgId);
    }

    /*
     * read all the nffg that are stored into the database
     */
    public Collection<NffgGraphType> readAllGraphs() {
        return graphMap.values();
    }

    /*
     * update the node list of a specific graph
     */
    public void updateGraph(String nffgId, RestrictedNodeType newNode) throws ServiceException, GraphNotFoundException {
        // search the nffg into the hashmap
        NffgGraphType queryResultGraph = graphMap.get(nffgId);

        if (queryResultGraph != null) {
            neo4jXMLclient = Neo4jServiceManager.getInstance();

            // assign a new name to the node
            String nodeName = nodeBaseName;
            nodeName = nodeName.concat(Integer.toString(nodeCounter.incrementAndGet()));
            newNode.setName(nodeName);

            // create a ne4j node
            Node neo4jNode = new Node();
            Property nodeProperty = new Property();
            Labels nodeLabels = new Labels();

            // synchronize here the query result graph
            synchronized (queryResultGraph) {
                nodeProperty.setName("name");
                nodeProperty.setValue(newNode.getName());

                nodeLabels.getLabel().add(nodeLabelName);
                nodeLabels.getLabel().add(nffgId);

                neo4jNode.setProperties(new Properties());
                neo4jNode.getProperties().getProperty().add(nodeProperty);

                queryResultGraph.getNodes().getNode().add(newNode);
            }

            // forward the node to the database
            neo4jXMLclient.postNode(neo4jNode, nodeLabels);

            // forward the relationship between the node and the host where is allocated
            Relationship neo4jRelationship = new Relationship();
            neo4jRelationship.setDstNode(newNode.getHostname());
            neo4jRelationship.setSrcNode(newNode.getName());
            neo4jRelationship.setType(hostRelationshipLabel);
            neo4jXMLclient.postRelationship(neo4jRelationship);

            return;
        }

        throw new GraphNotFoundException("the graph " + nffgId + "doesn't exist");
    }

    /*
     * insert into a specific graph a new link
     */
    public void updateGraph(String nffgId, ExtendedLinkType newLink) throws ServiceException, LinkAlreadyPresentException, InternalServerErrorException, GraphNotFoundException, NoNodeException {
        // search the graph by its ID
        NffgGraphType queryResultGraph = graphMap.get(nffgId);

        if (queryResultGraph != null) {
            // synchronize the access to the single graph where the link should allocated
            synchronized (queryResultGraph) {

                List<ExtendedLinkType> graphLinkList = queryResultGraph.getLinks().getLink();
                List<RestrictedNodeType> GraphNodeList = queryResultGraph.getNodes().getNode();

                // filter the list in order to obtain all the links that have a specified source node and a specified destination node
                Predicate<ExtendedLinkType> linkPredicate = p -> p.getDestinationNode() == newLink.getDestinationNode() && p.getSourceNode() == newLink.getSourceNode();

                // filter the node list, check if there are the nodes specified
                Predicate<RestrictedNodeType> nodePredicate = p -> p.getName() == newLink.getDestinationNode();
                Predicate<RestrictedNodeType> nodePredicate2 = p -> p.getName() == newLink.getSourceNode();

                if (GraphNodeList.stream().filter(nodePredicate).findFirst().get() == null
                        || GraphNodeList.stream().filter(nodePredicate2).findFirst().get() == null)
                    throw new NoNodeException();

                if (graphLinkList.stream().filter(linkPredicate).findFirst().get() == null && !newLink.isOverwrite())
                    throw new LinkAlreadyPresentException();

                // assign to the link a new name
                String linkName = linkBaseName;
                newLink.setLinkName(linkName.concat(Integer.toString(linkCounter.incrementAndGet())));
                queryResultGraph.getLinks().getLink().add(newLink);
            }

            // forward the relationship to Neo4j Database
            Relationship neo4jRelationship = new Relationship();
            neo4jRelationship.setDstNode(newLink.getDestinationNode());
            neo4jRelationship.setSrcNode(newLink.getSourceNode());
            neo4jRelationship.setType(nodeRelatioshipLabel);
            neo4jXMLclient.postRelationship(neo4jRelationship);

            return;
        }

        throw new GraphNotFoundException("the graph " + nffgId + " doesn't exist into the database");
    }

    /*
     *  this method is not implemented due to the fact that the assignment doesn't require it
     */
    public void deleteGraph() {
    }


    /*
     *  query the map in order to get the node correspondent to the name
     */
    public RestrictedNodeType queryGraph(String nodename) {
        return nodeIDMap.get(nodename);
    }
}
