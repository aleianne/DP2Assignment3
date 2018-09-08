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

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class DeployedNffgImpl implements DeployedNffg {

	private NffgGraphType newGraph;
	//private Map<NodeDescriptor, String> nodeDescriptorMap;
	private NfvDeployerServiceManager serviceManager;
	private String nffgId;
	
	
	// the constructor receive the graph, a map that represent the association of nodeDescriptor and name assigned by the server
	// it receives also the reference of the NFV Deployer service
	public DeployedNffgImpl(NffgGraphType newGraph, NfvDeployerServiceManager serviceManager) {
		this.newGraph = newGraph;
		//this.nodeDescriptorMap = nodeDescriptionMap;
		this.serviceManager = serviceManager;
		this.nffgId = newGraph.getNffgName();
	}
	
	@Override
	public NodeReader addNode(VNFTypeReader type, String hostName) throws AllocationException, ServiceException {		
		// create a new node
		RestrictedNodeType xmlNode = new RestrictedNodeType();
		xmlNode.setVNF(type.getName());
		xmlNode.setHostname(hostName);
		xmlNode.setNfFg(nffgId);
		
		// forward the node to the webserver
		RestrictedNodeType resNode = serviceManager.postNode(xmlNode, nffgId);

		// add a new node into the nffg representation saved into the client
		//newGraph.getNodes().getNode().add(resNode);

		// put the information into the nodeReader interface
		return new NodeReaderImpl(resNode, serviceManager);
	}

	@Override
	public LinkReader addLink(NodeReader source, NodeReader dest, boolean overwrite)
			throws NoNodeException, LinkAlreadyPresentException, ServiceException {
		// get the link name from the resolver
//
//		String destName = nodeDescriptorMap.get(dest);
//		String sourceName = nodeDescriptorMap.get(source);
		
		ExtendedLinkType xmlLink = new ExtendedLinkType();
		xmlLink.setDestinationNode(dest.getName());
		xmlLink.setSourceNode(source.getName());
		xmlLink.setOverwrite(overwrite);
		
//		if(destName == null || sourceName == null)
//			throw new NoNodeException();
		
		ExtendedLinkType link = serviceManager.postLink(xmlLink, nffgId);

		// add the link into the nffg representation saved into the client
		//newGraph.getLinks().getLink().add(link);
	
		// create a new link reader interface  in order to read the link infos
		return new LinkReaderImpl(link, serviceManager);
	}

	@Override
	public NffgReader getReader() throws ServiceException {
		return new NffgReaderImpl(newGraph, serviceManager);
	}

}
