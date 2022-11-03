package org.springbooz.webflux.holder.context.security.domain.entity;

import lombok.Data;

@Data
public class SystemRole {
  String id;
  Integer isDeleted;
  String parentId;
  String roleAlias;
  String roleName;
  String sort;
  String tenantId;
}
