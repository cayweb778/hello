package org.springbooz.datasource.r2dbc.mode.router;

import org.springbooz.datasource.r2dbc.config.ConnectionConfig;
import org.springbooz.datasource.r2dbc.mode.BaseModeConnectionFactory;
import org.springbooz.datasource.r2dbc.properties.BoozR2dbcProperties;

import java.util.HashMap;
import java.util.Map;

// 注意  RouterConnectionFactory extends BaseModeConnectionFactory
public class RouterConnectionFactory extends BaseModeConnectionFactory {
    BaseModeConnectionFactory b;

    public RouterConnectionFactory(Map<String, ConnectionConfig> configMap, String defaultDataBase) {
        super(configMap, defaultDataBase);
    }
//    public RouterConnectionFactory(BoozR2dbcProperties routerProperties) {
//        super(toMapDataBaseConfig(routerProperties), "default");
//    }
//
//    private static Map<String, ConnectionConfig> toMapDataBaseConfig(BoozR2dbcProperties routerProperties) {
//        Map<String, ConnectionConfig> configMap = new HashMap<>();
//        // 数据源信息
//        ConnectionConfig connectionConfig=routerProperties.getDatasource();
//        // 默认配置1个默认的
//        configMap.put("default",connectionConfig);
//        return configMap;
//    }

}
