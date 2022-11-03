package org.springbooz.datasource.r2dbc.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.springbooz.datasource.r2dbc.BoozR2dbcRoutingConnectionFactory;
import org.springbooz.datasource.r2dbc.annotation.R2dbcRouterDatasourceModel;
import org.springbooz.datasource.r2dbc.domain.entity.BoozDataSourceRouterApi;
import org.springbooz.datasource.r2dbc.service.R2dbcRouterLoader;
import org.springbooz.webflux.holder.BoozContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder.r2dbcRouterBuider;

@Component
@RequiredArgsConstructor
public  class DatasourceRouteFilter implements WebFilter {
  private final BoozR2dbcRoutingConnectionFactory r2dbcRoutingConnectionFactory;
  private final BoozDataSourceRouterApi boozDataSourceRouterApi;

      public Mono<Object> useSessionCompanySchemaName(String companySchemaName) {
        return BoozContextHolder.getSession()
                .map(session -> {
                    Map<String, Object> sessionMap = session.getAttributes();
                    sessionMap.put("companySchemaName", companySchemaName);
                    return sessionMap;
                });
    }




  @Lazy
  @Autowired
  R2dbcRouterLoader routerLoader;
  private Mono<Void> handleProceeding(R2dbcRouterDatasourceModel datasource, Mono<Void> mono) {
    return r2dbcRoutingConnectionFactory.loadDataSource(
        mono,
        r2dbcRouterBuider()
            .database(datasource.getDatabaseName())
            .user(datasource.getUser()));

  }

  public R2dbcRouterDatasourceModel toDatasource(String str){
    JSONObject jsonObject = JSON.parseObject(str);
    return new R2dbcRouterDatasourceModel(jsonObject.getString("datasourceName"),null, jsonObject.getString("schemaName"));
  }


  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    if(exchange.getRequest().getHeaders().get("datasource")==null){
      return chain.filter(exchange);
    }
    return handleProceeding(toDatasource(exchange.getRequest().getHeaders().get("datasource").get(0)),chain.filter(exchange));
  }
}
