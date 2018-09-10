package it.polito.dp2.NFV.sol3.client2;

import java.util.function.Predicate;

import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class LinkReaderImpl implements LinkReader {

    private NfvDeployerServiceManager serviceManager;
    private ExtendedLinkType link;
    private NffgGraphType nffg;
    private Predicate<RestrictedNodeType> nodePredicate;

    public LinkReaderImpl(ExtendedLinkType link, NffgGraphType nffg, NfvDeployerServiceManager serviceManager) {
        this.link = link;
        this.nffg = nffg;
        this.serviceManager = serviceManager;
    }

    @Override
    public String getName() {
        return link.getLinkName();
    }

    @Override
    public NodeReader getDestinationNode() {
        nodePredicate = p -> p.getName() == link.getDestinationNode();
        RestrictedNodeType node = nffg.getNodes().getNode().stream().filter(nodePredicate).findFirst().get();
        if (node == null) {
            System.err.println("fatal error, the node specified into the node doesn't exist");
            return null;
        } else {
            NodeReader nr = new NodeReaderImpl(node, serviceManager);
            return nr;
        }
    }

    @Override
    public int getLatency() {
        return link.getLatency().intValue();
    }

    @Override
    public NodeReader getSourceNode() {
        nodePredicate = p -> p.getName() == link.getSourceNode();
        RestrictedNodeType node = nffg.getNodes().getNode().stream().filter(nodePredicate).findFirst().get();
        if (node == null) {
            System.err.println("fatal error, the node specified into the node doesn't exist");
            return null;
        } else {
            NodeReader nr = new NodeReaderImpl(node, serviceManager);
            return nr;
        }
    }

    @Override
    public float getThroughput() {
        return link.getThroughput();
    }

}
