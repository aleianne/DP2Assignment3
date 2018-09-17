package it.polito.dp2.NFV.sol3.client2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;

import it.polito.dp2.NFV.sol3.ServiceXML.*;
import java.util.Optional;

public class NodeReaderImpl implements NodeReader {

    //private NfvDeployerServiceManager serviceManager;
    private RestrictedNodeType node;
    private NfvHelper nfvHelper;
    private NffgGraphType nffg;

    public NodeReaderImpl(RestrictedNodeType node, NfvHelper nfvHelper) {
        this.node = node;
        this.nfvHelper = nfvHelper;
        this.nffg = nfvHelper.getNffgFromName(node.getNfFg());
    }

    @Override
    public String getName() {
        return node.getName();
    }

    @Override
    public VNFTypeReader getFuncType() {

        List<FunctionType> functionList = nfvHelper.getFunctionList();
        Optional<FunctionType> function = functionList.stream().filter(p -> p.getName().compareTo(node.getName()) == 0).findFirst();

        if (function.isPresent())
            return new VNFTypeReaderImpl(function.get());

        System.out.println("the function " + node.getName() + " used by the node doesn't exist");
        return null;

//        VNFTypeReader vnf = null;
//        try {
//            FunctionType function = serviceMana ger.getFunction(node.getVNF());
//            vnf = new VNFTypeReaderImpl(function);
//        } catch (ServiceException se) {
//            System.err.println("impossible to implement a VNF reader interface");
//            System.err.println(se.getMessage());
//        }
    }

    @Override
    public HostReader getHost() {
        List<HostType> hostList = nfvHelper.getHostList();
        Optional<HostType> host = hostList.stream().filter(p -> p.getHostname() == node.getHostname()).findFirst();

        if (host.isPresent())
            return new HostReaderImpl(host.get(), nfvHelper);

        System.out.println("the host " + node.getHostname() + "specified into the node doesn't exist");
        return null;
    }

    @Override
    public Set<LinkReader> getLinks() {
        Set<LinkReader> lrSet = new HashSet<LinkReader>();
        for (LinkType link : nffg.getLinks().getLink()) {
            LinkReader lr = new LinkReaderImpl(link, nffg, nfvHelper);
            lrSet.add(lr);
        }
        return lrSet;
    }

    @Override
    public NffgReader getNffg() {
        return new NffgReaderImpl(nffg, nfvHelper);
    }

}
