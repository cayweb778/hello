package org.springbooz.datasource.r2dbc.mode.cluster;

import org.springbooz.datasource.r2dbc.config.ConnectionConfig;
import org.springbooz.datasource.r2dbc.mode.BaseModeConnectionFactory;

import java.util.HashMap;
import java.util.Map;

public class ClusterConnectionFactory extends BaseModeConnectionFactory {

    public ClusterConnectionFactory(ClusterModeConfig clusterMode) {
        super(toMapDataBaseConfig(clusterMode), ClusterMode.First);
    }

    private static Map<String, ConnectionConfig> toMapDataBaseConfig(ClusterModeConfig clusterMode) {
        Map<String, ConnectionConfig> configMap = new HashMap<>();
        if (clusterMode.getFirst() != null) {
            configMap.put(ClusterMode.First, clusterMode.getFirst());
        }
        if (clusterMode.getSecond() != null) {
            configMap.put(ClusterMode.Second, clusterMode.getSecond());
        }
        if (clusterMode.getThird() != null) {
            configMap.put(ClusterMode.Third, clusterMode.getThird());
        }
        if (configMap.isEmpty()) {
            throw new NullPointerException("");
        }
        return configMap;
    }

}
