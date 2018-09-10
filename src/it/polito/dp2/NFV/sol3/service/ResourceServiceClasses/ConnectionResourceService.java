package it.polito.dp2.NFV.sol3.service.ResourceServiceClasses;

import java.util.ArrayList;
import java.util.List;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;
import it.polito.dp2.NFV.sol3.service.DaoClasses.ConnectionDao;

public class ConnectionResourceService {

    public ConnectionResourceService() {

    }

    // insert the connection info into the database at the server startup
    public void setConnectionsAtStartup(List<ConnectionType> connList) {
        for (ConnectionType connection : connList) {
            ConnectionDao.getInstance().createConnection(connection);
        }
    }

    // get all the connection stored into the database
    public List<ConnectionType> getConnections() {
        List<ConnectionType> connList = new ArrayList<ConnectionType>(ConnectionDao.getInstance().readAllConnections());
        return connList;
    }

    // get a single connection
    public ConnectionType getConnection(String host1, String host2) {
        return ConnectionDao.getInstance().readConnection(host1, host2);
    }
}
