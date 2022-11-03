package org.boozsoft.repo;


import org.boozsoft.repo.entity.Menu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Collection;

public interface MenuRepository extends ReactiveCrudRepository<Menu,String> {

   Flux<Menu> findAllBy();
   Flux<Menu> findAllByIdInAndCategoryOrderBySortNoAscIdAsc(Collection idsFlux, Integer category);
   @Query("select * from _app_group_sys_menu where platform_id is null")
   Flux<Menu> findPlatformAll();
   Flux<Menu> findAllByPlatformId(String platformId);
}
