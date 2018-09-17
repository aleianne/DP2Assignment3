package it.polito.dp2.NFV.sol3.service.DaoClasses;

import it.polito.dp2.NFV.sol3.service.ResourceServiceClasses.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.DeployedNodeType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.ExtendedHostType;
import it.polito.dp2.NFV.sol3.service.ServiceXML.RestrictedNodeType;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.*;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

import it.polito.dp2.NFV.sol3.service.Exceptions.ServiceException;

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.InternalServerErrorException;

public class HostDao {

    private static final String hostLabelName = "host";
    private static ConcurrentMap<String, ExtendedHostType> hostMap = new ConcurrentHashMap<String, ExtendedHostType>();
    private static HostDao hostDao = new HostDao();

    public static HostDao getInstance() {
        return hostDao;
    }

    /*
     * insertion of data inside the database
     * insertion of data inside the local hashmap
     */
    public void createHosts(List<ExtendedHostType> hostList) throws ServiceException {
        Neo4jServiceManager neo4jXMLclient = Neo4jServiceManager.getInstance();

        Node neo4jNode = new Node();
        Property neo4jProperty = new Property();
        Labels hostLabel = new Labels();

        // declare a new property inside the neo4j node
        neo4jNode.setProperties(new Properties());
        neo4jNode.getProperties().getProperty().add(neo4jProperty);

        hostLabel.getLabel().add(hostLabelName);

        for (ExtendedHostType host : hostList) {
            hostMap.put(host.getHostname(), host);

            // set neo4j graph node property and forward it
            neo4jNode.getProperties().getProperty().get(0).setName("name");
            neo4jNode.getProperties().getProperty().get(0).setValue(host.getHostname());
            neo4jXMLclient.postNode(neo4jNode, hostLabel);
        }
    }

    /*
     * read the entire collection of data stored locally in the hashmap
     */
    public Collection<ExtendedHostType> readAllHosts() {
        return hostMap.values();
    }

    /*
     * read a single host starting from his hostname
     */
    public ExtendedHostType readHost(String hostname) {
        return hostMap.get(hostname);
    }

    /*
     * update total memory allocated field and total storage used field
     */
    public void updateHost(String hostname, FunctionType functionToBeDeployed) throws InternalServerErrorException {
        // interrogate the database in order to obtain the host to be update
        ExtendedHostType queryResultHost = hostMap.get(hostname);

        if (queryResultHost == null)
            throw new InternalServerErrorException();

        // update the storage and memory value inside the host
        int nodeMem = functionToBeDeployed.getRequiredMemory().intValue();
        int nodeStorage = functionToBeDeployed.getRequiredStorage().intValue();

        int availableStorage = queryResultHost.getStorageUsed().intValue();
        int availableMemory = queryResultHost.getMemoryUsed().intValue();
        int totalVNF = queryResultHost.getTotalVNFallocated().intValue();

        // update the host resource information
        totalVNF++;
        queryResultHost.setTotalVNFallocated(BigInteger.valueOf(totalVNF));
        queryResultHost.setMemoryUsed(BigInteger.valueOf(availableMemory + nodeMem));
        queryResultHost.setStorageUsed(BigInteger.valueOf(availableStorage + nodeStorage));
    }

    /*
        update the list of host deployed node
     */
    public void updateHost(String hostname, RestrictedNodeType nodeToBeDeployed) throws InternalServerErrorException {
        // query the database in order to obtain the host to be update
        ExtendedHostType queryResultHost = hostMap.get(hostname);

        if (queryResultHost == null)
            throw new InternalServerErrorException();

        ExtendedHostType.DeployedNodes hostDeployedNodes = queryResultHost.getDeployedNodes();
        DeployedNodeType node = new DeployedNodeType();
        node.setNodeName(nodeToBeDeployed.getName());

        if (hostDeployedNodes == null) {
            hostDeployedNodes = new ExtendedHostType.DeployedNodes();
            hostDeployedNodes.getNode().add(node);
            queryResultHost.setDeployedNodes(hostDeployedNodes);
        } else {
            hostDeployedNodes.getNode().add(node);
        }
    }

    /*public void updateHostDeployedNode(String hostName, String nodeId) {
        ExtendedHostType targetHost = hostMap.get(hostName);

        ExtendedHostType.DeployedNode newDeployedNode = new ExtendedHostType.DeployedNode();
        newDeployedNode.setNodeName(nodeId);

        targetHost.getDeployedNode().add(newDeployedNode);
    }*/

    public void deleteHost(ExtendedHostType host) {
    }

    /*
     * return a collection of hosts that satisfy the constraint of memory and storage
     */
    public Collection<ExtendedHostType> queryHost(int minStorage, int minMemory) {
        // apply the filter predicate to the hashmap
        Map<String, ExtendedHostType> hostSet = hostMap.entrySet()
                .stream()
                .filter(p -> p.getValue().getAvailableMemory().intValue() >= minMemory && p.getValue().getAvailableStorage().intValue() >= minStorage)
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        return hostSet.values();
    }
}
