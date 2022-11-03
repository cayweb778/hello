package org.springbooz.webflux.holder.filter;

import org.springbooz.webflux.holder.ReactiveContextHolder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveContextFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Mono<WebSession> session = exchange.getSession();

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        return chain.filter(exchange)
                .subscriberContext(ctx -> ctx.put(ReactiveContextHolder.Exchange_CONTEXT_KEY,exchange))
                .subscriberContext(ctx -> ctx.put(ReactiveContextHolder.REQUEST_CONTEXT_KEY, request))
                .subscriberContext(ctx -> ctx.put(ReactiveContextHolder.RESPONSE_CONTEXT_KEY, response))
                .subscriberContext(ctx -> ctx.put(ReactiveContextHolder.SESSION_CONTEXT_KEY, session));
    }
}


