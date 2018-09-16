package it.polito.dp2.NFV.sol3.client1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.lab3.UnknownEntityException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NodeReaderImpl implements NodeReader {

    private RestrictedNodeType node;
    private NfvDeployerServiceManager serviceManager;
    private NffgGraphType graphResponse;

    protected NodeReaderImpl(RestrictedNodeType newNode, NfvDeployerServiceManager serviceManager) {
        this.node = newNode;
        this.serviceManager = serviceManager;
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
        } catch (ServiceException se) {
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
        } catch (ServiceException se) {
            System.err.println(se.getMessage());
        }

        return hostReader;
    }

    @Override
    public Set<LinkReader> getLinks() {
        Set<LinkReader> linkReaderSet = new HashSet<>();

        try {
            LinksType links = serviceManager.getGraphLinks(node.getNfFg());

            // filter only those that have the source node name equal to this node
            for (LinkType link : links.getLink()) {
                if (link.getSourceNode() == node.getName())
                    linkReaderSet.add(new LinkReaderImpl(link, node.getNfFg(), serviceManager));
            }
        } catch (ServiceException se) {
            System.err.println(se.getMessage());
        }

        return linkReaderSet;
    }

    @Override
    public NffgReader getNffg() {
        NffgReader nffgReader = null;

        try {
            NffgGraphType responseGraph = serviceManager.getGraph(node.getNfFg());
            nffgReader = new NffgReaderImpl(graphResponse, serviceManager);
        } catch (ServiceException | UnknownEntityException se) {
            System.err.println(se.getMessage());
        }

        return nffgReader;
    }

}
