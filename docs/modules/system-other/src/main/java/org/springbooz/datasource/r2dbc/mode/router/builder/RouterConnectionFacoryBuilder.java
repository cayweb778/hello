package org.springbooz.datasource.r2dbc.mode.router.builder;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.mode.router.RouterConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springbooz.datasource.r2dbc.properties.BoozR2dbcProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

@Configuration
public class RouterConnectionFacoryBuilder {
    @Autowired
    BoozR2dbcProperties r2dbcProps;

//    public ConnectionConfig handleConnectionConfigBuild(String databaseName, String schemaName) {
//        ConnectionConfig connectionConfig = r2dbcProperties.get.getDatasource();
//        if (schemaName != null) {
//            String url = connectionConfig.getUrl();
//            url.split("/?/");
//
//            connectionConfig.setUrl(new R2dbcUrlUtil(connectionConfig.getUrl())
//                    .setDatabaseName(databaseName)
//                    .setSchemaName(schemaName)
//                    .make());
//        } else {
//            connectionConfig.setUrl(new R2dbcUrlUtil(connectionConfig.getUrl())
//                    .setDatabaseName(databaseName)
//                    .make());
//        }
//        return connectionConfig;
//    }

    public ConnectionFactory getPostgreConnectionFacotry(BoozR2dbcProperties r2dbcProps) {
        PostgresqlConnectionConfiguration.Builder builder = PostgresqlConnectionConfiguration.builder()
                .host(r2dbcProps.getUrl());
        if (!(r2dbcProps.getPort() == null ||Objects.equals("",r2dbcProps.getPort()))) {
            builder.port(Integer.parseInt(r2dbcProps.getPort()));
        }
        PostgresqlConnectionConfiguration build = builder.database(r2dbcProps.getName())
                .username(r2dbcProps.getUsername())
                .password(r2dbcProps.getPassword())
                .options(r2dbcProps.getProperties())
                .build();
        PostgresqlConnectionFactory connectionFactory=new PostgresqlConnectionFactory(build);
        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
//                .maxIdleTime(Duration.ofMillis(1000 * 1000))
//                .maxSize(10)
//                .validationQuery("SELECT 1")
//                .initialSize(5)
                .build();
        return new ConnectionPool(configuration);
    }

    public ConnectionFactory create(R2dbcRouterBuilder routerBuilder) {
        // 原始R2dbc参数拷贝新参数对象

        BoozR2dbcProperties newR2dbcProps=r2dbcProps.copy();
        // 修改host
        if (Objects.equals(routerBuilder.getHost(),"empty") || routerBuilder.getHost() != null) {
            newR2dbcProps.setUrl(routerBuilder.getHost());
        }

        // 修改user
        if (Objects.equals(routerBuilder.getUser(),"empty") || routerBuilder.getUser() != null) {
            newR2dbcProps.setUsername(routerBuilder.getUser());
        }
        // 修改host
        if (Objects.equals(routerBuilder.getPort(),"empty") || routerBuilder.getPort() != null) {
            newR2dbcProps.setPort(routerBuilder.getPort());
        }

        // 修改数据库名
        if (Objects.equals(routerBuilder.getDatabaseName(),"empty") || routerBuilder.getDatabaseName() != null) {
            newR2dbcProps.setName(routerBuilder.getDatabaseName());
        }

        // 修改数据库架构名
        if (Objects.equals(routerBuilder.getSchemaName(),"empty") || routerBuilder.getSchemaName() != null) {
            Map properties= CollectOfUtils.mapof("search_path", routerBuilder.getSchemaName());
            newR2dbcProps.setProperites(properties);
        }

        // 创建工厂
        return getPostgreConnectionFacotry(newR2dbcProps);
    }

    public ConnectionFactory build(R2dbcRouterBuilder routerBuilder) {
        return create(routerBuilder);
    }


}

