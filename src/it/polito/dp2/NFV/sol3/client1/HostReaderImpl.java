package it.polito.dp2.NFV.sol3.client1;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.sol3.service.ServiceXML.DeployedNodeType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedHostType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.HostType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.NffgGraphType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.NodeType;
 
public class HostReaderImpl implements HostReader{

	private ExtendedHostType host;
	private NffgGraphType nffg;
	
	public HostReaderImpl(ExtendedHostType host, NffgGraphType nffg) {
		this.host = host;
		this.nffg = nffg;
	}

	@Override
	public String getName() {
		return host.getHostname();
	}

	@Override
	public int getAvailableMemory() {
		return host.getAvailableMemory().intValue();
	}

	@Override
	public int getAvailableStorage() {
		return host.getAvailableStorage().intValue();
	}

	@Override
	public int getMaxVNFs() {
		return host.getMaxVNF().intValue();
	}

	@Override
	public Set<NodeReader> getNodes() {
		Set<NodeReader> nodeReaderSet = new HashSet<NodeReader> ();
		
		for(DeployedNodeType deployedNode: host.getDeployedNodes().getNode()) {
			Predicate<NodeType> nodePredicate = p-> p.getName() == deployedNode.getNodeName();
			NodeType newNode = nffg.getNodes().getNode().stream().filter(nodePredicate).findFirst().get();
			
			NodeReader newNodeReader = new NodeReaderImpl(newNode, nffg);
			nodeReaderSet.add(newNodeReader);
		}
		
		return nodeReaderSet;
	}
}
