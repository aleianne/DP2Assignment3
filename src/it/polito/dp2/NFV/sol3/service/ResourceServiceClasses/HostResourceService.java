package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.*;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import it.polito.dp2.NFV.lab3.ServiceException;

public class HostResourceService {

    private HostDao hostDao;

    public HostResourceService() {
        hostDao = HostDao.getInstance();
    }

    /*
        return all the hosts that are available into the NFV system
     */
    public List<ExtendedHostType> getAllAvailableHost() {
        return new ArrayList<ExtendedHostType>(hostDao.readAllHosts());
    }

    /*
        return all the nodes allocated into a specific host
     */
    public List<RestrictedNodeType> getAllocatedNodes(String hostname) throws InternalServerErrorException {
        List<RestrictedNodeType> nodeList = new ArrayList<RestrictedNodeType>();
        ExtendedHostType host = HostDao.getInstance().readHost(hostname);

        // if the database doesn't contain the host return null
        if (host == null || host.getDeployedNodes() == null || host.getDeployedNodes().getNode().isEmpty())
            return nodeList;

        for (DeployedNodeType deployed_node : host.getDeployedNodes().getNode()) {
            RestrictedNodeType node = GraphDao.getInstance().queryGraph(deployed_node.getNodeName());
            if (node == null)
                throw new InternalServerErrorException();

            nodeList.add(node);
        }

        return nodeList;
    }

    /*
     * return only the host that satisfy the latency and throughput constraint
     */
    public List<ExtendedHostType> queryAvailableHost(int minMemory, int minStorage) {
        return new ArrayList<ExtendedHostType>(hostDao.queryHost(minStorage, minMemory));
    }

    /*
     * search a single host element into the database
     */
    public ExtendedHostType getSingleHost(String hostname) {
        return hostDao.readHost(hostname);
    }

    /*
     * setup of the hosts available into NFV system
     */
    public void setHostsAtStartup(List<ExtendedHostType> hostList) throws ServiceException {
        if (hostList.isEmpty())
            throw new ServiceException("the list of host to be loaded in memory is empty");
        hostDao.createHosts(hostList);
    }
}
