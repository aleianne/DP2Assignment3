package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.DaoClasses.ConnectionDao;
import it.polito.dp2.NFV.sol3.service.DaoClasses.VnfDao;
import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import it.polito.dp2.NFV.ConnectionPerformanceReader;
import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NfvReader;
import it.polito.dp2.NFV.NfvReaderException;
import it.polito.dp2.NFV.NfvReaderFactory;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;

@Provider
public class NfvDeployer implements ApplicationEventListener {

    private static Logger logger = Logger.getLogger(NfvDeployer.class.getName());

    @Override
    public RequestEventListener onRequest(RequestEvent arg0) {
        return null;
    }

    @Override
    public void onEvent(ApplicationEvent applicationEvent) {
        // at the startup of the tomcat servlet call the intiNfvDeployer function
        switch (applicationEvent.getType()) {
            case INITIALIZATION_START: {
                initNfvDeployer();
            }
        }
    }

    public void initNfvDeployer() {
        try {
            logger.log(Level.SEVERE, "Nfv deployer Web-Service startup");

            // declare the object that are used to update the DB
            CatalogResourceService catalogServer = new CatalogResourceService();
            ConnectionResourceService connectionServer = new ConnectionResourceService();
            HostResourceService hostServer = new HostResourceService();
            NffgResourceService nffgServer = new NffgResourceService();

            ObjectFactory objFactory = ObjectFactoryManager.getObjectFactory();

            NfvReader monitor = NfvReaderFactory.newInstance().newNfvReader();
            List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType>();
            List<ConnectionType> connList = new ArrayList<ConnectionType>();
            List<FunctionType> functionList = new ArrayList<FunctionType>();

            logger.log(Level.INFO, "try to read the nfv info from the nfv reader interface");

            // read all the host from the interface
            for (HostReader hr : monitor.getHosts()) {
                ExtendedHostType newHost = objFactory.createExtendedHostType();

                // incapsulate the data from the reader interface into the ExtendedHostType instance
                newHost.setAvailableMemory(BigInteger.valueOf(hr.getAvailableMemory()));
                newHost.setAvailableStorage(BigInteger.valueOf(hr.getAvailableStorage()));
                newHost.setHostname(hr.getName());
                newHost.setMaxVNF(BigInteger.valueOf(hr.getMaxVNFs()));
                newHost.setMemoryUsed(BigInteger.valueOf(0));
                newHost.setStorageUsed(BigInteger.valueOf(0));
                newHost.setTotalVNFallocated(BigInteger.valueOf(0));
                hostList.add(newHost);
            }
            // add the list to the database
            hostServer.setHostsAtStartup(hostList);

            logger.log(Level.INFO, "all the hosts are loaded in memory");

            // deploy the network functions
            Set<VNFTypeReader> vnfCatalog = monitor.getVNFCatalog();
            for (VNFTypeReader function : vnfCatalog) {
                FunctionType newFunction = new FunctionType();

                // put the info inside the FunctionType function
                newFunction.setName(function.getName());
                newFunction.setType(FunctionEnumeration.fromValue(function.getFunctionalType().toString()));
                newFunction.setRequiredMemory(BigInteger.valueOf(function.getRequiredMemory()));
                newFunction.setRequiredStorage(BigInteger.valueOf(function.getRequiredStorage()));
                functionList.add(newFunction);
            }
            // put the data inside the the server
            catalogServer.setVnfsAtStartup(functionList);

            logger.log(Level.INFO, "all the virtual functions are loaded in memory");

            // instantiate a new connection performance reader interface
            ConnectionPerformanceReader connReader;
            for (HostReader outerHr : monitor.getHosts()) {
                for (HostReader innerHr : monitor.getHosts()) {
                    // extract the information from the interface
                    HostReader host1, host2;

                    host1 = outerHr;
                    host2 = innerHr;

                    connReader = monitor.getConnectionPerformance(host1, host2);

                    if (connReader != null) {
                        ConnectionType newConnection = new ConnectionType();
                        newConnection.setHostname1(host1.getName());
                        newConnection.setHostname2(host2.getName());
                        newConnection.setLatency(BigInteger.valueOf(connReader.getLatency()));
                        newConnection.setThroughput(Float.valueOf(connReader.getLatency()));
                        connList.add(newConnection);
                    }
                }
            }
            // add the connection list into memory
            connectionServer.setConnectionsAtStartup(connList);

            logger.log(Level.INFO, "all the connection between host are loaded in memory");

            // deploy the first graph
            NffgReader nfgr = monitor.getNffg("Nffg0");
            NffgGraphType newGraph = new NffgGraphType();

            newGraph.setNodes(new NffgGraphType.Nodes());
            newGraph.setLinks(new NffgGraphType.Links());

            List<RestrictedNodeType> nodeList = newGraph.getNodes().getNode();
            List<ExtendedLinkType> linkList = newGraph.getLinks().getLink();

            for (NodeReader nr : nfgr.getNodes()) {
                RestrictedNodeType newNode = new RestrictedNodeType();
                newNode.setHostname(nr.getHost().getName());
                newNode.setVNF(nr.getFuncType().getName());
                newNode.setName(nr.getName());
                nodeList.add(newNode);

                for (LinkReader lr : nr.getLinks()) {
                    ExtendedLinkType newLink = new ExtendedLinkType();

                    newLink.setDestinationNode(lr.getDestinationNode().getName());
                    newLink.setSourceNode(lr.getSourceNode().getName());
                    newLink.setLatency(BigInteger.valueOf(lr.getLatency()));
                    newLink.setThroughput(lr.getThroughput());
                    newLink.setLinkName(lr.getName());
                    newLink.setOverwrite(false);
                    linkList.add(newLink);
                }
            }

            nffgServer.deployNewNffgGraph(newGraph);
            logger.log(Level.INFO, "Nffg-0  is loaded");

        } catch (NfvReaderException ne) {
            logger.log(Level.SEVERE, "impossible to launch the nfv deployer web service: " + ne.getMessage());
        } catch (ServiceException se) {
            logger.log(Level.SEVERE, "Service Exception: " + se.getMessage());
        } catch (AllocationException ae) {
            logger.log(Level.SEVERE, "Impossible to allocate the first graph");
        }

    }

}
