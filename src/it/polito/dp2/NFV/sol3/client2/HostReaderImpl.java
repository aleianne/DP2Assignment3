package it.polito.dp2.NFV.sol3.client2;

import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class HostReaderImpl implements HostReader {

	private ExtendedHostType host;
	private NfvDeployerServiceManager serviceManager;
	
	public HostReaderImpl(ExtendedHostType host, NfvDeployerServiceManager serviceManager) {
		this.host = host;
		this.serviceManager = serviceManager;
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
		Set<NodeReader> nrSet = null;
		try {
			NodesType nodes = serviceManager.getHostNode(host.getHostname());
			for(RestrictedNodeType node: nodes.getNode()) {
				NodeReader nr = new NodeReaderImpl(node, serviceManager);
				nrSet.add(nr);
			}
		} catch(ServiceException se) {
			System.err.println("impossible to implement the node reader");
			System.err.println(se.getMessage());
		}
		return nrSet;
	}

}
