package org.springbooz.webflux.holder.context.security.commit;

import org.springbooz.webflux.holder.ReactiveContextHolder;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemDatasource;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUser;
import org.springbooz.webflux.holder.context.security.domain.entity.SystemUserPermission;

public class BoozContextCommit {
  public void getSessionMap(){

  }
  public static void commitUser(SystemUser systemUser){
    ReactiveContextHolder
        .getSessionMap()
        .map(sessionMap->sessionMap.put("system_user",systemUser));
  }
  public static void commitDataSource(SystemDatasource systemDatasource){
    ReactiveContextHolder
        .getSession()
        .map(session -> session.getAttributes())
        .map(sessionMap->sessionMap.put("system_datasource",systemDatasource));
  }

  public static void commitDataSource(SystemUserPermission systemUserPermission){
    ReactiveContextHolder
        .getSession()
        .map(session -> session.getAttributes())
        .map(sessionMap->sessionMap.put("system_datasource",systemUserPermission));
  }
}
