package org.springbooz.datasource.r2dbc.mode.cluster;

import org.springbooz.datasource.r2dbc.config.ConnectionConfig;


public class ClusterModeConfig {

    private ConnectionConfig first;
    private ConnectionConfig second;
    private ConnectionConfig third;

    public ConnectionConfig getFirst() {
        return first;
    }

    public void setFirst(ConnectionConfig first) {
        this.first = first;
    }

    public ConnectionConfig getSecond() {
        return second;
    }

    public void setSecond(ConnectionConfig second) {
        this.second = second;
    }

    public ConnectionConfig getThird() {
        return third;
    }

    public void setThird(ConnectionConfig third) {
        this.third = third;
    }

}
