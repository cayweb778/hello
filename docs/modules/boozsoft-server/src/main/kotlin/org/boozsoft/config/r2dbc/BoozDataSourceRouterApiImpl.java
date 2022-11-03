package org.boozsoft.config.r2dbc;

import com.alibaba.fastjson.JSON;
import org.springbooz.datasource.r2dbc.domain.entity.BoozDataSourceRouterApi;
import org.springbooz.webflux.holder.BoozContextHolder;
import org.springbooz.webflux.holder.context.security.domain.entity.BoozDatasource;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemDatasource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Service
public class BoozDataSourceRouterApiImpl implements BoozDataSourceRouterApi {
    @Override
    public Mono<SystemDatasource> getSystemDatabase() {
        return BoozContextHolder.getExchange().map(item -> {
            HttpHeaders headers = item.getRequest().getHeaders();
            String datasourceStr = headers.get("datasource").get(0);
            HashMap map = JSON.parseObject(datasourceStr, HashMap.class);
            SystemDatasource systemDatasource = new SystemDatasource();
            BoozDatasource boozDatasource = new BoozDatasource();
            boozDatasource.setSchemaName((String) map.get("schemaName"));
            systemDatasource.setCurrentDatasource(boozDatasource);
            return systemDatasource;
        });
    }
}
