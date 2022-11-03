//package org.boozsoft.rest;
//
//import org.springbooz.core.tool.result.R;
//import org.springbooz.security.oidc.client.holder.BoozOidcCommit;
//import org.springbooz.webflux.holder.context.security.domain.entity.BoozDatasource;
//import org.springbooz.webflux.holder.context.security.domain.entity.SystemDatasource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//@RestController
//@RequestMapping("/sys/datasource")
//public class DatasoureController {
//  @GetMapping("commitDatasource")
//  public Mono<R> commitDatasource(BoozDatasource datasource){
//    SystemDatasource systemDatasource=new SystemDatasource();
//    systemDatasource.setCurrentDatasource(datasource);
//    return BoozOidcCommit.commitSystemDatasource(systemDatasource).map(R::ok);
//  }
//  @Value("${spring.booz.r2dbc.name}")
//  public String databaseName;
//  @GetMapping("commitSchema")
//  public Mono<R> commitSchema(String schemaName){
//    SystemDatasource systemDatasource=new SystemDatasource();
//    BoozDatasource boozDatasource=new BoozDatasource();
//    boozDatasource.setDatabaseName(databaseName);
//    boozDatasource.setSchemaName(schemaName);
//    systemDatasource.setCurrentDatasource(boozDatasource);
//    return BoozOidcCommit.commitSystemDatasource(systemDatasource).map(R::ok);
//  }
//}
