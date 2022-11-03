package org.springbooz.datasource.r2dbc.helper;

import org.springbooz.datasource.r2dbc.BoozR2dbcRoutingConnectionFactory;
import org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder;
import org.springframework.context.annotation.Configuration;
import reactor.core.CorePublisher;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

import static org.springbooz.datasource.r2dbc.domain.vo.R2dbcRouterBuilder.r2dbcRouterBuider;

@Configuration
public class R2dbcHelper {
  private static BoozR2dbcRoutingConnectionFactory r2dbcRoutingConnectionFactory;

  public static <T> T r2dbcRouter(String database, String schema, Supplier<T> supplier) {
    Object o = supplier.get();
    if (!(o instanceof CorePublisher)) {
      throw new RuntimeException("错误,r2dbcRouter必须返回Mono或Flux");
    }
    Class<T> clz = (Class<T>) o.getClass();
    return r2dbcRoutingConnectionFactory.loadDataSource((CorePublisher) o, r2dbcRouterBuider().database(database).schema(schema));
  }

  public static void setR2dbcRoutingConnectionFactory(BoozR2dbcRoutingConnectionFactory r2dbcRoutingConnectionFactory) {
    R2dbcHelper.r2dbcRoutingConnectionFactory = r2dbcRoutingConnectionFactory;
  }

  public static A use(String databaseName, String schemaName,String user) {
    Mono<String> just = Mono.just("");
    A a = new A();
    a.r2dbcRouterBuilder=R2dbcRouterBuilder.of(databaseName, schemaName,user);
    return a;
  }

  public static A useR2dbcRouter() {

    return new A();
  }

  public static class A {
    R2dbcRouterBuilder r2dbcRouterBuilder;
    public <T extends CorePublisher> T exec(Supplier<T> supplier){
      T t = supplier.get();
      Class<T> returnClass= (Class<T>) t.getClass();
      return r2dbcRoutingConnectionFactory.loadDataSource(t, r2dbcRouterBuilder);
    }
  }
}
