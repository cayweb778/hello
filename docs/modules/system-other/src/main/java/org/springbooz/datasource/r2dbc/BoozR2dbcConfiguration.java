package org.springbooz.datasource.r2dbc;

import org.springbooz.datasource.r2dbc.mode.BaseModeConnectionFactory;
import org.springbooz.datasource.r2dbc.mode.cluster.ClusterConnectionFactory;
import org.springbooz.datasource.r2dbc.mode.cluster.ClusterModeConfig;
import org.springbooz.datasource.r2dbc.mode.ms.MasterSlaveConnectionFactory;
import org.springbooz.datasource.r2dbc.mode.ms.MasterSlaveModeConfig;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

import javax.annotation.Resource;

/**
 * @author wujiuye 2020/11/03
 */
@EnableAutoConfiguration(exclude = {R2dbcRepositoriesAutoConfiguration.class})
public class BoozR2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Resource
    private BoozR2dbcDataBaseConfig r2dbcDataBaseConfig;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        return new BoozR2dbcBaseConnectionFactory(null);
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(@Autowired ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }

    private BaseModeConnectionFactory createBaseModeConnectionFactory() {
        if (this.r2dbcDataBaseConfig.getMasterSlaveMode() != null) {
            MasterSlaveModeConfig masterSlaveMode = this.r2dbcDataBaseConfig.getMasterSlaveMode();
            return new MasterSlaveConnectionFactory(masterSlaveMode);
        }
        if (this.r2dbcDataBaseConfig.getClusterMode() != null) {
            ClusterModeConfig clusterMode = this.r2dbcDataBaseConfig.getClusterMode();
            return new ClusterConnectionFactory(clusterMode);
        }
        throw new NullPointerException("请选择一种模式配置数据源！");
    }

}
