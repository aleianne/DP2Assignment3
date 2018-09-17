package it.polito.dp2.NFV.sol3.client2;

import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.NfvDeployer;
import it.polito.dp2.NFV.sol3.ServiceXML.HostType;
import it.polito.dp2.NFV.sol3.ServiceXML.RestrictedNodeType;

import java.util.List;

public class HostReaderImpl implements HostReader {

    private HostType host;
    private NfvHelper nfvHelper;

    protected HostReaderImpl(HostType host, NfvHelper nfvHelper) {
        this.host = host;
        this.nfvHelper = nfvHelper;
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
        List<RestrictedNodeType> hostDeployedNodeList = nfvHelper.getDeployedNode(host.getHostname());
        NodeReader nr;

        for (RestrictedNodeType node : hostDeployedNodeList) {
            nr = new NodeReaderImpl(node, nfvHelper);
            nrSet.add(nr);
        }

//        try {
//            NodesType nodes = serviceManager.getHostNode(host.getHostname());
//            for (RestrictedNodeType node : nodes.getNode()) {
//                NodeReader nr = new NodeReaderImpl(node, serviceManager);
//                nrSet.add(nr);
//            }
//        } catch (ServiceException se) {
//            System.err.println("impossible to implement the node reader");
//            System.err.println(se.getMessage());
//        }
        return nrSet;
    }

}
