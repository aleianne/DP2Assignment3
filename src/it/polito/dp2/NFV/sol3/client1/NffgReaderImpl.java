package it.polito.dp2.NFV.sol3.client1;

import java.util.*;
import java.util.function.Predicate;

import javax.xml.datatype.DatatypeConfigurationException;

import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.DateConverter;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NffgReaderImpl implements NffgReader {

    private NffgGraphType newGraph;
    private NfvDeployerServiceManager serviceManager;

    public NffgReaderImpl(NffgGraphType newGraph, NfvDeployerServiceManager serviceManager) throws ServiceException {
        if (newGraph == null)
            throw new ServiceException();
        else {
            this.newGraph = newGraph;
            this.serviceManager = serviceManager;
        }
    }

    @Override
    public String getName() {
        return newGraph.getNffgName();
    }

    @Override
    public Calendar getDeployTime() {
        Calendar deployDate;
        DateConverter dateConverter = new DateConverter();
        try {
            // convert from  xml gregorian calendar to Calendar instance
            deployDate = dateConverter.fromXMLGregorianCalendar(newGraph.getDeployDate());
        } catch (DatatypeConfigurationException dce) {
            deployDate = null;
        }
        return deployDate;
    }

    @Override
    public NodeReader getNode(String nodeName) {
        List<RestrictedNodeType> graphNodeList = newGraph.getNodes().getNode();

        // return null if the node is empty
        if (graphNodeList.isEmpty())
            return null;

        // check if the node searched is inside the graph
        Predicate<RestrictedNodeType> nodePredicate = p -> p.getName() == nodeName;
        Optional<RestrictedNodeType> retrievedObject = graphNodeList.stream().filter(nodePredicate).findFirst();

        if (((Optional) retrievedObject).isPresent())
            return new NodeReaderImpl(retrievedObject.get(), serviceManager);

        return null;
    }

    @Override
    public Set<NodeReader> getNodes() {
        Set<NodeReader> nodeSet = new HashSet<>();

        try {
            NodesType nodes = serviceManager.getGraphNodes(newGraph.getNffgName());

            // put all graph nodes into the node reader set
            for (RestrictedNodeType node : nodes.getNode())
                nodeSet.add(new NodeReaderImpl(node, serviceManager));

        } catch (ServiceException se) {
            System.err.println(se.getMessage());
        }

        return nodeSet;
    }

}
