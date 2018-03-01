package it.polito.dp2.NFV.sol3.client1;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NodeReaderImpl implements NodeReader {

	private NodeType node;
	private NffgGraphType nffg;
	
	public NodeReaderImpl(NodeType newNode, NffgGraphType nffg) {
		this.node = newNode;
		this.nffg = nffg;
	}
	
	@Override
	public String getName() {
		return node.getName();
	}

	@Override
	public VNFTypeReader getFuncType() {
		return null;
	}

	@Override
	public HostReader getHost() {
		NfvDeployerServiceManager serviceManager = new NfvDeployerServiceManager();
		HostReader hostReader = null;
		
		try {
			HostType host = serviceManager.getHost(node.getHostname());		
			hostReader = new HostReaderImpl(host);
		} catch(ServiceException se) {
			
		}
		
		return hostReader;
	}

	@Override
	public Set<LinkReader> getLinks() {
		Set<LinkReader> linkReaderSet = new HashSet<LinkReader> ();
		
		// filter only those that have the source node name equal to this node
		for(ExtendedLinkType link: nffg.getLinks().getLink()) {
			if(link.getSourceNode() == node.getName()) {
				LinkReader lr = new LinkReaderImpl(link);
				linkReaderSet.add(lr);
			}
		}
		return linkReaderSet;
	}

	@Override
	public NffgReader getNffg() {
		return new NffgReaderImpl(nffg);
	}

}
