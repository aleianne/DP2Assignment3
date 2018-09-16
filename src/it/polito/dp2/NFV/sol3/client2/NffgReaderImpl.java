package it.polito.dp2.NFV.sol3.client2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.xml.datatype.DatatypeConfigurationException;

import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.DateConverter;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.util.Optional;

public class NffgReaderImpl implements NffgReader {

    private NffgGraphType nffg;
    private NfvHelper nfvHelper;

    protected NffgReaderImpl(NffgGraphType nffg, NfvHelper nfvHelper) {
        this.nffg = nffg;
        this.nfvHelper = nfvHelper;
    }

    @Override
    public String getName() {
        return nffg.getNffgName();
    }

    @Override
    public Calendar getDeployTime() {
        try {
            DateConverter dateConverter = new DateConverter();
            return dateConverter.fromXMLGregorianCalendar(nffg.getDeployDate());
        } catch (DatatypeConfigurationException dce) {
            return null;
        }
    }

    @Override
    public NodeReader getNode(String nodeName) {
        // filter the node list in order to get the node
        Optional<RestrictedNodeType> node = nffg.getNodes().getNode().stream().filter(p -> p.getName().compareTo(nodeName) == 0).findFirst();

        if (!node.isPresent()) {
            System.out.println("node " + nodeName + " doesn't exist");
            return null;
        }

        return new NodeReaderImpl(node.get(), nfvHelper);
    }

    @Override
    public Set<NodeReader> getNodes() {
        Set<NodeReader> nrSet = new HashSet<NodeReader>();
        for (RestrictedNodeType node : nffg.getNodes().getNode()) {
            NodeReader nr = new NodeReaderImpl(node, nfvHelper);
            nrSet.add(nr);
        }
        return nrSet;
    }

}
