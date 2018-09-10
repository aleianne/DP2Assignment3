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

public class NffgReaderImpl implements NffgReader {

    private NfvDeployerServiceManager serviceManager;
    private NffgGraphType nffg;
    private String nffgId;

    public NffgReaderImpl(String nffgId, NfvDeployerServiceManager serviceManager) {
        this.nffgId = nffgId;
        try {
            this.nffg = serviceManager.getGraph(nffgId);
        } catch (ServiceException se) {
            System.err.println("impossible to implement the nffg reader interface");
            System.err.println(se.getMessage());
        }
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
        Predicate<RestrictedNodeType> nffgPredicate = p -> p.getName() == nodeName;
        RestrictedNodeType node = nffg.getNodes().getNode().stream().filter(nffgPredicate).findFirst().get();

        if (node == null) {
            return null;
        } else {
            return new NodeReaderImpl(node, serviceManager);
        }
    }

    @Override
    public Set<NodeReader> getNodes() {
        Set<NodeReader> nrSet = new HashSet<NodeReader>();
        for (RestrictedNodeType node : nffg.getNodes().getNode()) {
            NodeReader nr = new NodeReaderImpl(node, serviceManager);
            nrSet.add(nr);
        }
        return nrSet;
    }

}
