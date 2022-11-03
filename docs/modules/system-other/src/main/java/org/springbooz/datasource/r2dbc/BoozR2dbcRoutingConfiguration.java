package org.springbooz.datasource.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.ConnectionFactory;
import org.springbooz.datasource.r2dbc.helper.R2dbcHelper;
import org.springbooz.datasource.r2dbc.mode.router.builder.RouterConnectionFacoryBuilder;
import org.springbooz.datasource.r2dbc.properties.BoozR2dbcProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.Map;
import java.util.Objects;

/**
 * @author wujiuye 2020/11/03
 */
@EnableAutoConfiguration(exclude = {R2dbcRepositoriesAutoConfiguration.class})
@Configuration
public class BoozR2dbcRoutingConfiguration extends AbstractR2dbcConfiguration {

    @Resource
    private BoozR2dbcProperties r2dbcProps;
    @Autowired
    RouterConnectionFacoryBuilder routerConnectionFacoryBuilder;
//    @Autowired
//    R2dbcProperties r2dbcProperties;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        BoozR2dbcRoutingConnectionFactory boozR2dbcRoutingConnectionFactory = new BoozR2dbcRoutingConnectionFactory(createBaseModeConnectionFactory());
        R2dbcHelper.setR2dbcRoutingConnectionFactory(boozR2dbcRoutingConnectionFactory);
        return  boozR2dbcRoutingConnectionFactory;
    }

    @Bean
    public ReactiveTransactionManager reactiveTransactionManager(@Autowired ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }



    private ConnectionFactory createBaseModeConnectionFactory() {
        String mode = r2dbcProps.getMode();
        if (mode != null && Objects.equals(mode, "router")) {
            return routerConnectionFacoryBuilder.getPostgreConnectionFacotry(r2dbcProps);
        }
        throw new NullPointerException("配置了@EnableBoozRdbcRouter，但配置信息错误,请检查！");
    }

}
