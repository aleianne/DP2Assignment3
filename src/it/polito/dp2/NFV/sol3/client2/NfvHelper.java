package it.polito.dp2.NFV.sol3.client2;

import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.lab3.UnknownEntityException;
import it.polito.dp2.NFV.sol3.client2.NfvDeployerServiceManager;
import it.polito.dp2.NFV.sol3.ServiceXML.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class NfvHelper {

    private NfvDeployerServiceManager serviceManager;
    private List<NffgGraphType> deployedGraphList;
    private List<ConnectionType> availableConnectionList;
    private List<FunctionType> availableFunctionList;
    private List<HostType> availableHostList;
    private Map<String, List<RestrictedNodeType>> hostDeployedNodeMap;
    private Map<String, NffgGraphType> nffgMap;

    protected NfvHelper() {

        serviceManager = new NfvDeployerServiceManager();

        try {
            CatalogType catalog = serviceManager.getCatalog();

            HostsType availableHosts = serviceManager.getHosts();

            ConnectionsType availableConnections = serviceManager.getConnections();

            NffgsInfoType deployedNffgs = serviceManager.getGraphs(null);
            deployedGraphList = new ArrayList<NffgGraphType> ();
            nffgMap = new HashMap<String, NffgGraphType> ();

            for (NffgsInfoType.NffgInfo nffgInfo : deployedNffgs.getNffgInfo()) {
                NffgGraphType retrievedGraph = serviceManager.getGraph(nffgInfo.getNffgName());
                deployedGraphList.add(retrievedGraph);
                nffgMap.put(nffgInfo.getNffgName(), retrievedGraph);
            }

            availableFunctionList = catalog.getFunction();
            availableHostList = availableHosts.getHost();
            availableConnectionList = availableConnections.getConnection();

            hostDeployedNodeMap = new HashMap<String, List<RestrictedNodeType>>();

            for (HostType host : availableHostList) {
                NodesType hostDeployedNode = serviceManager.getHostNode(host.getHostname());
                hostDeployedNodeMap.put(host.getHostname(), hostDeployedNode.getNode());
            }

        } catch (ServiceException | UnknownEntityException se) {
            System.err.println(se.getMessage());
        }

    }

    protected List<ConnectionType> getConnectionList() {
        return availableConnectionList;
    }

    protected List<HostType> getHostList() {
        return availableHostList;
    }

    protected List<NffgGraphType> getGraphList() {
        return deployedGraphList;
    }

    protected List<FunctionType> getFunctionList() {
        return availableFunctionList;
    }

    protected List<RestrictedNodeType> getDeployedNode(String hostname) {
        return hostDeployedNodeMap.get(hostname);
    }

    protected NffgGraphType getNffgFromName(String name) {return nffgMap.get(name);}

}
