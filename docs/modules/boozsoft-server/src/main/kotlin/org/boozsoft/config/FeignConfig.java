package org.boozsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.function.Function;


/**
 * Feign配置
 * 使用FeignClient进行服务间调用，传递headers信息
 */
@Configuration
public class FeignConfig implements ReactiveHttpRequestInterceptor {


  @Override
  public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest reactiveHttpRequest) {
    return Mono.just("")
        .transformDeferredContextual((c,a)->{
          ServerWebExchange serverWebExchange = a.get(ServerWebExchange.class);
          HttpHeaders headers = serverWebExchange.getRequest().getHeaders();

          String authorization="Bearer "+headers.get("Authorization").get(0);
          reactiveHttpRequest.headers().put("Authorization", Collections.singletonList(authorization));
          reactiveHttpRequest.headers().put(HttpHeaders.ACCEPT, Collections.singletonList(MediaType.APPLICATION_JSON_VALUE));
          System.out.println(c);
          return c;
        }).map(item->reactiveHttpRequest);
  }

  @Override
  public <V> Function<V, Mono<ReactiveHttpRequest>> compose(Function<? super V, ? extends ReactiveHttpRequest> before) {
    return ReactiveHttpRequestInterceptor.super.compose(before);
  }

  @Override
  public <V> Function<ReactiveHttpRequest, V> andThen(Function<? super Mono<ReactiveHttpRequest>, ? extends V> after) {
    return ReactiveHttpRequestInterceptor.super.andThen(after);
  }
}
