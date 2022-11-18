package org.boozsoft.config.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
@Configuration
public class CorsConfig {
  @Bean
  @Order(-20000)
  public WebFilter corsFilter() {
    return (ServerWebExchange ctx, WebFilterChain chain) -> {


      ServerHttpRequest req = ctx.getRequest();
      ServerHttpResponse rep = ctx.getResponse();

      //设置允许跨域的配置
      // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
//      rep.getHeaders().add("Access-Control-Allow-Origin", "*");
      // 允许的访问方法
      rep.getHeaders().add("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE, PATCH");
      // Access-Control-Max-Age 用于 CORS 相关配置的缓存
      rep.getHeaders().add("Access-Control-Max-Age", "3600");
      rep.getHeaders().add("Access-Control-Allow-Headers", "*");


      String method = req.getMethodValue();
      if (method.equals("OPTIONS")) {
        rep.setStatusCode(HttpStatus.OK);
        return Mono.empty();
      }
      return chain.filter(ctx);


    };
  }
}
