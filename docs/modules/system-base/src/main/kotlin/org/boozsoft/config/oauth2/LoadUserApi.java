package org.boozsoft.config.oauth2;

import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * @author 木子桉易洋
 * @version 1.0
 * @company 财税达软件科技
 * @date 2021/1/21 11:00 上午
 */
public interface LoadUserApi {
  Mono<Map> saveSecurtyStateMap(Map map);
  Mono<Map> getSecurtyStateMap();
  Mono<SystemUser> loadUser();
  Flux<Role> loadRoles(String userId);
  Flux<Permission> loadPermissions(List<Role> roleList);
}
