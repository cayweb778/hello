package org.boozsoft.repo;


import org.boozsoft.repo.entity.RoleMenu;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

public interface RoleMenuRepository extends ReactiveCrudRepository<RoleMenu,String> {
   //   @CreatedBy
//   Flux<RoleMenu> findAllByRoleIdInAndLayoutIdOrderBySortNo(Collection roleIds,Integer layoutId);
//   Flux<RoleMenu> findAllByRoleIdInOrderBySortNoAscMenuIdAsc(Collection roleIds);
//   Flux<RoleMenu> findAllByRoleIdInAndLayoutIdOrderBySortNoAscMenuIdAsc(Collection roleIds,String platformId);
   Flux<RoleMenu> findAllByMenuIdIn(List<String> menuIds);
   Flux<RoleMenu> findAllByRoleIdIn(List<String> menuIds);
   Mono<Void> deleteAllByRoleIdAndMenuIdIn(String roleId, List<String> menuIds);
   Mono<Void> deleteAllByRoleIdAndMenuIdNotIn(String roleId, List<String> menuIds);
}
