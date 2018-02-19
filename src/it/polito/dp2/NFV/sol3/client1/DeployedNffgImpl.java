package it.polito.dp2.NFV.sol3.client1;

import java.util.Map;

import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.DeployedNffg;
import it.polito.dp2.NFV.lab3.LinkAlreadyPresentException;
import it.polito.dp2.NFV.lab3.NoNodeException;
import it.polito.dp2.NFV.lab3.NodeDescriptor;
import it.polito.dp2.NFV.lab3.ServiceException;

public class DeployedNffgImpl implements DeployedNffg{

	private NffgGraphType newGraph;
	private Map<NodeDescriptor, String> nodeDescriptorMap;
	private NfvDeployerServiceManager serviceManager;
	
	public DeployedNffgImpl(NffgGraphType newGraph, Map<NodeDescription, String> nodeDescriptionMap, NfvDeployerServiceManager serviceManager) {
		this.newGraph = newGraph;
		this.nodeDescriptorMap = nodeDescriptionMap;
		this.serviceManager = serviceManager;
	}
	
	@Override
	public NodeReader addNode(VNFTypeReader type, String hostName) throws AllocationException, ServiceException {
		// create a new node
		NodeType xmlNode = new NodeType();
		xmlNode.setVNF(type.getName());
		xmlNode.setHostname(hostName);
		
		serviceManager.postNode(xmlNode, nffgName);
		
		// put the information into the nodeReader interface
		
		
		return null;
	}

	@Override
	public LinkReader addLink(NodeReader source, NodeReader dest, boolean overwrite)
			throws NoNodeException, LinkAlreadyPresentException, ServiceException {
		ExtendedLinkType xmlLink = new ExtendedLinkType();
		
		String destName = nodeDescriptorMap.get(dest);
		String sourceName = nodeDescriptorMap.get(source);
		
		xmlLink.setDestinationNode(destName);
		xmlLink.setSourceNode(sourceName);
		xmlLink.setOverwrite(overwrite);
		
		if(destName == null || sourceName == null)
			throw new NoNodeException();
		
		ExtendedLinkType link = serviceManager.postLink(xmlLink, nffgName);
		
		// set the date into the 
		
		
		
		return null;
	}

	@Override
	public NffgReader getReader() throws ServiceException {
		
		return null;
	}

}
