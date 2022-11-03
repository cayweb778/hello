package org.springbooz.datasource.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springbooz.datasource.r2dbc.helper.R2dbcHelper;
import org.springbooz.datasource.r2dbc.mode.router.RouterConnectionFactory;
import org.springbooz.datasource.r2dbc.mode.router.builder.RouterConnectionFacoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder.r2dbcRouterBuider;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2020/12/30 1:34 下午
 */
public class BoozR2dbcRoutingConnectionFactory extends AbstractRoutingConnectionFactory {

  private final static String DB_KEY = "HOTKIT-R2DBC-DB";
  public Map<String, ConnectionFactory> connectionFactoryMap;
  public RouterConnectionFactory routerConnectionFactory;
  @Autowired
  RouterConnectionFacoryBuilder routerConnectionFacoryBuilder;

  public BoozR2dbcRoutingConnectionFactory(ConnectionFactory postgresqlConnectionFactory) {
    this.routerConnectionFactory = routerConnectionFactory;
    connectionFactoryMap = new HashMap<>();
    connectionFactoryMap.put("defalut", postgresqlConnectionFactory);
    setTargetConnectionFactories(connectionFactoryMap);
    setDefaultTargetConnectionFactory(connectionFactoryMap.get("defalut"));
  }

  public <T> T  loadDataSource( CorePublisher corePublisher, R2dbcRouterBuilder builder) {
    useLazyDataSource(builder);
    Class<T> clz=(Class<T>) corePublisher.getClass();
    return determineDatasourceKey(clz, corePublisher, builder.getIdentity());
  }

  public void useLazyDataSource(R2dbcRouterBuilder builder) {
    String identity = builder.getIdentity();
    if (connectionFactoryMap.get(identity) == null) {
      ConnectionFactory connectionFactory = routerConnectionFacoryBuilder.build(builder);
      connectionFactoryMap.put(identity, connectionFactory);
      // 使connectionFactoryMap生效
      afterPropertiesSet();
    }
  }

  public static <T> T determineDatasourceKey(Class<T> returnType, CorePublisher corePublisher, String dataSource) {

    if (Mono.class.isAssignableFrom(returnType)) {
      T a= (T) ((Mono<Object>) corePublisher).subscriberContext(context -> context.put(DB_KEY, dataSource));
      return a;
    }
    if (Flux.class.isAssignableFrom(returnType)) {
      T b=(T) ((Flux<Object>) corePublisher).subscriberContext(context -> context.put(DB_KEY, dataSource));
      return b;
    }
    throw new RuntimeException("错误返回类型");
  }
//    public <T>T loadDataSource(T t, String databaseName, String schemaName){
//        String iden=schemaName==null?databaseName:databaseName+"-"+schemaName;
//       handleLoadDataSource(databaseName,schemaName);
//        return putDataSource(t,iden);
//    }
//    public <T>T loadDataSource(T t, String databaseName){
//        return this.loadDataSource(t,databaseName,null);
//    }



  @Override
  protected Mono<Object> determineCurrentLookupKey() {
    return Mono.subscriberContext().handle((context, sink) -> {
      if (context.hasKey(DB_KEY)) {
        sink.next(context.get(DB_KEY));
      }
    });
  }
}
