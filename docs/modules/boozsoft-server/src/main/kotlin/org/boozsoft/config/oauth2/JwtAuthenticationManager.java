package org.boozsoft.config.oauth2;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;

/**
 * @author: ffzs
 * @Date: 2020/8/16 下午6:18
 */

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {
    public final R2dbcEntityTemplate entityTemplate;

    @Autowired
    LoadUserApi loadUserApi;
//    @Autowired
//    TokenResource tokenResource;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return loadUserApi
                .getSecurtyStateMap()
                // 为空，token获取用户
                .switchIfEmpty(
                    Mono.just("").flatMap(item->getState())
                        .flatMap(item->loadUserApi.saveSecurtyStateMap(item))
                )
                .flatMap(this::createAuthentication);
    }

    public Mono<BoozTokenUserAuthentication> createAuthentication(Map stateMap) {

        BoozTokenUserAuthentication boozTokenUserAuthentication = new BoozTokenUserAuthentication((Collection<? extends GrantedAuthority>) stateMap.get("permission"));
        boozTokenUserAuthentication.setAuthenticated(true);
        boozTokenUserAuthentication.setDetails((SystemUser) stateMap.get("user"));
        return Mono.just(boozTokenUserAuthentication).subscriberContext(ctx->{
//            ServerWebExchange.
            ServerWebExchange serverWebExchange = ctx.get(ServerWebExchange.class);
            serverWebExchange.getAttributes().put("stateMap",stateMap);
            return ctx;
        });
    }

    public Mono<HashMap<String, Object>> getState(){
        HashMap<String, Object> stateMap = new HashMap<String, Object>();
        // 用户
        Mono<SystemUser> systemUserMono = Mono.just("").flatMap(item -> loadUserApi.loadUser()
                .map(item2 -> {
                    stateMap.put("user", item2);
                    return item2;
                }));
        // 角色
        Mono<List<Role>> rolesMono = systemUserMono
                .flatMapMany(item -> loadUserApi.loadRoles(item.getId()))
                .collectList().map(item -> {
                    stateMap.put("roles", item);
                    return item;
                });

//        Mono<List<Permission>> permissionMono = rolesMono
//                .flatMapMany(roles -> loadUserApi.loadPermissions(roles))
//                .collectList()
//                .map(item -> {
//                    stateMap.put("permissions", item);
//                    return item;
//                });
        return rolesMono.doOnNext(item->stateMap.put("permissions", new ArrayList())).map(item->stateMap);

    }


}
