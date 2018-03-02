package it.polito.dp2.NFV.sol3.client1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NodeReaderImpl implements NodeReader {

	private RestrictedNodeType node;
	private NfvDeployerServiceManager serviceManager;
	private NffgGraphType graphResponse;
	
	public NodeReaderImpl(RestrictedNodeType newNode, NfvDeployerServiceManager serviceManager) {
		this.node = newNode;
		this.serviceManager = serviceManager;
		
		try {
			graphResponse = serviceManager.getGraph(node.getNfFg());
		} catch(ServiceException se) {
			
		}
	}
	
	@Override
	public String getName() {
		return node.getName();
	}

	@Override
	public VNFTypeReader getFuncType() {
		VNFTypeReader vnfReader = null;
		
		try {
			vnfReader = new VNFTypeReaderImpl(serviceManager.getFunction(node.getVNF()));
		} catch(ServiceException se) {
			System.err.println(se.getMessage());
		}
		
		return vnfReader;
	}

	@Override
	public HostReader getHost() {
		HostReader hostReader = null;
		
		try {
			HostType host = (HostType) serviceManager.getHost(node.getHostname());		
			hostReader = new HostReaderImpl(host, serviceManager);
		} catch(ServiceException se) {
			System.err.println(se.getMessage());
		}
		return hostReader;
	}

	@Override
	public Set<LinkReader> getLinks() {
		Set<LinkReader> linkReaderSet = new HashSet<LinkReader> ();
		
		// filter only those that have the source node name equal to this node
		for(ExtendedLinkType link: graphResponse.getLinks().getLink()) {
			if(link.getSourceNode() == node.getName()) {
				LinkReader lr = new LinkReaderImpl(link, graphResponse, serviceManager);
				linkReaderSet.add(lr);
			}
		}
		return linkReaderSet;
	}

	@Override
	public NffgReader getNffg() {
		NffgReader nffgReader = null;
		
		try {
			nffgReader = new NffgReaderImpl(graphResponse, serviceManager);
		} catch(ServiceException se) {
			System.err.println(se.getMessage());
		}
		
		return nffgReader;
	}

}
