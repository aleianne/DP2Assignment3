package it.polito.dp2.NFV.sol3.client2;

import java.util.function.Predicate;

import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.List;
import java.util.Optional;

public class LinkReaderImpl implements LinkReader {

    //private NfvDeployerServiceManager serviceManager;
    private ExtendedLinkType link;
    private NffgGraphType nffg;
    private NfvHelper nfvHelper;

    protected LinkReaderImpl(ExtendedLinkType link, NffgGraphType nffg, NfvHelper nfvHelper) {
        this.link = link;
        this.nfvHelper = nfvHelper;
    }

    @Override
    public String getName() {
        return link.getLinkName();
    }

    @Override
    public NodeReader getDestinationNode() {
        List<RestrictedNodeType> nodeList = nffg.getNodes().getNode();
        Optional<RestrictedNodeType> retrievedDestNode = nodeList.stream().filter(p -> p.getName().compareTo(link.getDestinationNode()) == 0).findFirst();

        if (retrievedDestNode.isPresent())
            return new NodeReaderImpl(retrievedDestNode.get(), nfvHelper);

        System.out.println("the node " + link.getDestinationNode() + " specified as destination of the link doesn't exist");
        return null;
    }

    @Override
    public int getLatency() {
        return link.getLatency().intValue();
    }

    @Override
    public NodeReader getSourceNode() {
//        nodePredicate = p -> p.getName() == link.getSourceNode();
//        RestrictedNodeType node = nffg.getNodes().getNode().stream().filter(nodePredicate).findFirst().get();
//        if (node == null) {
//            System.err.println("fatal error, the node specified into the node doesn't exist");
//            return null;
//        } else {
//            NodeReader nr = new NodeReaderImpl(node, serviceManager);
//            return nr;
//        }
        List<RestrictedNodeType> nodeList = nffg.getNodes().getNode();
        Optional<RestrictedNodeType> retrievedSourceNode = nodeList.stream().filter(p -> p.getName().compareTo(link.getSourceNode()) == 0).findFirst();

        if (retrievedSourceNode.isPresent())
            return new NodeReaderImpl(retrievedSourceNode.get(), nfvHelper);


        System.out.println("the node " + link.getSourceNode() + " specified as source of the link doesn't exist");
        return null;
    }

    @Override
    public float getThroughput() {
        return link.getThroughput();
    }

}
