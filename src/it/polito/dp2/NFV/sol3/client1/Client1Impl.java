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
		nffgMap = new HashMap<String, DeployedNffg> ();
		serviceManager = new NfvDeployerServiceManager();
	}

	@Override
	public DeployedNffg deployNffg(NffgDescriptor nffg) throws AllocationException, ServiceException {
		// use a counter for the name of the node
		// the first map is used to associate a nodeDescriptor to a name in order to forward the link of the graph
		// the second map is used to assocate a nodeDescriptor to the name returned by the server
		
		int nodeCounter = 0;
		Map<NodeDescriptor, String> nodeMap = new HashMap<NodeDescriptor, String> ();
		Map<NodeDescriptor, String> nodeResponseMap = new HashMap<NodeDescriptor, String> ();
		
		// convert the nffg descriptor into the xml type for the forward
		NffgGraphType newGraph = new NffgGraphType();
		newGraph.setNodes(new NffgGraphType.Nodes());
		newGraph.setLinks(new NffgGraphType.Links());
		
		// create the xml instance for the node inside the graph
		for(NodeDescriptor node: nffg.getNodes()) {
			NodeType xmlNode = new NodeType();
			
			xmlNode.setHostname(node.getHostName());
			xmlNode.setVNF(node.getFuncType().getName());
			nodeMap.put(node, Integer.toString(nodeCounter));
			
			newGraph.getNodes().getNode().add(xmlNode);	
			nodeCounter++;
		}
		
		// crete the links xml description
		for(NodeDescriptor node: nffg.getNodes()) {
			for(LinkDescriptor link: node.getLinks()) {
				ExtendedLinkType xmlLink = new ExtendedLinkType();
			
				String destNode = nodeMap.get(link.getDestinationNode());
				String srcNode = nodeMap.get(link.getSourceNode());
				
				if(destNode == null || srcNode == null) 
					throw new ServiceException();
				
				xmlLink.setDestinationNode(destNode);
				xmlLink.setSourceNode(srcNode);
				xmlLink.setThroughput(link.getThroughput());
				xmlLink.setLatency(BigInteger.valueOf(link.getLatency()));
				newGraph.getLinks().getLink().add(xmlLink);
			}
		}
		
		// forward the graph to the remote service
		NffgGraphType responseGraph = serviceManager.postNffg(newGraph);
		
		// put all the node deployed into an hashmap that contain all the association of node and name assigned by the server
		int index = 0;
		for(NodeDescriptor node: nffg.getNodes()) {
			nodeResponseMap.put(node, responseGraph.getNodes().getNode().get(index).getName());
			index++;
		}
		
		DeployedNffg deployedNffg = new DeployedNffgImpl(responseGraph, nodeResponseMap, serviceManager);
		nffgMap.put(responseGraph.getNffgName(), deployedNffg);
		
		return deployedNffg;
	}

	@Override
	public DeployedNffg getDeployedNffg(String name) throws UnknownEntityException, ServiceException {

		return null;
	}

}
