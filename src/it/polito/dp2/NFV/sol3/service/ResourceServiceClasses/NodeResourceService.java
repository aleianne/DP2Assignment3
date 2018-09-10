package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

import it.polito.dp2.NFV.lab3.AllocationException;
import it.polito.dp2.NFV.lab3.ServiceException;
import it.polito.dp2.NFV.sol3.service.DaoClasses.GraphDao;
import it.polito.dp2.NFV.sol3.service.DaoClasses.HostDao;
import it.polito.dp2.NFV.sol3.service.DaoClasses.VnfDao;
import it.polito.dp2.NFV.sol3.service.Exceptions.GraphNotFoundException;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.Node;
import it.polito.dp2.NFV.sol3.service.Neo4jSimpleXML.Nodes;
import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class NodeResourceService {

    private static Logger logger = Logger.getLogger(NodeResourceService.class.getName());

    public String addNode(String graphId, RestrictedNodeType newNode) throws ServiceException, AllocationException, InternalServerErrorException, GraphNotFoundException {

        /*
         * in this method use the same class used for the graph allocation
         * so crete a list with only one element and a list
         */

        HostDao hostDao = HostDao.getInstance();
        FunctionType virtualFunction = VnfDao.getInstance().readVnf(newNode.getName());

        if (virtualFunction == null)
            throw new AllocationException();

        // create the list of node and vnf used during annotation
        List<FunctionType> vnfList = new ArrayList<FunctionType>();
        List<RestrictedNodeType> nodeList = new ArrayList<RestrictedNodeType>();
        nodeList.add(newNode);

        GraphAllocator allocator = new GraphAllocator();

        if (vnfList.size() != nodeList.size()) {
            logger.log(Level.SEVERE, "nodeList and vnfList are not of the same size");
            throw new InternalServerErrorException("server encourred into an error");
        }

        // synchronize the access to the host DAO interface
        synchronized (hostDao) {
            List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType>(hostDao.readAllHosts());
            allocator.findSelectedHost(vnfList, nodeList, hostDao);

            if (!vnfList.isEmpty()) {
                allocator.findBestHost(vnfList, hostList);
            }

            // create a new graph in the database
            GraphDao.getInstance().updateGraph(graphId, newNode);

            allocator.allocateGraph(nodeList, hostDao);
        }

        return nodeList.get(0).getName();
    }


    public void getNode(String nodeId) {}

    public RestrictedNodeType searchNodeIntoGraph(String nffgId, String nodeId) throws NotFoundException {
        // get the nffg from the graphDao
        NffgGraphType retrievedNffg = GraphDao.getInstance().readGraph(nffgId);

        if (retrievedNffg == null)
            throw new NotFoundException();

        List<RestrictedNodeType> nodeList = retrievedNffg.getNodes().getNode();

        if (nodeList == null || nodeList.isEmpty())
            throw new NotFoundException();

        Optional<RestrictedNodeType> node = nodeList.stream().filter(p -> p.getName() == nodeId).findFirst();

        if (node.isPresent())
            return node.get();

        throw new NotFoundException();
    }

    public List<ExtendedHostType> getReachableHost(String nffgId) throws ServiceException {
        Neo4jServiceManager neo4jClient = new Neo4jServiceManager();
        Nodes receivedNodes = neo4jClient.getReachableHost(nffgId);

        if (receivedNodes == null || receivedNodes.getNode() == null)
            throw new NotFoundException();

        List<ExtendedHostType> hostList = new ArrayList<ExtendedHostType>();

        if (receivedNodes.getNode().isEmpty())
            return hostList;

        // elaborate the response of the neo4j service
        for (Node newNode : receivedNodes.getNode()) {
            ExtendedHostType newHost = HostDao.getInstance().readHost(newNode.getProperties().getProperty().get(0).getValue());
            hostList.add(newHost);
        }

        return hostList;
    }
}
