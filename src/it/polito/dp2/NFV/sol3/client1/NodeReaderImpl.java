package it.polito.dp2.NFV.sol3.client1;

import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;

public class NodeReaderImpl implements NodeReader {

	private NodeType node;
	
	public NodeReaderImpl(NodeType newNode) {
		node = newNode;
	}
	
	@Override
	public String getName() {
		return node.getNodeName();
	}

	@Override
	public VNFTypeReader getFuncType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HostReader getHost() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<LinkReader> getLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NffgReader getNffg() {
		// TODO Auto-generated method stub
		return null;
	}

}
