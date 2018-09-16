package it.polito.dp2.NFV.sol3.client1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.lab3.UnknownEntityException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.DeployedNodeType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedHostType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.HostType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.NffgGraphType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.NodesType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.RestrictedNodeType;


public class HostReaderImpl implements HostReader {

    private HostType host;
    private NfvDeployerServiceManager serviceManager;

    protected HostReaderImpl(HostType host2, NfvDeployerServiceManager serviceManager) {
        this.host = host2;
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
        Set<NodeReader> nodeReaderSet = new HashSet<>();
        NodesType nodeList = null;

        try {
            nodeList = serviceManager.getHostNode(host.getHostname());
            for (RestrictedNodeType deployedNode : nodeList.getNode()) {
                NodeReader newNodeReader = new NodeReaderImpl(deployedNode, serviceManager);
                nodeReaderSet.add(newNodeReader);
            }
        } catch (ServiceException | UnknownEntityException se) {
            System.err.println(se.getMessage());
        }

        return nodeReaderSet;
    }
}
