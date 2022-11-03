package org.springbooz.webflux.holder;

import org.springbooz.webflux.holder.context.security.domain.entity.SystemDatasource;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUserPermission;
import reactor.core.publisher.Mono;

public class BoozContextHolder extends ReactiveContextHolder {
  public Mono<SystemDatasource> getSystemDatasource() {
    return getSession()
        .map(item -> new SystemDatasource());
  }

  public Mono<SystemUserPermission> getSystemPermission() {
    return getSession()
        .map(item -> new SystemUserPermission());
  }

  public Mono<SystemUser> getSystemUser() {
    return getSession()
        .map(item -> new SystemUser());
  }
}
