package it.polito.dp2.NFV.sol3.client2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFV.ConnectionPerformanceReader;
import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NfvReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.client1.NfvDeployerServiceManager;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;


public class NfvReaderImpl implements NfvReader {

	private NfvDeployerServiceManager serviceManager;
	
	public NfvReaderImpl() {
		serviceManager = new NfvDeployerServiceManager();
	}
	
	@Override
	public ConnectionPerformanceReader getConnectionPerformance(HostReader host1, HostReader host2) {
		ConnectionPerformanceReader cpr = null;
		try {
			ConnectionType conn = serviceManager.getConnection(host1, host2);
			cpr = new ConnectionPerformanceReaderImpl(conn);
		} catch(ServiceException se) {
			System.err.println(se.getMessage());
		}
		return cpr;
	}

	@Override
	public HostReader getHost(String host1) {
		HostReader hr = null;
		try {
			ExtendedHostType host = serviceManager.getHost(host1);
			hr = new HostReaderImpl(host);
		} catch(ServiceException se) {
			System.err.println("impossible to create the HostReader instance");
			System.err.println(host1);
		}
		return hr;
	}

	@Override
	public Set<HostReader> getHosts() {
		Set<HostReader> hrSet = new HashSet<HostReader> ();
		try {
			ExtendedHostType host = serviceManager.getHosts();
			HostReader hr = new HostReaderImpl(host);
			hrSet.add(hr);
		} catch(ServiceException se) {
			System.err.println("impossible to create the HostReader interface set");
			System.err.println(se.getMessage());
		}
		return hrSet;
	}

	@Override
	public NffgReader getNffg(String nffgId) {
		NffgReader nfgr = null;
		try {
			nfgr = new NffgReaderImpl(nffgId, serviceManager);
		} catch(ServiceException se) {
			System.err.println("impossible to implement the nffg Reader interface");
			System.err.println(se.getMessage());
		}
		return nfgr;
	}

	@Override
	public Set<NffgReader> getNffgs(Calendar date) {
		Set<NffgReader> nrSet = new HashSet<NffgReader> ();
		
		try {
			NffgsInfoType nffgs = serviceManager.getGraphs(date);
			for(NffgsInfoType.NffgInfo info: nffgs.getNffgs().get) {
				NffgReader nfgr = new NffgReaderImpl(info.getNffgName());
				nrSet.add(nfgr);
			}
		} catch(ServiceException se) {
			System.err.println("impossible to implement the host Reader interface");
			System.err.println(se.getMessage());
		}
		
		return nrSet;
	}

	@Override
	public Set<VNFTypeReader> getVNFCatalog() {
		Set<VNFTypeReader> vnfSet = new HashSet<VNFTypeReader> ();
		
		try {
			CatalogType catalog = serviceManager.getCatalog();
			for(FunctionType function: catalog.getFunction()) {
				VNFTypeReader vnfReader = new VNFTypeReaderImpl(function);
				vnfSet.add(vnfReader);
			}
		} catch(ServiceException se) {
			System.err.println("impossible to retrieve the VNF catalog interface");
			System.err.println(se.getMessage());
		}
		
		return vnfSet;
	}

}
