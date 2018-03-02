package it.polito.dp2.NFV.sol3.client1;

import java.util.function.Predicate;

import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedLinkType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.NffgGraphType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.RestrictedNodeType;

public class LinkReaderImpl implements LinkReader{

	private ExtendedLinkType link;
	private NffgGraphType nffg;
	private Predicate<RestrictedNodeType> linkPredicate;
	private NfvDeployerServiceManager serviceManager;
	
	public LinkReaderImpl(ExtendedLinkType link, NffgGraphType nffg, NfvDeployerServiceManager serviceManager) {
		this.link = link;
		this.nffg = nffg;
	}

	@Override
	public String getName() {
		return link.getLinkName();
	}

	@Override
	public NodeReader getDestinationNode() {
		// filter the list 
		linkPredicate = p-> p.getName() == link.getDestinationNode();
		RestrictedNodeType destNode = nffg.getNodes().getNode().stream().filter(linkPredicate).findFirst().get();
		NodeReader nr = new NodeReaderImpl(destNode, serviceManager);
		return nr;
	}

	@Override
	public int getLatency() {
		return link.getLatency().intValue();
	}

	@Override
	public NodeReader getSourceNode() {
		// filter the list 
		linkPredicate = p-> p.getName() == link.getSourceNode();
		RestrictedNodeType srcNode = nffg.getNodes().getNode().stream().filter(linkPredicate).findFirst().get();
		NodeReader nr = new NodeReaderImpl(srcNode, serviceManager);
		return nr;
	}

	@Override
	public float getThroughput() {
		return link.getThroughput();
	}
	
	
	
}
