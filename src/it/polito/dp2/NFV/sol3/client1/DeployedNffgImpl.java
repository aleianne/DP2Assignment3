package it.polito.dp2.NFV.sol3.client1;

import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.*;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class DeployedNffgImpl implements DeployedNffg {

    private NfvDeployerServiceManager serviceManager;
    private String nffgId;

    // the constructor receive the graph, a map that represent the association of nodeDescriptor and name assigned by the server
    // it receives also the reference of the NFV Deployer service
    protected DeployedNffgImpl(String nffgName, NfvDeployerServiceManager serviceManager) {
        this.serviceManager = serviceManager;
        this.nffgId = nffgName;
    }

    @Override
    public NodeReader addNode(VNFTypeReader type, String hostName) throws AllocationException, ServiceException {
        // create a new node
        RestrictedNodeType xmlNode = new RestrictedNodeType();
        xmlNode.setName("Node");
        xmlNode.setVNF(type.getName());
        xmlNode.setHostname(hostName);
        xmlNode.setNfFg(nffgId);

        // forward the node to the webserver
        RestrictedNodeType resNode = serviceManager.postNode(xmlNode, nffgId);

        // put the information into the nodeReader interface
        return new NodeReaderImpl(resNode, serviceManager);
    }

    @Override
    public LinkReader addLink(NodeReader source, NodeReader dest, boolean overwrite)
            throws NoNodeException, LinkAlreadyPresentException, ServiceException {

        // create a new link
        ExtendedLinkType xmlLink = new ExtendedLinkType();
        xmlLink.setLinkName("Link");
        xmlLink.setDestinationNode(dest.getName());
        xmlLink.setSourceNode(source.getName());
        xmlLink.setOverwrite(overwrite);

        // forward the link to the service
        LinkType link = (LinkType) serviceManager.postLink(xmlLink, nffgId);

        // create a new link reader interface  in order to read the link infos
        return new LinkReaderImpl(link, nffgId, serviceManager);
    }

    @Override
    public NffgReader getReader() throws ServiceException {
        try {
            NffgGraphType newGraph = serviceManager.getGraph(nffgId);
            return new NffgReaderImpl(newGraph, serviceManager);
        } catch (UnknownEntityException ue)  {
            System.out.println("impossible to find the nffg " + nffgId);
            return null;
        }
    }

}
