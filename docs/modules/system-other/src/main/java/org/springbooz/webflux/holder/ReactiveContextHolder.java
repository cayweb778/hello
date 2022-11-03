package org.springbooz.webflux.holder;

import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.util.Map;

public class ReactiveContextHolder {
    public static final Class<ServerHttpRequest> REQUEST_CONTEXT_KEY = ServerHttpRequest.class;
    public static final Class<ServerHttpResponse> RESPONSE_CONTEXT_KEY = ServerHttpResponse.class;
    public static final Class<WebSession> SESSION_CONTEXT_KEY = WebSession.class;
    public static final Class<ServerWebExchange> Exchange_CONTEXT_KEY = ServerWebExchange.class;

    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext()
                .map(ctx -> ctx.get(REQUEST_CONTEXT_KEY));
    }
    public static Mono<ServerHttpResponse> getResponse() {
        return Mono.subscriberContext()
            .map(ctx -> ctx.get(RESPONSE_CONTEXT_KEY));
    }

    public static Mono<WebSession> getSession() {
        return Mono.subscriberContext()
            .flatMap(ctx -> (Mono<WebSession>)ctx.getOrEmpty(SESSION_CONTEXT_KEY).get());
    }
    public static Mono<Map<String, Object>> getSessionMap() {
      return getSession().map(session -> session.getAttributes());
    }

    public static Mono<ServerWebExchange> getExchange() {
        return Mono.subscriberContext()
            .map(ctx -> ctx.get(Exchange_CONTEXT_KEY));
    }
}
