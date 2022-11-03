package org.springbooz.webflux.holder.context.security.domain.entity;

import lombok.Data;
import org.springbooz.webflux.holder.context.security.domain.entity.permission.BoozLayout;

import java.util.Set;

@Data
public class SystemUserPermission {
  public BoozLayout currentLayout;
  Set<String> permission;
}
