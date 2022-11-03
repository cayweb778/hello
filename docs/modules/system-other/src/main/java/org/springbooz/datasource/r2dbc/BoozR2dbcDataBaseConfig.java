package org.springbooz.datasource.r2dbc;

import org.springbooz.datasource.r2dbc.mode.cluster.ClusterModeConfig;
import org.springbooz.datasource.r2dbc.mode.ms.MasterSlaveModeConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "hotkit.r2dbc")
public class BoozR2dbcDataBaseConfig {


    private MasterSlaveModeConfig masterSlaveMode;

    private ClusterModeConfig clusterMode;

    public ClusterModeConfig getClusterMode() {
        return clusterMode;
    }

    public void setMasterSlaveMode(MasterSlaveModeConfig masterSlaveMode) {
        this.masterSlaveMode = masterSlaveMode;
    }

    public void setClusterMode(ClusterModeConfig clusterMode) {
        this.clusterMode = clusterMode;
    }

    public MasterSlaveModeConfig getMasterSlaveMode() {
        return masterSlaveMode;
    }

}
