package org.springbooz.webflux.holder.context.security.resolver;


import org.springbooz.webflux.BoozContext;
import org.springbooz.webflux.holder.BoozContextHolder;
import org.springbooz.webflux.holder.context.ContextFrom;
import org.springbooz.webflux.holder.context.security.domain.entity.BoozContextInfo;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemDatasource;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUserPermission;
import org.springframework.core.MethodParameter;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 实现@BoozContext的参数解析器
 * Created by caidapao on 2018/3/30
 * Time 22:02
 */
public class BoozContextMethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(BoozContext.class);
  }

  @Override
  public Mono<Object> resolveArgument(MethodParameter parameter, BindingContext bindingContext, ServerWebExchange exchange) {
    BoozContextInfo boozContextInfo = new BoozContextInfo();

    String from = "session";

    if (from.equals(ContextFrom.SESSION)) {
      return BoozContextHolder
          .getSession()
          .map(session -> session.getAttributes())
          .doOnNext(sessionMap -> boozContextInfo.setUser((SystemUser) sessionMap.get("system_user")))
          .doOnNext(sessionMap -> boozContextInfo.setDatasource((SystemDatasource) sessionMap.get("system_datasource")))
          .doOnNext(sessionMap -> boozContextInfo.setPermission((SystemUserPermission) sessionMap.get("system_permission")))
          .map(item -> boozContextInfo);
    }

    if (from.equals(ContextFrom.REDIS)) {

    }

    if (from.equals(ContextFrom.DATABASE)) {

    }


    throw new RuntimeException("来源错误");
  }
}
