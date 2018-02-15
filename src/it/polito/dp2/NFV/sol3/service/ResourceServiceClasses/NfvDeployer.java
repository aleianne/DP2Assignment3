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
public class NfvDeployer implements ApplicationEventListener{
	
	private static Logger logger = Logger.getLogger(NfvDeployer.class.getName());

	@Override
	public RequestEventListener onRequest(RequestEvent arg0) {
		return null;
	}

	@Override
	public void onEvent(ApplicationEvent applicationEvent) {
	
		switch(applicationEvent.getType()) {
			case INITIALIZATION_START: {
				// initialize the nfv deployer
				initNfvDeployer();
			}
		}
	}
	
	public void initNfvDeployer(){

		try {
			
			logger.log(Level.INFO, "Nfv deployer Web-Service startup");
			
			// declare the object that are used to update the DB
			CatalogResourceService catalogServer = new CatalogResourceService();
			ConnectionResourceService connectionServer = new ConnectionResourceService();
			HostResourceService hostServer = new HostResourceService();
			NffgResourceService nffgServer = new NffgResourceService();
			
			ObjectFactory objFactory = ObjectFactoryManager.getObjectFactory();
			
			NfvReader monitor = NfvReaderFactory.newInstance().newNfvReader();
			List<ExtendedExtendedHostType> hostList = new ArrayList<ExtendedHostType>();
			List<ConnectionType> connList = new ArrayList<ConnectionType>();
			List<FunctionType> functionList = new ArrayList<FunctionType>();
			
			logger.log(Level.INFO, "try to read the nfv info from the nfv reader interface");
			
			// first: read all the host from the interface
			for(HostReader hr: monitor.getHosts()) {
				ExtendedHostType newHost = objFactory.createExtendedHostType();
				
				// incapsulate the data from the reader interface into the ExtendedHostType instance
				newHost.setAvailableMemory(BigInteger.valueOf(hr.getAvailableMemory()));
				newHost.setAvailableStorage(BigInteger.valueOf(hr.getAvailableStorage()));
				newHost.setHostname(hr.getName());
				newHost.setMaxVNF(BigInteger.valueOf(hr.getMaxVNFs()));
				newHost.setTotalVNFallocated(BigInteger.valueOf(0));
				
				hostList.add(newHost);
			}
			// add the list to the database
			hostServer.setHostsAtStartup(hostList);
			
			logger.log(Level.INFO, "all the host are loaded in memory");
			
			// deploy the network functions
			Set<VNFTypeReader> vnfCatalog = monitor.getVNFCatalog();
			for(VNFTypeReader function: vnfCatalog) {
				FunctionType newFunction = new FunctionType();
				
				// put the info inside the FunctionType function
				newFunction.setName(function.getName());
				newFunction.setType(FunctionEnumeration.fromValue(function.getFunctionalType().toString()));
				newFunction.setRequiredMemory(BigInteger.valueOf(function.getRequiredMemory()));
				newFunction.setRequiredStorage(BigInteger.valueOf(function.getRequiredStorage()));
				
				// add the function to the catalog
				functionList.add(newFunction);
			}
			// put the data inside the the server
			catalogServer.setVnfsAtStartup(functionList);
			
			logger.log(Level.INFO, "all the virtual functions are loaded in memory");
			
			// instantiate a new connection performance reader interface
			ConnectionPerformanceReader connReader;
			for(HostReader outerHr: monitor.getHosts()) {
				for(HostReader innerHr: monitor.getHosts()) {
					// extract the information from the interface
					HostReader host1, host2;
					
					host1 = outerHr;
					host2 = innerHr;
					
					connReader = monitor.getConnectionPerformance(host1, host2);
					if(connReader != null) {
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
			NffgType newGraph = new NffgType();
			
			List<NodeType> nodeList = newGraph.getNode();
			List<LinkType> linkList;
			
			for(NodeReader nr: nfgr.getNodes()) {
				NodeType newNode = new NodeType();
				newNode.setHostname(nr.getHost().getName());
				newNode.setVNF(nr.getFuncType().getName());
				newNode.setName(nr.getName());
				
				nodeList.add(newNode);
				linkList = newNode.getLink();
				
				for(LinkReader lr: nr.getLinks()) {
					LinkType newLink = new LinkType();
					
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
			logger.log(Level.INFO, "the first graph is loaded");
			
		} catch(NfvReaderException ne) {
			logger.log(Level.SEVERE, "impossible to lauch the nfv deployer web service: " + ne.getMessage());
		} catch(ServiceException se) {
			logger.log(Level.SEVERE, "Service Exception: " + se.getMessage());
		} catch(AllocationException ae) {
			logger.log(Level.SEVERE, "Impossible to allocate the first graph");
		}
		
	}

}
