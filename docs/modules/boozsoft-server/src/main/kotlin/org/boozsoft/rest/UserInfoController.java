//package org.boozsoft.rest;
//
//import com.alibaba.fastjson.JSON;
//import org.boozsoft.domain.vo.RoleDTO;
//import org.boozsoft.repo.entity.Role;
//import org.springbooz.core.tool.result.R;
//import org.springbooz.security.oidc.client.domain.BoozOidcUser;
//import org.springbooz.security.oidc.client.holder.BoozOidcUserHolder;
//import org.springbooz.webflux.holder.BoozContextHolder;
//import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
//import org.springframework.http.HttpCookie;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseCookie;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.net.URI;
//import java.net.URLDecoder;
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.stream.Collectors;
//
///**
// * @author 木子桉易洋
// * @version 1.0
// * @company 财税达软件科技
// * @date 2021/1/21 10:47 上午
// */
//@RestController
//public class UserInfoController {
//  @GetMapping("/oauthlogin")
//  public Mono<Object> a(ServerWebExchange exchange, String redirectUri) {
//    // 从资源服务器获取用户信息
//    Mono<Object> adas = exchange.getSession().map(a -> {
//      ServerHttpResponse response = exchange.getResponse();
//      MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();
//      response.setStatusCode(HttpStatus.FOUND);
//      response.addCookie(ResponseCookie.from("SESSION", cookies.get("SESSION").get(0).getValue()).build());
////      response.addCookie(ResponseCookie.from("Idea-3369909d", cookies.get("Idea-3369909d").get(0).getValue()).build());
//      response.getHeaders().setLocation(URI.create(URLDecoder.decode(redirectUri)));
//      return a;
//    }).flatMap(a -> {
//      return ReactiveSecurityContextHolder.getContext().map(b -> {
//        return b;
//      });
//    });
//    // 重定向到vue
////        return "asdasdas";
//    return adas;
//  }
//
//  public List<Map<String, String>> authToRoleList(Collection<? extends GrantedAuthority> authorities) {
//    return authorities.stream().map(grantedAuthority -> {
//          String roleId = grantedAuthority.getAuthority().replace("ROLE_", "");
//          return Map.of(
//              "roleId", roleId,
//              "roleName", "匿名角色"
//          );
//        }
//    ).collect(Collectors.toList());
//  }
//
//  @GetMapping("/setCurrentSchema")
//  public Mono<R> setCurrentSchema() {
//    return BoozContextHolder.getSession()
//        .map(session -> {
//          session.getAttributes().put("currentSchema", "bjxgkj-001");
//          return session.getAttributes();
//        })
//
//        .map(sessionMap -> R.ok().setResult(sessionMap.get("currentSchema")));
//  }
//
//  public Mono<Map<String, Object>> setSystemUser(SystemUser user) {
//    return BoozContextHolder
//        .getSession()
//        .map(session -> session.getAttributes())
//        .doOnNext(sessionMap -> sessionMap.put("system_user", user));
//  }
//
//  public List<RoleDTO> toRoleDtoSet(Set<Role> roleSet) {
//    return roleSet.stream()
//        .map(role -> RoleDTO.from(role)).collect(Collectors.toList());
//  }
//
////  public Map getUserInfo(DefaultOidcUser oidcUser) {
////    Map userInfo = JSON.parseObject(JSON.toJSONString(oidcUser.getClaims().get("prod_userinfo")), Map.class);
////    userInfo.put("roleList", ((JSONArray) userInfo.get("roleList")).stream().collect(Collectors.toSet()));
////    userInfo.put("userId", userInfo.remove("id"));
////
////    JSONObject idToken = (JSONObject) userInfo.remove("idToken");
////    userInfo.put("token", Map.of("value", idToken.get("tokenValue")));
////    return userInfo;
////  }
//
//  public SystemUser userInfoToSystemUser(Map userInfo) {
//    SystemUser systemUser = new SystemUser();
//
//    systemUser.setId((String) userInfo.get("id"));
//    systemUser.setUsername((String) userInfo.get("username"));
//    systemUser.setName((String) userInfo.get("name"));
//    systemUser.setDeptId((String) userInfo.get("deptId"));
//    systemUser.setIdToken((String) userInfo.get("idToken"));
//    systemUser.setPassword((String) userInfo.get("password"));
//    systemUser.setPhone((String) userInfo.get("phone"));
//    systemUser.setRealName((String) userInfo.get("realName"));
//    systemUser.setRoleList((String) userInfo.get("roleList"));
//
//    return systemUser;
//  }
//
//  //    @PreAuthorize("permiss('aaa')")
//  @GetMapping("/userinfo")
////    public Mono<R> userinfo(OAuth2AuthenticationToken oAuth2AuthenticationToken, @RegisteredOAuth2AuthorizedClient OAuth2AuthorizedClient authorizedClient,
////                            @AuthenticationPrincipal DefaultOidcUser oauth2User, Principal principal, Authentication authentication, ServerWebExchange exchange, String redirectUri) {
//  public Mono<R> userinfo(@AuthenticationPrincipal BoozOidcUser oauth2User, ServerWebExchange exchange) {
//    return Mono
//        .zip(
//            BoozOidcUserHolder.getSystemUser(),
//            BoozOidcUserHolder.getSystemRole(),
//            BoozOidcUserHolder.getSystemJiGou()
//        )
//        .map(tuple ->
//            {
//              List roleList = tuple.getT2().stream().map(item -> {
//                Map map = JSON.parseObject(JSON.toJSONString(item), Map.class);
//                map.put("id", map.get("id").toString());
//                return map;
//              }).collect(Collectors.toList());
//              return Map.of(
//                  "userInfo", tuple.getT1(),
//                  "roleList", roleList,
//                  "jigouList", tuple.getT3()
//              );
//
//            }
//        )
//        .map(context -> R.ok().setResult(context));
//  }
//}
