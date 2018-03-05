package it.polito.dp2.NFV.sol3.client2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NodeReaderImpl implements NodeReader {

	private NfvDeployerServiceManager serviceManager;
	private RestrictedNodeType node;
	private NffgGraphType graph;
	
	public NodeReaderImpl(RestrictedNodeType node, NfvDeployerServiceManager serviceManager) { 
		this.node = node;
		this.serviceManager = serviceManager;
	}
	
	@Override
	public String getName() {
		return node.getName();
	}

	@Override
	public VNFTypeReader getFuncType() {
		VNFTypeReader vnf = null;
		try {
			FunctionType function = serviceManager.getFunction(node.getVNF());
			vnf = new VNFTypeReaderImpl(function);
		} catch(ServiceException se) {
			System.err.println("impossible to implement a VNF reader interface");
			System.err.println(se.getMessage());
		}
		return vnf;
	}

	@Override
	public HostReader getHost() {
		HostReader hr;
		try {
			ExtendedHostType host = serviceManager.getHost(node.getHostname());
			hr = new HostReaderImpl(host, serviceManager);
		} catch(ServiceException se) {
			System.err.println("impossible to implement the host reader interface");
			System.err.println(se.getMessage());
		}
		return hr;
	}

	@Override
	public Set<LinkReader> getLinks() {
		Set<LinkReader> lrSet = new HashSet<LinkReader> ();
		for(ExtendedLinkType link: graph.getLinks().getLink()) {
			LinkReader lr = new LinkReaderImpl(link, graph, serviceManager);
			lrSet.add(lr);
		}
		return lrSet;
	}

	@Override
	public NffgReader getNffg() {
		NffgReader nfgr = new NffgReaderImpl(node.getNfFg(), serviceManager);
		return nfgr;
	}

}
