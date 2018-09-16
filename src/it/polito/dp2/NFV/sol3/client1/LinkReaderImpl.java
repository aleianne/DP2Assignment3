package it.polito.dp2.NFV.sol3.client1;

import java.math.BigInteger;
import java.util.function.Predicate;

import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.ServiceXML.LinkType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.NffgGraphType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.RestrictedNodeType;

import javax.ws.rs.NotFoundException;

public class LinkReaderImpl implements LinkReader {

    private LinkType link;
    private String nffgId;
    private Predicate<RestrictedNodeType> linkPredicate;
    private NfvDeployerServiceManager serviceManager;

    protected LinkReaderImpl(LinkType link, String nffgId, NfvDeployerServiceManager serviceManager) {
        this.link = link;
        this.serviceManager = serviceManager;
        this.nffgId = nffgId;
    }

    @Override
    public String getName() {
        return link.getLinkName();
    }

    @Override
    public NodeReader getDestinationNode() {

        try {
            RestrictedNodeType retrievedNode = serviceManager.getNodeIntoGraph(nffgId, link.getDestinationNode());
            System.out.println("the destination node is " + retrievedNode.getName());
            return new NodeReaderImpl(retrievedNode, serviceManager);
        } catch (ServiceException se) {
            System.err.println(se.getMessage());
            return null;
        } catch (NotFoundException nfe) {
            System.err.println();
            return null;
        }
//        // filter the list
//        linkPredicate = p -> p.getName() == link.getDestinationNode();
//        RestrictedNodeType destNode = nffg.getNodes().getNode().stream().filter(linkPredicate).findFirst().get();
//        return new NodeReaderImpl(destNode, serviceManager);
    }

    @Override
    public int getLatency() {

        if (link.getLatency() == null)
            return 0;

        return link.getLatency().intValue();
    }

    @Override
    public NodeReader getSourceNode() {

        try {
            RestrictedNodeType retrievedNode = serviceManager.getNodeIntoGraph(nffgId, link.getSourceNode());
            System.out.println("the source node is: " + retrievedNode.getName());
            return new NodeReaderImpl(retrievedNode, serviceManager);
        } catch (ServiceException se) {
             System.err.println(se.getMessage());
             return null;
        } catch (NotFoundException nfe) {
            System.err.println("impossible to find node " + link.getSourceNode());
            return null;
        }


//        // filter the list
//        linkPredicate = p -> p.getName() == link.getSourceNode();
//        RestrictedNodeType srcNode = nffg.getNodes().getNode().stream().filter(linkPredicate).findFirst().get();
//        return new NodeReaderImpl(srcNode, serviceManager);
    }

    @Override
    public float getThroughput() {

        if (link.getThroughput() == null)
            return 0;

        return link.getThroughput();
    }

}
