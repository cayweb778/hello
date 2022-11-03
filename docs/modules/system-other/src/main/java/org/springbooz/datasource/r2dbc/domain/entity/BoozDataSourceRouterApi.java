package org.springbooz.datasource.r2dbc.domain.entity;

import org.springbooz.webflux.holder.context.security.domain.entity.SystemDatasource;
import reactor.core.publisher.Mono;

public interface BoozDataSourceRouterApi {


  Mono<SystemDatasource> getSystemDatabase();
}
