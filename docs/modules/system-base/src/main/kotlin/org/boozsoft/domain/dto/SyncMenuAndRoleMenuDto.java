package org.boozsoft.domain.dto;

import lombok.Data;
import lombok.Setter;
import org.boozsoft.repo.entity.Menu;
import org.boozsoft.repo.entity.RoleMenu;
import reactor.core.publisher.Flux;

import java.util.List;
@Setter
public class SyncMenuAndRoleMenuDto {
  List<Menu> menuList;
  List<RoleMenu> roleMenuList;

  public Flux<Menu> getMenuFlux() {
    return Flux.fromIterable(menuList);
  }

  public Flux<RoleMenu> getRoleMenuFlux() {
    return Flux.fromIterable(roleMenuList);
  }
}
