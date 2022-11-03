package org.boozsoft.resource;

import com.alibaba.fastjson.JSON;
import org.boozsoft.api.ResourceGetClient;
import org.boozsoft.api.SsoUserInfoClient;
import org.boozsoft.config.oauth2.BoozTokenUser;
import org.boozsoft.config.oauth2.LoadUserApi;
import org.boozsoft.config.oauth2.Permission;
import org.boozsoft.domain.entity.User;
import org.boozsoft.repo.*;
import org.boozsoft.repo.entity.Menu;
import org.boozsoft.repo.entity.Role;
import org.boozsoft.repo.entity.RoleMenu;
import org.boozsoft.repo.entity.UserRole;
//import org.springbooz.security.oidc.client.config.service.LoadUserApi;
//import org.springbooz.security.oidc.client.token.BoozTokenUser;
//import org.springbooz.security.oidc.client.token.Permission;
import org.boozsoft.utils.CollectOfUtils;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
//import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.util.Map;

import static org.springbooz.webflux.holder.ReactiveContextHolder.Exchange_CONTEXT_KEY;

@Component
public class TokenResource {
    static WebClient webClient = WebClient.create();
    @Autowired
    public ResourceGetClient a;
    @Value("${resourcesPath}")
    private String resourcesPath;

    //    private static String resourcePath="http://account.chinabooz.com:9090";
    public Mono<String> getToken() {
        return Mono.subscriberContext()
                .map(ctx -> (ServerWebExchange) ctx.getOrEmpty(Exchange_CONTEXT_KEY).get())
                 .map(exchange -> exchange.getRequest().getHeaders().get("authorization").get(0));
    }

    public Mono<Object> resourceGet(String key) {
        return a.stateData(key).map(item->item);
    }

    public Mono<Map> resourcePut(String key, String value) {
        BodyInserters.FormInserter<String> body = BodyInserters
                .fromFormData("key", key)
                .with("value", value);
        return getToken()
                .flatMap(token -> {
                    return webClient.put()
                            .uri(resourcesPath + "/state/data")
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                            .headers((headers) -> headers
                                    .setBearerAuth(token)
                            )
                            .body(body)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .map(item -> {
                                return CollectOfUtils.mapof(
                                        "key", "value"
                                );
                            });
                });
    }

    public Mono<String> getTokenOpenId() {
        return getTokenUser().map(BoozTokenUser::getOpenId);
    }

    public Mono<BoozTokenUser> getTokenUser() {

        return getToken().flatMap(tokenValue -> {
            if (tokenValue.equals("mock token")) {

                String tokenUserJson = "{\"sub\":\"131221213123123121233123\",\"code\":\"超级管理员\",\"openid\":\"131221213123123121233123\",\"sex\":-1,\"deptId\":\"1123598813738675201,1123598813738675202,1123598813738675203\",\"updateUser\":\"1123598821738675201\",\"updateTime\":{\"dayOfWeek\":\"FRIDAY\",\"hour\":13,\"month\":\"DECEMBER\",\"year\":2020,\"dayOfMonth\":18,\"dayOfYear\":353,\"monthValue\":12,\"nano\":0,\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"},\"minute\":50,\"second\":51},\"avatar\":\"\",\"postId\":\"1123598817738675201\",\"realName\":\"超级管理员\",\"password\":\"1\",\"isDeleted\":0,\"phone\":\"17180100969\",\"createTime\":{\"dayOfWeek\":\"TUESDAY\",\"hour\":17,\"month\":\"DECEMBER\",\"year\":2020,\"dayOfMonth\":15,\"dayOfYear\":350,\"monthValue\":12,\"nano\":0,\"chronology\":{\"id\":\"ISO\",\"calendarType\":\"iso8601\"},\"minute\":23,\"second\":32},\"name\":\"超级管理员\",\"tenantId\":\"000000\",\"createUser\":\"1123598821738675201\",\"id\":10000032,\"email\":\"\",\"username\":\"admin\",\"status\":1}";
                return Mono.just(tokenUserJson);
            } else {
                return resourceGet("user")
                        .map(JSON::toJSONString);
            }
        })
                .map(userJson -> JSON.parseObject(userJson, BoozTokenUser.class));
    }

    @Autowired
    LoadUserApi loadUserApi;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRoleRepository userRoleRepository;

    public Mono<SystemUser> getSystemUser() {
        return loadUserApi.loadUser();
    }

//    public Mono<Object> commitRoleList(Map roleList) {
//        return resourcePut("roleList", JSON.toJSONString(roleList)).map(item -> item.get("key"));
//    }

    public Flux<Role> getUserRoleList() {
        Mono<String> userIdMono = getSystemUser().map(SystemUser::getId);
        Flux<String> roleIdsMono = userIdMono
                .flatMapMany(userId -> userRoleRepository.findByUserId(userId)
                        .map(UserRole::getRoleId));
        return roleRepository
                .findAllById(roleIdsMono)
                .filter(item -> item.getIsDeleted() == 0);

    }

    public Flux<Permission> getPermission() {
        return getSystemUser().flatMapMany(item -> loadUserApi.loadRoles(item.getId())).collectList().flatMapMany(item -> loadUserApi.loadPermissions(item));
    }

    @Autowired
    public MenuRepository menuRepository;
    @Autowired
    public UserRepository repository;
    @Autowired
    public RoleMenuRepository roleMenuRepository;
//    @Deprecated
//    public Flux<Menu> getLayouts() {
//        return null;
////        Mono<String> userIdMono = getSystemUser().map(SystemUser::getId).cache();
////        Flux<String> roleIdsMono = userIdMono
////                .flatMapMany(userId -> userRoleRepository.findByUserId(userId)
////                        .map(UserRole::getRoleId)).cache();
////        Flux<Role> rolesMono = roleRepository
////                .findAllById(roleIdsMono)
////                .filter(item -> item.getIsDeleted() == 0).cache();
////        Flux<RoleMenu> roleLayoutFlux = rolesMono.collectList()
////                .flatMapMany(item -> roleMenuRepository.findAllByRoleIdInOrderBySortNoAscMenuIdAsc(item))
////            .collectList().map(item->item).flatMapMany(Flux::fromIterable)
////                .filter(item -> item.getLayoutId() .equals( "0")).cache();
////        Flux<String> layoutIdFlux = roleLayoutFlux
////            .map(RoleMenu::getMenuId)
////            .collectList()
////            .map(item->item)
////            .flatMapMany(Flux::fromIterable);
////        return menuRepository.findAllById(layoutIdFlux).collectList()
////            .map(Flux::fromIterable)
////            .flatMapMany(item ->
////                roleLayoutFlux.map(RoleMenu::getMenuId)
////                    .flatMap(item2 -> item
////                        .filter(itemx -> item2.equals(itemx.getId()))
////                        .next()
////                    )
////            ).map(item->item);
////        return getSystemUser().flatMapMany(item->loadUserApi.loadRoles(item.getId())).collectList().flatMapMany(item->loadUserApi.loadMenu(item));
//    }

//    public Flux<Menu> getMenu(String platformId) {
//
//        Mono<String> userIdMono = getSystemUser().map(SystemUser::getId);
//        Flux<String> roleIdsMono = userIdMono
//                .flatMapMany(userId -> userRoleRepository.findByUserId(userId)
//                        .map(UserRole::getRoleId));
//        Flux<Role> rolesMono = roleRepository
//                .findAllById(roleIdsMono)
//                .filter(item -> item.getIsDeleted() == 0);
//        Flux<RoleMenu> roleMenuFlux = rolesMono.collectList().flatMapMany(item -> roleMenuRepository.findAllByRoleIdInAndLayoutIdOrderBySortNoAscMenuIdAsc(item,platformId));
//        return menuRepository.findAllById(roleMenuFlux.map(RoleMenu::getMenuId)).collectList().map(item->item).flatMapMany(Flux::fromIterable);
////        return getSystemUser().flatMapMany(item->loadUserApi.loadRoles(item.getId())).collectList().flatMapMany(item->loadUserApi.loadMenu(item));
//    }

//    public void getTokenDate() {
//
//    }
//
//    public void getDatasource() {
//
//    }
}
