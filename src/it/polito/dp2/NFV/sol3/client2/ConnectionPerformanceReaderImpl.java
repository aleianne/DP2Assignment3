package it.polito.dp2.NFV.sol3.client2;

import it.polito.dp2.NFV.ConnectionPerformanceReader;

import it.polito.dp2.NFV.sol3.service.ServiceXML.*;

public class ConnectionPerformanceReaderImpl implements ConnectionPerformanceReader {

    private ConnectionType conn;

    protected ConnectionPerformanceReaderImpl(ConnectionType conn) {
        this.conn = conn;
    }

    @Override
    public int getLatency() {
        return conn.getLatency().intValue();
    }

    @Override
    public float getThroughput() {

        System.out.println("this is the throughput " + conn.getThroughput());

        return conn.getThroughput();
    }

}
