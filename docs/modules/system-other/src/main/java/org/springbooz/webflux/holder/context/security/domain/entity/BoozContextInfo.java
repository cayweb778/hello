package org.springbooz.webflux.holder.context.security.domain.entity;

import lombok.Data;

@Data
public class BoozContextInfo {
  public SystemUser user;
  public SystemDatasource datasource;
  public SystemUserPermission permission;
}
