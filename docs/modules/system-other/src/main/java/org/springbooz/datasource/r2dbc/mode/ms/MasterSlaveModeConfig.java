package org.springbooz.datasource.r2dbc.mode.ms;

import org.springbooz.datasource.r2dbc.config.ConnectionConfig;


public class MasterSlaveModeConfig {

    private ConnectionConfig master;
    private ConnectionConfig slave;

    public ConnectionConfig getMaster() {
        return master;
    }

    public ConnectionConfig getSlave() {
        return slave;
    }

    public void setMaster(ConnectionConfig master) {
        this.master = master;
    }

    public void setSlave(ConnectionConfig slave) {
        this.slave = slave;
    }

}
