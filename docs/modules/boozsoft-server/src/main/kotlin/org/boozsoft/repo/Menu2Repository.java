//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.boozsoft.repo;

import java.util.Collection;
import org.boozsoft.repo.entity.Menu;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface Menu2Repository extends ReactiveCrudRepository<Menu, String> {
  Flux<Menu> findAllBy();

  Flux<Menu> findAllByIdInAndCategoryOrderBySortNoAscIdAsc(Collection var1, Integer var2);

  @Query("select * from _app_group_sys_menu where cast(category as varchar(3))  like '10_'")
  Flux<Menu> findPlatforms();


}
