package it.polito.dp2.NFV.sol3.client1;

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

public class Client1Impl implements NfvClient {
	
	private Map<String, DeployedNffg> nffgMap = new HashMap<String, DeployedNffg> ();

	@Override
	public DeployedNffg deployNffg(NffgDescriptor nffg) throws AllocationException, ServiceException {
		NfvDeployerServiceManager serviceManager = new NfvDeployerServiceManager();
		
		// use a counter for the name of the node
		int nodeCounter = 0;
		Map<NodeDescriptor, String> nodeMap = new HashMap<NodeDescriptor, String> ();
		
		
		// convert the nffg descriptor into the xml type for the forward
		NffgGraphType newGraph = new NffgGraphType();
		newGraph.setNodes(new NffgGraphType.Nodes());
		newGraph.setLinks(new NffgGraphType.Links());
		
		// create the nodes xml representaion
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
			
				// TODO change the destination and the source of the link
				xmlLink.setDestinationNode(nodeMap.get(link.getDestinationNode()));
				xmlLink.setSourceNode(nodeMap.get(link.getSourceNode()));
				xmlLink.setTroughtput(link.getThroughput());
				xmlLink.setLatency(link.getLatency());
			
				newGraph.getLinks().getLink().add(xmlLink);
			}
		}
		
		serviceManager.postNffg(newGraph);
		
		
		return null;
	}

	@Override
	public DeployedNffg getDeployedNffg(String name) throws UnknownEntityException, ServiceException {

		return null;
	}

}
