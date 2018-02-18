package it.polito.dp2.NFV.sol3.client1;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.DeployedNffg;
import it.polito.dp2.NFV.lab3.NffgDescriptor;
import it.polito.dp2.NFV.lab3.NfvClient;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.lab3.UnknownEntityException;

public class Client1Impl implements NfvClient {

	@Override
	public DeployedNffg deployNffg(NffgDescriptor nffg) throws AllocationException, ServiceException {
		NfvDeployerServiceManager serviceManager = new NfvDeployerServiceManager();
		
		// convert the nffg descriptor into the xml type for the forward
		NffgGraphType newGraph = new NffgGraphType();
		newGraph.setNodes(new NffgGraphType.Nodes());
		newGraph.setLinks(new NffgGraphType.Links());
		
		for(NodeRepresentation node: nffg.getNodes()) {
			NodeType xmlNode = new NodeType();
			
			xmlNode.setHostname(node.getHostName());
			xmlNode.setVNF(node.getFuncType().getName());
			newGraph.getNodes().getNode().add(xmlNode);
			
			for(LinkRepresentation link: node.getLinks()) {
				ExtendedLinkType xmlLink = new ExtendedLinkType();
				
				
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
