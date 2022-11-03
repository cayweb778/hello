package org.boozsoft.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLDecoder;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/3/22 4:33 下午
 */
@Controller
public class LogOutController {
    @GetMapping("/logout")
    @ResponseBody
    public Mono<WebSession> logout(ServerWebExchange exchange, String redirectUri){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().setLocation(URI.create("/oauthlogin?"+ URLDecoder.decode(redirectUri)));
        return exchange.getSession().doOnNext(webSession -> webSession.getAttributes().clear());
    }
}
