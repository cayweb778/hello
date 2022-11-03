package org.springbooz.webflux.holder.context.security.domain.entity;

import lombok.Data;

@Data
public class SystemJigou {
  String id;
      String tenantId;
  String parentId;
  String deptName;
  String deptType;
  String fullName;
  String sort;
  String remark;
  Integer isDeleted;
}
