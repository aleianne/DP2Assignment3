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

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class Client1Impl implements NfvClient {

    private Map<String, DeployedNffg> nffgMap;
    private NfvDeployerServiceManager serviceManager;

    public Client1Impl() {
        // this is the map that contains the association between the node assigned by the client and the
        nffgMap = new HashMap<String, DeployedNffg>();
        serviceManager = new NfvDeployerServiceManager();
    }

    @Override
    public DeployedNffg deployNffg(NffgDescriptor nffg) throws AllocationException, ServiceException {
        // use a counter for the name of the node
        // the first map is used to associate a nodeDescriptor to a name in order to forward the link of the graph
        // the second map is used to associate a nodeDescriptor to the name returned by the server

        int counter = 0;
        Map<NodeDescriptor, String> nodeMap = new HashMap<>();
        //Map<NodeDescriptor, String> nodeResponseMap = new HashMap<>();

        // convert the nffg descriptor into an instance of xml binded class
        NffgGraphType newGraph = new NffgGraphType();
        newGraph.setNffgName("Nffg");
        newGraph.setNodes(new NffgGraphType.Nodes());
        newGraph.setLinks(new NffgGraphType.Links());

        // create a new xml instance for the nodes inside the graph
        for (NodeDescriptor node : nffg.getNodes()) {
            RestrictedNodeType xmlNode = new RestrictedNodeType();

            xmlNode.setHostname(node.getHostName());
            xmlNode.setVNF(node.getFuncType().getName());
            xmlNode.setName("node" + Integer.toString(counter));
            xmlNode.setNfFg("Nffg");
            nodeMap.put(node, Integer.toString(counter));

            newGraph.getNodes().getNode().add(xmlNode);
            counter++;
        }

        
        counter = 0;
        for (NodeDescriptor node : nffg.getNodes()) {
            for (LinkDescriptor link : node.getLinks()) {
                ExtendedLinkType xmlLink = new ExtendedLinkType();

                String destNode = nodeMap.get(link.getDestinationNode());
                String srcNode = nodeMap.get(link.getSourceNode());

                if (destNode == null || srcNode == null)
                    throw new ServiceException();

                xmlLink.setLinkName("link" + Integer.toString(counter));
                xmlLink.setDestinationNode(destNode);
                xmlLink.setSourceNode(srcNode);
                xmlLink.setThroughput(link.getThroughput());
                xmlLink.setLatency(BigInteger.valueOf(link.getLatency()));
                xmlLink.setOverwrite(false);
                newGraph.getLinks().getLink().add(xmlLink);
                counter++;
            }
        }
        
        System.out.println("nffg name "  + newGraph.getNffgName());
        
        for (RestrictedNodeType node : newGraph.getNodes().getNode()) {
        	System.out.println("node " + node.getName());
        	System.out.println("host" + node.getHostname());
        	System.out.println("vnf " + node.getVNF());
        	System.out.println("nffg " + node.getNfFg());
        }
        
        
        for (ExtendedLinkType link : newGraph.getLinks().getLink()) {
        	System.out.println("link " +link.getLinkName());
        	System.out.println("src node" + link.getDestinationNode());
        	System.out.println("dest node" + link.getSourceNode());
        	System.out.println("throughput " + link.getThroughput());
        	System.out.println("latency " + link.getLatency());
        }

        // forward the graph to the remote service
        NffgGraphType responseGraph = serviceManager.postNffg(newGraph);

//		// put all the nodes deployed into an hashmap that contains all the association of the client node name and the name assigned by the server
//		int index = 0;
//		for(NodeDescriptor node: nffg.getNodes()) {
//			nodeResponseMap.put(node, responseGraph.getNodes().getNode().get(index).getName());
//			index++;
//		}

        //List<RestrictedNodeType> nodesList = responseGraph.getNodes().getNode();

        DeployedNffg deployedNffg = new DeployedNffgImpl(responseGraph.getNffgName(), serviceManager);
        nffgMap.put(responseGraph.getNffgName(), deployedNffg);

        return deployedNffg;
    }

    @Override
    public DeployedNffg getDeployedNffg(String name) throws UnknownEntityException, ServiceException {
        DeployedNffg nffg = nffgMap.get(name);

        if (nffg == null)
            throw new UnknownEntityException();

        return nffg;
    }

}
