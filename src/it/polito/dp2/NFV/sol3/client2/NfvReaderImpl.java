package it.polito.dp2.NFV.sol3.client2;

import java.util.*;

import it.polito.dp2.NFV.ConnectionPerformanceReader;
import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NfvReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.sol3.client2.NfvDeployerServiceManager;

import it.polito.dp2.NFV.sol3.ServiceXML.ConnectionType;
import it.polito.dp2.NFV.sol3.ServiceXML.HostType;
import it.polito.dp2.NFV.sol3.ServiceXML.FunctionType;
import it.polito.dp2.NFV.sol3.ServiceXML.NffgGraphType;



import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.XMLGregorianCalendar;

import java.util.Optional;


public class NfvReaderImpl implements NfvReader {

    private NfvHelper nfvHelper;

    public NfvReaderImpl(NfvHelper nfvHelper) {
        this.nfvHelper = nfvHelper;
    }

    @Override
    public ConnectionPerformanceReader getConnectionPerformance(HostReader host1, HostReader host2) {

        List<ConnectionType> connectionList = nfvHelper.getConnectionList();
        Optional<ConnectionType> retrievedConnection = connectionList.stream().filter(p -> p.getHostname1().compareTo(host1.getName()) == 0 && p.getHostname2().compareTo(host2.getName()) == 0).findFirst();

        if (retrievedConnection.isPresent())
            return new ConnectionPerformanceReaderImpl(retrievedConnection.get());

        System.out.println("the connection between " + host1.getName() + " and " + host2.getName() + " doesn't exist");
        return null;
    }

    @Override
    public HostReader getHost(String host1) {

        List<HostType> hostList = nfvHelper.getHostList();
        Optional<HostType> retrievedHost = hostList.stream().filter(p -> p.getHostname().compareTo(host1) == 0).findFirst();

        if (retrievedHost.isPresent())
            return new HostReaderImpl(retrievedHost.get(), nfvHelper);

        System.out.println("the host " + host1 + " doesn't exist");
        return null;
    }

    @Override
    public Set<HostReader> getHosts() {
        Set<HostReader> hrSet = new HashSet<>();
        List<HostType> hostList = nfvHelper.getHostList();
        HostReader hr;

        for (HostType host: hostList) {
            hr = new HostReaderImpl(host, nfvHelper);
            hrSet.add(hr);
        }

        return hrSet;
    }

    @Override
    public NffgReader getNffg(String nffgId) {

        List<NffgGraphType> nffgList = nfvHelper.getGraphList();
        Optional<NffgGraphType> retrievedNffg = nffgList.stream().filter(p -> p.getNffgName().compareTo(nffgId) == 0).findFirst();

        if (((Optional) retrievedNffg).isPresent())
            return new NffgReaderImpl(retrievedNffg.get(), nfvHelper);

        System.out.println("the nffg " + nffgId + " doesn't exist");
        return null;
    }

    @Override
    public Set<NffgReader> getNffgs(Calendar date) {
        Set<NffgReader> nrSet = new HashSet<>();
        NffgReader nr;

        if (date == null) {
            for (NffgGraphType graph : nfvHelper.getGraphList()) {
                nr = new NffgReaderImpl(graph, nfvHelper);
                nrSet.add(nr);
            }
            return nrSet;
        }

        try {
            XMLGregorianCalendar xmlDate = CalendarXMLconverter.toXMLGregorianCalendar(date);
            for (NffgGraphType graph : nfvHelper.getGraphList()) {
                int res = graph.getDeployDate().compare(xmlDate);
                if (res == DatatypeConstants.GREATER || res == DatatypeConstants.EQUAL) {
                    nr = new NffgReaderImpl(graph, nfvHelper);
                    nrSet.add(nr);
                }
            }
        } catch (DatatypeConfigurationException dte) {
            System.out.println("impossible to convert the date passed as parameter");
            return null;
        }

        return nrSet;
    }

    @Override
    public Set<VNFTypeReader> getVNFCatalog() {
        Set<VNFTypeReader> vnfSet = new HashSet<>();
        VNFTypeReader vnf;

        for (FunctionType function : nfvHelper.getFunctionList()) {
            vnf = new VNFTypeReaderImpl(function);
            vnfSet.add(vnf);
        }

        return vnfSet;
    }
}
