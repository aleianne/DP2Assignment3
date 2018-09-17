package it.polito.dp2.NFV.sol3.client1;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.DeployedNffg;
import it.polito.dp2.NFV.lab3.LinkDescriptor;
import it.polito.dp2.NFV.lab3.NffgDescriptor;
import it.polito.dp2.NFV.lab3.NfvClient;
import it.polito.dp2.NFV.lab3.NodeDescriptor;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.lab3.UnknownEntityException;

import it.polito.dp2.NFV.sol3.ServiceXML.*;

public class Client1Impl implements NfvClient {

//    private Map<String, DeployedNffg> nffgMap;
    private NfvDeployerServiceManager serviceManager;
    private int nffgCounter;
    private static final String nffgNameLabel = "Nffg";
    private static final String nodeNameLabel = "Node";

    public Client1Impl() {
        // this is the map that contains the association between the node assigned by the client and the
        //nffgMap = new HashMap<String, DeployedNffg>();
        serviceManager = new NfvDeployerServiceManager();
        nffgCounter = 0;
    }

    @Override
    public DeployedNffg deployNffg(NffgDescriptor nffg) throws AllocationException, ServiceException {
        // use a counter for the node name
        // the first map is used to associate a nodeDescriptor to a name in order to forward the link of the graph
        // the second map is used to associate a nodeDescriptor to the name returned by the server

        int counter = 0;
        // associate a node reference to a random assigned name
        Map<NodeDescriptor, String> nodeMap = new HashMap<>();

        // create a new nffg name
        String nffgName = nffgNameLabel.concat(Integer.toString(nffgCounter));

        // convert the nffg descriptor into an instance of xml mapped class
        NffgGraphType graphToBeDeployed = new NffgGraphType();
        graphToBeDeployed.setNffgName(nffgName);
        graphToBeDeployed.setNodes(new NffgGraphType.Nodes());
        graphToBeDeployed.setLinks(new NffgGraphType.Links());

        // create a new xml instance for the nodes inside the graph
        for (NodeDescriptor nodeDescriptor : nffg.getNodes()) {
            RestrictedNodeType xmlNode = new RestrictedNodeType();

            String nodeName = nodeNameLabel.concat(Integer.toString(counter));

            xmlNode.setHostname(nodeDescriptor.getHostName());
            xmlNode.setVNF(nodeDescriptor.getFuncType().getName());
            xmlNode.setName(nodeName);
            xmlNode.setNfFg(nffgName);
            nodeMap.put(nodeDescriptor, nodeName);

            graphToBeDeployed.getNodes().getNode().add(xmlNode);
            counter++;
        }

        // refresh counter
        counter = 0;
        for (NodeDescriptor nodeDescriptor : nffg.getNodes()) {
            for (LinkDescriptor linkDescriptor : nodeDescriptor.getLinks()) {
                LinkType xmlLink = new LinkType();

                String destNode = nodeMap.get(linkDescriptor.getDestinationNode());
                String srcNode = nodeMap.get(linkDescriptor.getSourceNode());

                if (destNode == null || srcNode == null)
                    throw new ServiceException();

                xmlLink.setLinkName("link" + Integer.toString(counter));
                xmlLink.setDestinationNode(destNode);
                xmlLink.setSourceNode(srcNode);
                xmlLink.setThroughput(linkDescriptor.getThroughput());
                xmlLink.setLatency(BigInteger.valueOf(linkDescriptor.getLatency()));
                graphToBeDeployed.getLinks().getLink().add(xmlLink);
                counter++;
            }
        }
        
//        System.out.println("nffg name "  + newGraph.getNffgName());
//
//        for (RestrictedNodeType node : newGraph.getNodes().getNode()) {
//        	System.out.println("node " + node.getName());
//        	System.out.println("host " + node.getHostname());
//        	System.out.println("vnf " + node.getVNF());
//        	System.out.println("nffg " + node.getNfFg());
//        }
//
//
//        for (ExtendedLinkType link : newGraph.getLinks().getLink()) {
//        	System.out.println("link " +link.getLinkName());
//        	System.out.println("src node " + link.getDestinationNode());
//        	System.out.println("dest node " + link.getSourceNode());
//        	System.out.println("throughput " + link.getThroughput());
//        	System.out.println("latency " + link.getLatency());
//        }

        // forward the graph to the remote service
        NffgGraphType responseGraph = serviceManager.postNffg(graphToBeDeployed);

        DeployedNffg deployedNffg = new DeployedNffgImpl(responseGraph.getNffgName(), serviceManager);
        nffgCounter++;

        return deployedNffg;
    }

    @Override
    public DeployedNffg getDeployedNffg(String name) throws UnknownEntityException, ServiceException {

        if (name == null)
            throw new ServiceException();

        NffgGraphType retrievedGraph = serviceManager.getGraph(name);
        return new DeployedNffgImpl(retrievedGraph.getNffgName(), serviceManager);
    }

}
